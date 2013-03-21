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

require 'mongo'
require 'stomp'
require 'json'
require_relative '../../../../search/step_definitions/search_indexer_steps.rb'

When /^I update the "(.*?)" with ID "(.*?)" field "(.*?)" to "(.*?)"$/ do |collection, id, field, value|
  conn = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
  midgar = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a"
  mdb = conn.db(midgar)

  coll = mdb[collection]
  entity = coll.find_one({"_id" => id})
  assert(entity, "cant find #{collection} with id #{id}")
  entry = entity
  subfields = field.split(".")
  last = subfields.pop
  subfields.each { |subfield|
    entry = entry[subfield]
  }
  entry[last] = value
  puts "saving entity: #{entity}"
  coll.save(entity)
end

When /^I send an update event to the search indexer for collection "(.*?)" and ID "(.*?)"$/ do |collection, id|
  midgar = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a"
  message = [{"ns" => "#{midgar}.#{collection}",
              "o" => { "$set" => { "type" => collection } },
              "o2" => { "_id" => id },
              "op" => "u"
             }]
  client = Stomp::Client.new
  client.publish("search", message.to_json)
end

Then /^I will EVENTUALLY GET "(.*?)" with (\d+) elements$/ do |query, count|
  success = false
  10.times {
    step "I navigate to GET \"#{query}\""
    if count.to_i == @result.size
      success = true
      break
    end
    sleep 1
  }
  assert(success, "expected #{count} elements but got back #{@result.size}")
end
