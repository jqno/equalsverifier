/*
 * Copyright 2017 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.internal.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

public class FieldNameExtractorTest {
    private static final String FIELD_NOT_FOUND = "field not found: ";

    private static final String FIELD_STRING = "fieldString";
    private static final String FIELD_OBJECT = "fieldObject";
    private static final String FIELD_LIST = "fieldList";
    private static final String FIELD_PRIMITIVE_INT = "fieldPrimitiveInt";


    @Test
    public void should_extractFields_succesfully() throws Exception {
        Set<String> fields = FieldNameExtractor.extractFieldNames(FieldNameExtractorTestHelper.class);

        assertTrue(FIELD_NOT_FOUND + FIELD_STRING, fields.contains(FIELD_STRING));
        assertTrue(FIELD_NOT_FOUND + FIELD_OBJECT, fields.contains(FIELD_OBJECT));
        assertTrue(FIELD_NOT_FOUND + FIELD_LIST, fields.contains(FIELD_LIST));
        assertTrue(FIELD_NOT_FOUND + FIELD_PRIMITIVE_INT, fields.contains(FIELD_PRIMITIVE_INT));

        assertTrue("Total number of fields was not equal to expected value", 4 == fields.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_disallow_adding_extra_fields() throws Exception {
        Set<String> fields = FieldNameExtractor.extractFieldNames(FieldNameExtractorTestHelper.class);

        fields.add("illegally added field");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_disallow_removing_fields() throws Exception {
        Set<String> fields = FieldNameExtractor.extractFieldNames(FieldNameExtractorTestHelper.class);

        fields.remove(FIELD_STRING);
    }

    class FieldNameExtractorTestHelper {
        public final String fieldString;
        protected int fieldPrimitiveInt;
        final List<Integer> fieldList;
        private final Object fieldObject;

        public FieldNameExtractorTestHelper(String fieldString, List<Integer> fieldList, Object fieldObject) {
            this.fieldString = fieldString;
            this.fieldList = fieldList;
            this.fieldObject = fieldObject;
        }

        public void setFieldPrimitiveInt(int newInt) {
            this.fieldPrimitiveInt = newInt;
        }
    }

}
