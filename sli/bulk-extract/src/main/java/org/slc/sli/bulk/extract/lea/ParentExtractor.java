/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class ParentExtractor implements EntityExtract {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EdOrgExtractHelper edOrgExtractHelper;
    
    public ParentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    @Override
    public void extractEntities(EntityToEdOrgCache entityToEdorgCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.PARENT, this.getClass().getName());
        Iterator<Entity> parents = repo.findEach(EntityNames.PARENT, new NeutralQuery());
        Set<String> validParents = entityToEdorgCache.getEntityIds();
        while (parents.hasNext()) {
            Entity parent = parents.next();
            if (!validParents.contains(parent.getEntityId())) {
                continue;
            }
            for (String edOrg : entityToEdorgCache.getEntriesById(parent.getEntityId())) {
                extractor.extractEntity(parent, map.getExtractFileForEdOrg(edOrg), EntityNames.PARENT);
            }

        }
        
    }
    
}
