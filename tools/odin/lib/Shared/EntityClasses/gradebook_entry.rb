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

require_relative "baseEntity.rb"

# creates grade book entry
class GradebookEntry < BaseEntity

  attr_accessor :type, :date_assigned
  
  def initialize(type, date_assigned, section, grading_period = nil)
    @type           = type
    @date_assigned  = date_assigned
    @section        = section
    @grading_period = grading_period
  end

  def section
    {:ed_org_id => "", :unique_section_code => ""}
  end

  def grading_period
    return nil if @grading_period.nil?
    {:type => GradingPeriodType.to_string(@grading_period['type']), :ed_org_id => "", :begin_date => ""}
  end
end