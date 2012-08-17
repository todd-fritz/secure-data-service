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

package org.slc.sli.sif.translation;

import java.util.List;

import openadk.library.SIFDataObject;

import org.slc.sli.api.client.impl.GenericEntity;

/**
 *
 * Interface for translation of all or part of a SIFDataObject
 * to an SLI entity.
 *
 * @author jtully
 *
 * @param <T>, the SliEntity type that is returned.
 */
public interface TranslationTask {
    /*
     * Transform a SIF SifDataObject into an SLI entity
     */
     public List<GenericEntity> translate(final SIFDataObject sifData, String zoneId) throws SifTranslationException;
}
