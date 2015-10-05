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
package nl.jqno.equalsverifier.internal.prefabvalues;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TypeTagParameterizedTest<T> {
    @SuppressWarnings("unused") private final String simpleField = null;
    @SuppressWarnings("unused") private final List<String> fieldWithSingleTypeParameter = null;
    @SuppressWarnings("unused") private final Map<String, Integer> fieldWithTwoTypeParameters = null;
    @SuppressWarnings("unused") private final Map<String, List<String>> fieldWithNestedTypeParameters = null;
    @SuppressWarnings("unused") private final Map<List<Integer>, Map<List<Double>, Map<String, Float>>> fieldWithRidiculousTypeParameters = null;
    @SuppressWarnings({ "unused", "raw" }) private final Map rawMapField = null;
    @SuppressWarnings("unused") private final List<?> fieldWithWildcardParameter = null;
    @SuppressWarnings("unused") private final Class<String>[] fieldWithGenericArrayParameter = null;
    @SuppressWarnings("unused") private final List<T> fieldWithTypeVariable = null;

    @SuppressWarnings("unused") private final int primitiveField = 0;
    @SuppressWarnings("unused") private final String[] arrayField = null;

    private final String fieldName;
    private final TypeTag expected;

    public TypeTagParameterizedTest(String fieldName, TypeTag expected) {
        this.fieldName = fieldName;
        this.expected = expected;
    }

    @Parameters(name = "Field {0} should be {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "simpleField", new TypeTag(String.class) },
                { "fieldWithSingleTypeParameter", new TypeTag(List.class, new TypeTag(String.class)) },
                { "fieldWithTwoTypeParameters", new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Integer.class)) },
                { "fieldWithNestedTypeParameters", new TypeTag(Map.class,
                        new TypeTag(String.class),
                        new TypeTag(List.class, new TypeTag(String.class))) },
                { "fieldWithRidiculousTypeParameters", new TypeTag(Map.class,
                        new TypeTag(List.class, new TypeTag(Integer.class)),
                        new TypeTag(Map.class,
                                new TypeTag(List.class, new TypeTag(Double.class)),
                                new TypeTag(Map.class,
                                        new TypeTag(String.class),
                                        new TypeTag(Float.class)))) },
                { "rawMapField", new TypeTag(Map.class) },
                { "fieldWithWildcardParameter", new TypeTag(List.class, new TypeTag(TypeTag.Wildcard.class)) },
                { "fieldWithGenericArrayParameter", new TypeTag(TypeTag.GenericArray.class, new TypeTag(Class.class, new TypeTag(String.class))) },
                { "fieldWithTypeVariable", new TypeTag(List.class, new TypeTag(TypeTag.TypeVariable.class)) },
                { "primitiveField", new TypeTag(int.class) },
                { "arrayField", new TypeTag(String[].class) }
        });
    }

    @Test
    public void correctness() {
        TypeTag actual = TypeTag.of(getField(fieldName));
        assertEquals(expected, actual);
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
