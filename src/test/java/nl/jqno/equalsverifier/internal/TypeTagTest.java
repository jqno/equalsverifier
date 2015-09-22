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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TypeTagTest {
    private static final TypeTag SOME_LONG_TYPETAG =
            new TypeTag(Map.class, new TypeTag(Integer.class), new TypeTag(List.class, new TypeTag(String.class)));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(TypeTag.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void typeCannotBeNull() {
        thrown.expect(NullPointerException.class);
        new TypeTag(null);
    }

    @Test
    public void getType() {
        assertEquals(Map.class, SOME_LONG_TYPETAG.getType());
    }

    @Test
    public void testToString() {
        assertEquals("TypeTag: String", new TypeTag(String.class).toString());
        assertEquals("TypeTag: List<TypeTag: String>", new TypeTag(List.class, new TypeTag(String.class)).toString());
        assertEquals("TypeTag: Map<TypeTag: Integer, TypeTag: List<TypeTag: String>>", SOME_LONG_TYPETAG.toString());
    }
}
