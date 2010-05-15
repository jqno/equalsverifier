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

import org.junit.Test;

public class FieldIterableTest {
	@Test
	public void simpleFields() {
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(FieldContainer.class)) {
			actual.add(field);
		}
		
		assertEquals(FIELD_CONTAINER_FIELDS, actual);
	}
	
	@Test
	public void subClassFields() {
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(SubFieldContainer.class)) {
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
		expected.add(SubNoFields.class.getField("o"));
		
		Set<Field> actual = new HashSet<Field>();
		for (Field field : new FieldIterable(SubNoFields.class)) {
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
		FIELD_CONTAINER_FIELDS.add(SubEmptySubFieldContainer.class.getDeclaredField("ll"));
		
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
		Iterator<Field> iterator = new FieldIterable(FieldContainer.class).iterator();
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
		try {
			add(FieldContainer.class.getDeclaredField("i"));
			add(FieldContainer.class.getDeclaredField("j"));
			add(FieldContainer.class.getDeclaredField("k"));
			add(FieldContainer.class.getDeclaredField("l"));
			add(FieldContainer.class.getDeclaredField("I"));
			add(FieldContainer.class.getDeclaredField("J"));
			add(FieldContainer.class.getDeclaredField("K"));
			add(FieldContainer.class.getDeclaredField("L"));
		}
		catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
	}};
	
	@SuppressWarnings("serial")
	private static final Set<Field> SUB_FIELD_CONTAINER_FIELDS = new HashSet<Field>() {{
		try {
			addAll(FIELD_CONTAINER_FIELDS);
			add(SubFieldContainer.class.getDeclaredField("a"));
			add(SubFieldContainer.class.getDeclaredField("b"));
			add(SubFieldContainer.class.getDeclaredField("c"));
			add(SubFieldContainer.class.getDeclaredField("d"));
		}
		catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		}
	}};
	
	static class FieldContainer {
		@SuppressWarnings("unused")
		private final int i = 0;
		final int j = 0;
		protected final int k = 0;
		public final int l = 0;
		@SuppressWarnings("unused")
		private static final int I = 0;
		final static int J = 0;
		protected static final int K = 0;
		public static final int L = 0;
	}
	
	static class SubFieldContainer extends FieldContainer {
		@SuppressWarnings("unused")
		private final String a = "";
		final String b = "";
		protected final String c = "";
		public final String d = "";
	}
	
	static class NoFields {}
	
	static class SubNoFields extends NoFields {
		public Object o;
	}
	
	static class EmptySubFieldContainer extends FieldContainer {}
	
	static class SubEmptySubFieldContainer extends EmptySubFieldContainer {
		public long ll = 0;
	}
	
	interface Interface {}
	
	static class Outer {
		class Inner {}
	}
}
