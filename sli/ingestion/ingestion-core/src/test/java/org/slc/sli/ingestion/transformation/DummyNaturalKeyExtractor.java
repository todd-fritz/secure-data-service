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
package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

/**
 * @author sashton
 */
public class DummyNaturalKeyExtractor implements INaturalKeyExtractor {
    
    @Override
    public Map<String, String> getNaturalKeys(Entity entity) {
        return new HashMap<String, String>();
    }
    
    @Override
    public Map<String, Boolean> getNaturalKeyFields(Entity entity) {
        return new HashMap<String, Boolean>();
    }
    
    @Override
    public NaturalKeyDescriptor getNaturalKeyDescriptor(Entity entity) {
        return new NaturalKeyDescriptor();
    }
    
}
