=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end
# Enable tailcall optimizations to reduce overall stack size.
RubyVM::InstructionSequence.compile_option = {
  :tailcall_optimization => true,
  :trace_instruction => true
}

require 'digest/md5'
require 'digest/sha1'
require 'logger'
require 'rexml/document'
require 'thwait'
require 'yaml'

require_relative 'WorldDefinition/pre_requisite_builder.rb'
require_relative 'WorldDefinition/world_builder.rb'
require_relative 'OutputGeneration/DataWriter.rb'
require_relative 'OutputGeneration/XmlDataWriter.rb'
require_relative 'OutputGeneration/XML/validator'
require_relative 'Shared/util'
require_relative 'Shared/demographics'
require_relative 'Shared/EntityClasses/student'

require_relative 'EntityCreation/entity_factory'
require_relative 'EntityCreation/work_order_queue'

# offline data integration nexus --> ODIN
class Odin
  def initialize
    $stdout.sync = true
    @log = Logger.new($stdout)
    @log.level = Logger::INFO
    @workOrderQueue = nil
    @entityQueue = nil
  end

  def generate( scenario )

    scenarioYAML, prng = getScenario(scenario)
    BaseEntity.initializeDemographics(scenarioYAML['demographics'], scenarioYAML['choices'])
    BaseEntity.set_scenario(scenarioYAML)

    clean

    output = scenarioYAML['DATA_OUTPUT']
    @log.info <<-out

      -------------------------------------------------------
      ODIN: Offline Data Interchange Nexus
      -------------------------------------------------------
      Simulation will use scenario: #{scenario}

    out

    if output == "xml"
      @log.info "XML output specified --> Generating ed-fi xml interchanges."
      writer = XmlDataWriter.new(scenarioYAML)
    elsif output == "api"
      @log.info "API output specified --> All data will be POSTed via API using host: <need hostname>"
      # will need to create oauth token for POSTing data via API
      # initialize api data writer
    else
      @log.info "No DATA_OUTPUT specified in scenario configuration --> Using in-memory store."
      writer = DataWriter.new
    end

    @entityQueue = EntityQueue.new
    @entityQueue.writer(writer)

    @workOrderQueue = WorkOrderQueue.new
    @workOrderQueue.factory(EntityFactory.new(prng), @entityQueue)

    start = Time.now
    
    # load pre-requisites for scenario (specified in yaml)
    pre_requisites = PreRequisiteBuilder.new(scenarioYAML)
    display_pre_requisites_before_world_building(pre_requisites.get)

    # create a snapshot of the world
    edOrgs = WorldBuilder.new(prng, scenarioYAML, @workOrderQueue, pre_requisites.get).build
    display_world_summary(edOrgs)
    display_pre_requisites_after_world_building(pre_requisites.get)

    writer.display_entity_counts

    File.open("#{scenarioYAML["DIRECTORY"] or 'generated/'}manifest.json", 'w'){ |f|
      writer.tracker.write_edfi_manifest(f)
    }

    # clean up writer
    # -> xml  data writer: writes any entities that are still queued and closes file handles
    # -> base data writer: clears maps of entities and counts
    writer.finalize

    finalTime = Time.now - start
    @log.info "Total generation time: #{finalTime} secs"

    genCtlFile
    genDataZip
  end

  # Display a brief summary of the world just created
  def display_world_summary(world)
    @log.info <<-out

      Summary of World:
        - state education agencies: #{world["seas"].size}
        - local education agencies: #{world["leas"].size}
        - elementary schools:       #{world["elementary"].size}
        - middle     schools:       #{world["middle"].size}
        - high       schools:       #{world["high"].size}

    out
  end

  # Display all pre-requisites before world building
  # -> all staff members (at relevant education organizations)
  # -> all teachers (at relevant education organizations)
  def display_pre_requisites_before_world_building(pre_requisites)
    @log.info "Pre-requisites for world building:"
    pre_requisites.each do |type,edOrgs|
      @log.info "#{type.inspect}:"
      edOrgs.each do |organization_id, members|
        @log.info "education organization: #{organization_id}"
        next if members["staff"].nil?
        members["staff"].each do |member|
          @log.info " -> staff unique state id: #{member[:staff_id]} (#{member[:name]}) has role: #{member[:role]}" if !member[:staff_id].nil?
        end
        next if members["students"].nil?
        members["students"].each do |member|
          @log.info " -> student unique state id: #{member[:student_id]} (#{member[:name]}) has role: #{member[:role]}" if !member[:student_id].nil?
        end
      end
    end
  end

  # Display the pre-requisites that were not met during world building
  def display_pre_requisites_after_world_building(pre_requisites)
    # used 'displayed_title' variable to only display the 'these still remain' message after world building
    # if there were entities specified in the 
    displayed_title = false
    pre_requisites.each do |type,edOrgs|
      if !edOrgs.nil? and edOrgs.size > 0
        if !displayed_title
          @log.info "After world building, these still remain (not created during world building):"
          displayed_title = true
        end
        @log.info "#{type.inspect}:"
        edOrgs.each do |organization_id, members|
          @log.info "education organization: #{organization_id}"         
          next if members["staff"].nil?
          members["staff"].each do |member|
            @log.info " -> staff unique state id: #{member[:staff_id]} (#{member[:name]}) with role: #{member[:role]}"
          end
          next if members["students"].nil?
          members["students"].each do |member|
            @log.info " -> staff unique state id: #{member[:student_id]} (#{member[:name]}) with role: #{member[:role]}"
          end          
        end
      end
    end
    if displayed_title
      @log.info <<-out

        To guarantee that all members of the staff catalog be created, It is recommended that you:
         -> increase the number of students in the scenario (property in yaml: STUDENT_COUNT)
         -> increase the number of years in the scenario (property in yaml: NUMBER_OF_YEARS)
         -> tune the cardinality of entities (there are several properties to control this)

      out
    end
  end

  def validate
    valid = true
    Dir["#{File.dirname(__FILE__)}/../generated/*.xml"].each { |f| valid = valid && validate_file(f) }
    valid
  end

  ## Clean the generated directory
  def clean
    Dir["#{File.dirname(__FILE__)}/../generated/*.xml"].each { |f| File::delete f }
  end

  # Generate a MD5 hash of the generated xml files.
  def md5
    hashes = []
    Dir["#{File.dirname(__FILE__)}/../generated/*.xml"].each { |f|
      hashes.push( Digest::MD5.hexdigest( f ))
    }
    Digest::MD5.hexdigest( hashes.to_s )
  end

end

# TODO: Place this in an appropriate class or module instead of defining on Kernel
# Load the scenario from YAML returning the YAML and a random-number generator
def getScenario(scenario_name)
  configYAML = YAML.load_file(File.join(File.dirname(__FILE__),'/../config.yml'))
  [load_scenario(scenario_name, configYAML), Random.new(configYAML['seed'])]
end
