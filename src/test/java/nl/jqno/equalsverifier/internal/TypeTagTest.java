/*
 * Copyright 2015 Jan Ouwens
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
package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TypeTagTest {
    @SuppressWarnings("unused") private final String simpleField = null;
    @SuppressWarnings("unused") private final List<String> fieldWithSingleTypeParameter = null;
    @SuppressWarnings("unused") private final Map<String, Integer> fieldWithTwoTypeParameters = null;
    @SuppressWarnings("unused") private final Map<String, List<String>> fieldWithNestedTypeParameters = null;
    @SuppressWarnings("unused") private final Map<List<Integer>, Map<List<Double>, Map<String, Float>>> fieldWithRidiculousTypeParameters = null;

    @SuppressWarnings("unused") private final int primitiveField = 0;
    @SuppressWarnings("unused") private final String[] arrayField = null;

    private TypeTag expected;
    private TypeTag actual;

    @Test
    public void createSimpleTypeTagFromField() {
        expected = new TypeTag(String.class);
        actual = TypeTag.of(getField("simpleField"));
        assertEquals(expected, actual);
    }

    @Test
    public void createTypeTagWithSingleTypeParameterFromField() {
        expected = new TypeTag(List.class, new TypeTag(String.class));
        actual = TypeTag.of(getField("fieldWithSingleTypeParameter"));
        assertEquals(expected, actual);
    }

    @Test
    public void createTypeTagWithTwoTypeParametersFromField() {
        expected = new TypeTag(Map.class,
                new TypeTag(String.class),
                new TypeTag(Integer.class));
        actual = TypeTag.of(getField("fieldWithTwoTypeParameters"));
        assertEquals(expected, actual);
    }

    @Test
    public void createTypeTagWithNestedTypeParametersFromField() {
        expected = new TypeTag(Map.class,
                new TypeTag(String.class),
                new TypeTag(List.class, new TypeTag(String.class)));
        actual = TypeTag.of(getField("fieldWithNestedTypeParameters"));
        assertEquals(expected, actual);
    }

    @Test
    public void createTypeTagWithRidiculousTypeParametersFromField() {
        expected = new TypeTag(Map.class,
                new TypeTag(List.class, new TypeTag(Integer.class)),
                new TypeTag(Map.class,
                        new TypeTag(List.class, new TypeTag(Double.class)),
                        new TypeTag(Map.class,
                                new TypeTag(String.class),
                                new TypeTag(Float.class))));
        actual = TypeTag.of(getField("fieldWithRidiculousTypeParameters"));
        assertEquals(expected, actual);
    }

    @Test
    public void createPrimitiveTypeTagFromField() {
        expected = new TypeTag(int.class);
        actual = TypeTag.of(getField("primitiveField"));
        assertEquals(expected, actual);
    }

    @Test
    public void createArrayTypeTagFromField() {
        expected = new TypeTag(String[].class);
        actual = TypeTag.of(getField("arrayField"));
        assertEquals(expected, actual);
    }

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(TypeTag.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    private Field getField(String name) {
        try {
            return getClass().getDeclaredField(name);
        }
        catch (NoSuchFieldException e) {
            fail(e.toString());
            return null;
        }
    }
}
