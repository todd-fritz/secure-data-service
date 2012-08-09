############################################################
# Manual tests start
############################################################

desc "Run Activemq Redundancy Test"
task :ingestionActivemqRedundancyTest do
  runTests("test/features/ingestion/features/ingestion_mqRedundancy.feature")
end

desc "Run Mongo Tracking Aspect Test"
task :ingestionMongoTrackingAspect do
  runTests("test/features/ingestion/features/ingestion_MongoTrackingAspect.feature")
end

############################################################
# Manual tests end
############################################################