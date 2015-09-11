/*
 * Copyright 2010, 2013 Jan Ouwens
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

import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FieldIterableTest {
    private static final Set<Field> FIELD_CONTAINER_FIELDS = createFieldContainerFields();
    private static final Set<Field> SUB_FIELD_CONTAINER_FIELDS = createSubFieldContainerFields();
    private static final Set<Field> FIELD_AND_SUB_FIELD_CONTAINER_FIELDS = createFieldAndSubFieldContainerFields();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void simpleFields() {
        Set<Field> actual = new HashSet<>();
        for (Field field : FieldIterable.of(DifferentAccessModifiersFieldContainer.class)) {
            actual.add(field);
        }

        assertEquals(FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void subAndSuperClassFields() {
        Set<Field> actual = new HashSet<>();
        for (Field field : FieldIterable.of(DifferentAccessModifiersSubFieldContainer.class)) {
            actual.add(field);
        }

        assertEquals(FIELD_AND_SUB_FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void onlySubClassFields() {
        Set<Field> actual = new HashSet<>();
        for (Field field : FieldIterable.ofIgnoringSuper(DifferentAccessModifiersSubFieldContainer.class)) {
            actual.add(field);
        }

        assertEquals(SUB_FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void noFields() {
        FieldIterable iterable = FieldIterable.of(NoFields.class);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void superHasNoFields() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<>();
        expected.add(NoFieldsSubWithFields.class.getField("field"));

        Set<Field> actual = new HashSet<>();
        for (Field field : FieldIterable.of(NoFieldsSubWithFields.class)) {
            actual.add(field);
        }

        assertEquals(expected, actual);
    }

    @Test
    public void subHasNoFields() {
        Set<Field> actual = new HashSet<>();
        for (Field field : FieldIterable.of(EmptySubFieldContainer.class)) {
            actual.add(field);
        }

        assertEquals(FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void classInTheMiddleHasNoFields() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<>();
        expected.addAll(FIELD_CONTAINER_FIELDS);
        expected.add(SubEmptySubFieldContainer.class.getDeclaredField("field"));

        Set<Field> actual = new HashSet<>();
        for (Field field : FieldIterable.of(SubEmptySubFieldContainer.class)) {
            actual.add(field);
        }

        assertEquals(expected, actual);
    }

    @Test
    public void interfaceTest() {
        FieldIterable iterable = FieldIterable.of(Interface.class);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void nextAfterLastElement() {
        Iterator<Field> iterator = FieldIterable.of(DifferentAccessModifiersFieldContainer.class).iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }

        thrown.expect(NoSuchElementException.class);
        iterator.next();
    }

    @Test
    public void objectHasNoElements() {
        FieldIterable iterable = FieldIterable.of(Object.class);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void ignoreSyntheticFields() {
        FieldIterable iterable = FieldIterable.of(Outer.Inner.class);
        assertFalse(iterable.iterator().hasNext());
    }

    private static Set<Field> createFieldContainerFields() {
        Set<Field> result = new HashSet<>();
        Class<DifferentAccessModifiersFieldContainer> type = DifferentAccessModifiersFieldContainer.class;
        try {
            result.add(type.getDeclaredField("i"));
            result.add(type.getDeclaredField("j"));
            result.add(type.getDeclaredField("k"));
            result.add(type.getDeclaredField("l"));
            result.add(type.getDeclaredField("I"));
            result.add(type.getDeclaredField("J"));
            result.add(type.getDeclaredField("K"));
            result.add(type.getDeclaredField("L"));
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private static Set<Field> createSubFieldContainerFields() {
        Set<Field> result = new HashSet<>();
        Class<DifferentAccessModifiersSubFieldContainer> type = DifferentAccessModifiersSubFieldContainer.class;
        try {
            result.add(type.getDeclaredField("a"));
            result.add(type.getDeclaredField("b"));
            result.add(type.getDeclaredField("c"));
            result.add(type.getDeclaredField("d"));
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private static Set<Field> createFieldAndSubFieldContainerFields() {
        Set<Field> result = new HashSet<>();
        result.addAll(FIELD_CONTAINER_FIELDS);
        result.addAll(SUB_FIELD_CONTAINER_FIELDS);
        return result;
    }
}
