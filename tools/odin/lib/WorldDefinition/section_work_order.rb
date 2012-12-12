=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

class SectionWorkOrderFactory

  def initialize(world, scenario)
    @world = world
    @next_id = -1
    @scenario = scenario
  end

  def gen_sections(ed_org, ed_org_type, yielder)
    school_id = DataUtility.get_school_id(ed_org['id'], ed_org_type.to_sym)
    unless ed_org['students'].nil?
      ed_org['students'].each{|year, student_map|
        unless ed_org['offerings'].nil?
          student_map.each{|grade, num|
            sections_from_edorg(ed_org, ed_org_type, year, grade).each{|offering, sections|
              sections.each{|section_id|
                yielder.yield({:type=>Section, :id=>section_id, :edOrg=>school_id, :offering=>offering})
              }
            }
          }
        end
      }
    end
  end

  def sections(id, ed_org_type, year, grade)
    if (@world.nil? == false && @world[ed_org_type].nil? == false)
      ed_org = @world[ed_org_type].find{|s| s['id'] = id}
      sections_from_edorg(ed_org, ed_org_type, year, grade)
    end
  end

  def sections_from_edorg(ed_org, ed_org_type, year, grade)
    section_map = {}
    if (ed_org['offerings'].nil? == false && ed_org['offerings'][year].nil? == false)
      offerings = ed_org['offerings'][year].select{|c| c['grade'] == grade}
      offerings.each{|course|
        find_sections(ed_org, ed_org_type, year, grade, course).each{|section|
          section_map[course] = find_sections(ed_org, ed_org_type, year, grade, course)
        }
      }
    end
    section_map
  end

  private

  def find_sections(ed_org, ed_org_type, year, grade, course)
    student_count = ed_org['students'][year][grade]
    section_count = (student_count.to_f / students_per_section(ed_org_type)).ceil
    (1..section_count)
    end

  def students_per_section(type)
    @scenario['STUDENTS_PER_SECTION'][type]
  end

end
