/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package mapred;

import java.util.Properties;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Aggregator;
import cascading.operation.Function;
import cascading.operation.aggregator.Count;
import cascading.operation.regex.RegexGenerator;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tuple.Fields;

public class BigDiffCascading {
    
    public void execute(String inputPath1, String inputPath2, String outputPath) {

        // define source and sink Taps.
        Scheme sourceScheme = new TextLine( new Fields( "line" ) );
        Tap lhSource = new Hfs( sourceScheme, inputPath1 );
        Tap rhSource = new Hfs( sourceScheme, inputPath2 );

        Scheme sinkScheme = new TextLine( new Fields( "source", "line" ) );
        Tap sink = new Hfs( sinkScheme, outputPath, SinkMode.REPLACE );

        /*
        Pipe lhs = new Pipe("lhs");
        lhs = new Each(lhs, new Fields("source", "line"), )
        
        String regex = "(left|right)[\\s]+(.*)";
        Function function = new RegexGenerator( new Fields( "source", "line" ), regex );
        assembly = new Each( assembly, new Fields( "line" ), function );

        // group the Tuple stream by the "word" value
        assembly = new GroupBy( assembly, new Fields( "source" ) );

        // For every Tuple group
        // count the number of occurrences of "word" and store result in
        // a field named "count"
        Aggregator count = new Count( new Fields( "count" ) );
        assembly = new Every( assembly, count );

        // initialize app properties, tell Hadoop which jar file to use
        Properties properties = new Properties();
        FlowConnector.setApplicationJarClass( properties, WordCountCascading.class );

        // plan a new Flow from the assembly using the source and sink Taps
        // with the above properties
        FlowConnector flowConnector = new FlowConnector( properties );
        Flow flow = flowConnector.connect( "word-count", source, sink, assembly );


        // execute the flow, block until complete
        flow.complete();    
        */    
    }
}
