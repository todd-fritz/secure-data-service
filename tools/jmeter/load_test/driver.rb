#!/usr/bin/env ruby
require 'json'

require_relative 'lib/load_test'

class Driver

  JMETER_HOME_ERROR = <<END
Please set environment variable JMETER_HOME to your JMeter root. For example,
  export JMETER_HOME="/opt/apache-jmeter"
END

  def initialize
    check_jmeter_home
    config_name = ARGV[0]
    config = LoadTest::Config::CONFIGS[config_name.to_sym] unless config_name.nil?
    if config.nil?
      puts "#{$0} [#{LoadTest::Config::CONFIGS.collect {|name, config| name.to_s}.join(',')}]"
    else
      command = ARGV[1]
      command_to_files_map = get_command_to_files_map(config)
      files = command_to_files_map[command]
      if files.nil?
        puts "#{$0} #{config_name} [#{command_to_files_map.collect{|command, files| command.to_s}.join(',')}]"
      else
        time = Time.now.strftime("%Y%m%d_%H%M%S")
        run_root = File.join(config[:data_root], "#{config_name}_#{time}")
        jmeter_runner = LoadTest::JmeterRunner.new(@jmeter_exec, config)

        if(config[:test_run])
          puts "Doing a test run"
          test_run_root = File.join(run_root, "test_run")
          success = true
          files.each{|result_dir, file|
            test_run_full_path = File.join(test_run_root, result_dir)
            success = jmeter_runner.collect_all_data(result_dir, file, test_run_full_path, [1])
            if !success
              puts "#{result_dir} failed during test run"
              break
            end
          }
          FileUtils.rm_rf(test_run_root)
          if !success
            return
          end
        end

        puts "Saving config to #{run_root}/config.json"
        File.open("#{run_root}/config.json", "w") do |f|
          f.write(config.to_json)
        end

        files.each{|result_dir, file|
          full_path = File.join(run_root, result_dir)
          jmeter_runner.collect_all_data(result_dir, file, full_path, config[:thread_count_array])
        }

        puts "Result is saved to #{run_root}"

        File.open("#{run_root}/result.json", "w") do |f|
          jmeter_result_processor = LoadTest::JmeterResultProcessor.new({:result_dir => run_root})
          f.write(jmeter_result_processor.aggregate_all_result().to_json)
        end
      end
    end
  end

  private
  def check_jmeter_home
    if ENV["JMETER_HOME"].nil?
      raise JMETER_HOME_ERROR
    else
      @jmeter_exec = "#{ENV["JMETER_HOME"]}/bin/jmeter"
    end
  end

  def get_command_to_files_map(config)
    file_map = {}
    all_files = {}
    Dir.foreach(config[:config_root]) do |filename|
      if match_index = filename =~ /[.]jmx$/
        unless config[:ignore].include? filename
          file = File.join(config[:config_root], filename)
          result_dir = filename[0..(match_index - 1)]
          file_map[result_dir] = {result_dir => file}
          all_files[result_dir] = file
        end
      end
    end
    file_map["all"] = all_files
    file_map
  end
end

driver = Driver.new
