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


package org.slc.sli.modeling.xmi.reader;

/**
 * Intentionally package-protected for use by the {@link XmiReader} only.
 */
final class XmiBadAttributeException extends RuntimeException {
    private static final long serialVersionUID = -4938178032912350951L;
    
    public XmiBadAttributeException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
