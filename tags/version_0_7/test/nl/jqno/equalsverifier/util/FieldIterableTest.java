/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import nl.jqno.equalsverifier.util.TypeHelper.DifferentAccessModifiersFieldContainer;
import nl.jqno.equalsverifier.util.TypeHelper.DifferentAccessModifiersSubFieldContainer;
import nl.jqno.equalsverifier.util.TypeHelper.EmptySubFieldContainer;
import nl.jqno.equalsverifier.util.TypeHelper.Interface;
import nl.jqno.equalsverifier.util.TypeHelper.NoFields;
import nl.jqno.equalsverifier.util.TypeHelper.NoFieldsSubWithFields;
import nl.jqno.equalsverifier.util.TypeHelper.Outer;
import nl.jqno.equalsverifier.util.TypeHelper.SubEmptySubFieldContainer;

import org.junit.Test;

public class FieldIterableTest {
	@Test
	public void simpleFields() {
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(DifferentAccessModifiersFieldContainer.class)) {
			actual.add(field);
		}
		
		assertEquals(FIELD_CONTAINER_FIELDS, actual);
	}
	
	@Test
	public void subClassFields() {
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(DifferentAccessModifiersSubFieldContainer.class)) {
			actual.add(field);
		}
		
		assertEquals(SUB_FIELD_CONTAINER_FIELDS, actual);
	}
	
	@Test
	public void noFields() {
		FieldIterable iterable = new FieldIterable(NoFields.class);
		assertFalse(iterable.iterator().hasNext());
	}
	
	@Test
	public void superHasNoFields() throws NoSuchFieldException {
		Set<Field> expected = new HashSet<Field>();
		expected.add(NoFieldsSubWithFields.class.getField("field"));
		
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(NoFieldsSubWithFields.class)) {
			actual.add(field);
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void subHasNoFields() {
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(EmptySubFieldContainer.class)) {
			actual.add(field);
		}
		
		assertEquals(FIELD_CONTAINER_FIELDS, actual);
	}
	
	@Test
	public void classInTheMiddleHasNoFields() throws NoSuchFieldException {
		FIELD_CONTAINER_FIELDS.add(SubEmptySubFieldContainer.class.getDeclaredField("field"));
		
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(SubEmptySubFieldContainer.class)) {
			actual.add(field);
		}
		
		assertEquals(FIELD_CONTAINER_FIELDS, actual);
	}
	
	@Test
	public void interfaceTest() {
		FieldIterable iterable = new FieldIterable(Interface.class);
		assertFalse(iterable.iterator().hasNext());
	}

	@Test(expected=NoSuchElementException.class)
	public void nextAfterLastElement() {
		Iterator<Field> iterator = new FieldIterable(DifferentAccessModifiersFieldContainer.class).iterator();
		while (iterator.hasNext()) {
			iterator.next();
		}
		iterator.next();
	}
	
	@Test
	public void objectHasNoElements() {
		FieldIterable iterable = new FieldIterable(Object.class);
		assertFalse(iterable.iterator().hasNext());
	}
	
	@Test
	public void ignoreSyntheticFields() {
		FieldIterable iterable = new FieldIterable(Outer.Inner.class);
		assertFalse(iterable.iterator().hasNext());
	}
	
	@SuppressWarnings("serial")
	private static final Set<Field> FIELD_CONTAINER_FIELDS = new HashSet<Field>() {{
		Class<DifferentAccessModifiersFieldContainer> type = DifferentAccessModifiersFieldContainer.class;
		try {
			add(type.getDeclaredField("i"));
			add(type.getDeclaredField("j"));
			add(type.getDeclaredField("k"));
			add(type.getDeclaredField("l"));
			add(type.getDeclaredField("I"));
			add(type.getDeclaredField("J"));
			add(type.getDeclaredField("K"));
			add(type.getDeclaredField("L"));
		}
		catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
	}};
	
	@SuppressWarnings("serial")
	private static final Set<Field> SUB_FIELD_CONTAINER_FIELDS = new HashSet<Field>() {{
		Class<DifferentAccessModifiersSubFieldContainer> type = DifferentAccessModifiersSubFieldContainer.class;
		try {
			addAll(FIELD_CONTAINER_FIELDS);
			add(type.getDeclaredField("a"));
			add(type.getDeclaredField("b"));
			add(type.getDeclaredField("c"));
			add(type.getDeclaredField("d"));
		}
		catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
	}};
}
