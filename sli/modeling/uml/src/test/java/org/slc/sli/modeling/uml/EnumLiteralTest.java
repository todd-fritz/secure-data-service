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
package org.slc.sli.modeling.uml;


import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.uml.index.DefaultVisitor;
import org.slc.sli.modeling.uml.utils.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test for EnumLiteral
 * @author chung
 */
public class EnumLiteralTest {

    private EnumLiteral enumLiteral;
    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        enumLiteral = new EnumLiteral(Identifier.fromString("1234"), "TestEnumLiteral", TestUtils.EMPTY_TAGGED_VALUES);
    }

    @Test
    public void testAccept() {
        enumLiteral.accept(visitor);
    }

    @Test
    public void testToString() {
        String string1 = enumLiteral.toString();
        String string2 = "{id: 1234, name: \"TestEnumLiteral\"}";
        assertEquals(string2, string1);
    }

}
