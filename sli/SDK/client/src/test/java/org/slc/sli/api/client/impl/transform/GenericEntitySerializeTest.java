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


package org.slc.sli.api.client.impl.transform;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slc.sli.api.client.impl.GenericEntity;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test GenericEntity serialization
 */
public class GenericEntitySerializeTest {
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testSerializeBasicEntity() throws IOException, JSONException {
        
        GenericEntity e = TestHelpers.createSimpleGenericEntity();
        
        String jsonString = mapper.writeValueAsString(e);
        
        assertNotNull(jsonString);
        JsonNode eNode = mapper.readTree(jsonString);

        JSONAssert.assertEquals(TestHelpers.SIMPLE_JSON_BODY.toString(), eNode.toString(), true);
    }
    
    @Test
    public void testComplexEntity() throws IOException, JSONException {
        
        GenericEntity e = TestHelpers.createComplexEntity();
        
        String jsonString = mapper.writeValueAsString(e);
        assertNotNull(jsonString);
        
        JsonNode eNode = mapper.readTree(jsonString);

        JSONAssert.assertEquals(TestHelpers.COMPLEX_JSON_BODY.toString(), eNode.toString(), true);
    }
}
