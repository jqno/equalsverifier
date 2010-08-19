/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.util.TypeHelper.AbstractAndInterfaceArrayContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.ArrayContainer;
import nl.jqno.equalsverifier.util.TypeHelper.InterfaceContainer;
import nl.jqno.equalsverifier.util.TypeHelper.ObjectContainer;
import nl.jqno.equalsverifier.util.TypeHelper.Outer;
import nl.jqno.equalsverifier.util.TypeHelper.PrimitiveContainer;
import nl.jqno.equalsverifier.util.TypeHelper.PrivateObjectContainer;
import nl.jqno.equalsverifier.util.TypeHelper.StaticFinalContainer;
import nl.jqno.equalsverifier.util.TypeHelper.Outer.Inner;

import org.junit.Before;
import org.junit.Test;

public class FieldAccessorTest {
	private PrefabValues prefabValues;
	
	@Before
	public void setup() {
		prefabValues = PrefabValuesFactory.withJavaClasses();
	}
	
	@Test
	public void getObject() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, "_object");
		assertSame(foo, fieldAccessor.getObject());
	}
	
	@Test
	public void getField() throws NoSuchFieldException {
		ObjectContainer foo = new ObjectContainer();
		Field field = foo.getClass().getDeclaredField("_object");
		FieldAccessor fieldAccessor = new FieldAccessor(foo, field);
		assertSame(field, fieldAccessor.getField());
	}
	
	@Test
	public void getFieldType() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, "_object");
		assertEquals(Object.class, fieldAccessor.getFieldType());
	}
	
	@Test
	public void getFieldName() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, "_object");
		assertEquals("_object", fieldAccessor.getFieldName());
	}
	
	@Test
	public void setValuePrimitive() {
		PrimitiveContainer foo = new PrimitiveContainer();
		setField(foo, "i", 20);
		assertEquals(20, foo.i);
	}
	
	@Test
	public void getValuePrimitive() {
		PrimitiveContainer foo = new PrimitiveContainer();
		foo.i = 10;
		Object value = getValue(foo, "i");
		assertEquals(10, value);
	}
	
	@Test
	public void getValueObject() {
		Object object = new Object();
		ObjectContainer foo = new ObjectContainer();
		foo._object = object;
		Object value = getValue(foo, "_object");
		assertEquals(object, value);
	}
	
	@Test
	public void setValueObject() {
		Object object = new Object();
		ObjectContainer foo = new ObjectContainer();
		setField(foo, "_object", object);
		assertEquals(object, foo._object);
	}
	
	@Test
	public void nullFieldHappyPath() throws NoSuchFieldException {
		ObjectContainer foo = new ObjectContainer();
		foo._object = new Object();
		nullField(foo, "_object");
		assertNull(foo._object);
	}
	
	@Test
	public void nullFieldOnPrimitiveIsNoOp() throws NoSuchFieldException {
		PrimitiveContainer foo = new PrimitiveContainer();
		foo.i = 10;
		nullField(foo, "i");
		assertEquals(10, foo.i);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void nullFieldOnPrimitiveStaticFinalIsNoOp() throws NoSuchFieldException {
		StaticFinalContainer foo = new StaticFinalContainer();
		nullField(foo, "CONST");
		assertEquals(42, foo.CONST);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void nullFieldOnObjectStaticFinalIsNoOp() throws NoSuchFieldException {
		StaticFinalContainer foo = new StaticFinalContainer();
		Object original = foo.OBJECT;
		nullField(foo, "OBJECT");
		assertSame(original, foo.OBJECT);
	}
	
	@Test
	public void nullFieldOnSyntheticIsNoOp() throws NoSuchFieldException {
		Outer outer = new Outer();
		Inner inner = outer.new Inner();
		String fieldName = getSyntheticFieldName(inner, "this$");
		nullField(inner, fieldName);
		assertSame(outer, inner.getOuter());
	}
	
	@Test
	public void nullPrivateField() throws NoSuchFieldException {
		PrivateObjectContainer foo = new PrivateObjectContainer();
		nullField(foo, "object");
		assertNull(foo.get());
	}
	
	@Test
	public void copyToPrimitiveField() {
		int value = 10;
		
		PrimitiveContainer from = new PrimitiveContainer();
		from.i = value;
		
		PrimitiveContainer to = new PrimitiveContainer();
		copyField(to, from, "i");
		
		assertEquals(value, to.i);
	}
	
	@Test
	public void copyToObjectField() {
		Object value = new Object();
		
		ObjectContainer from = new ObjectContainer();
		from._object = value;
		
		ObjectContainer to = new ObjectContainer();
		copyField(to, from, "_object");
		
		assertSame(value, to._object);
	}

	@Test
	public void changeField() {
		AllTypesContainer reference = new AllTypesContainer();
		AllTypesContainer changed = new AllTypesContainer();
		assertTrue(reference.equals(changed));
		
		for (Field field : AllTypesContainer.class.getDeclaredFields()) {
			new FieldAccessor(changed, field).changeField(prefabValues);
			assertFalse("On field: " + field.getName(), reference.equals(changed));
			new FieldAccessor(reference, field).changeField(prefabValues);
			assertTrue("On field: " + field.getName(), reference.equals(changed));
		}
	}

	@SuppressWarnings("static-access")
	@Test
	public void changeFieldOnPrimitiveStaticFinalIsNoOp() throws NoSuchFieldException {
		StaticFinalContainer foo = new StaticFinalContainer();
		changeField(foo, "CONST");
		assertEquals(42, foo.CONST);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void changeFieldStaticFinal() throws SecurityException, NoSuchFieldException {
		StaticFinalContainer foo = new StaticFinalContainer();
		Object original = foo.OBJECT;
		changeField(foo, "OBJECT");
		assertEquals(original, foo.OBJECT);
	}
	
	@Test
	public void changeAbstractField() {
		AbstractClassContainer foo = new AbstractClassContainer();
		changeField(foo, "ac");
		assertNotNull(foo.ac);
	}
	
	@Test
	public void changeInterfaceField() {
		InterfaceContainer foo = new InterfaceContainer();
		changeField(foo, "_interface");
		assertNotNull(foo._interface);
	}
	
	@Test
	public void changeFieldOnCglibFieldIsNoOp() throws NoSuchFieldException, IllegalAccessException {
		class Empty {}
		
		Empty cglibed = Instantiator.of(Empty.class).instantiateAnonymousSubclass();
		String fieldName = getSyntheticFieldName(cglibed, "CGLIB$");
		Field field = cglibed.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		
		Object original = field.get(cglibed);
		changeField(cglibed, fieldName);
		
		assertEquals(original, field.get(cglibed));
	}
	
	@Test
	public void changeArrayField() {
		AllArrayTypesContainer reference = new AllArrayTypesContainer();
		AllArrayTypesContainer changed = new AllArrayTypesContainer();
		assertTrue(reference.equals(changed));
		
		for (Field field : AllArrayTypesContainer.class.getDeclaredFields()) {
			new FieldAccessor(changed, field).changeArrayField(0, prefabValues);
			assertFalse("On field: " + field.getName(), reference.equals(changed));
			new FieldAccessor(reference, field).changeArrayField(0, prefabValues);
			assertTrue("On field: " + field.getName(), reference.equals(changed));
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void changeArrayFieldWhichIsNotAnArrayField() {
		ObjectContainer foo = new ObjectContainer();
		changeArrayField(foo, "_object", 0);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void changeArrayFieldWithInvalidIndex() {
		final int size = 10;
		ArrayContainer foo = new ArrayContainer();
		foo.array = new int[size];
		changeArrayField(foo, "array", size);
	}
	
	@Test
	public void changeAbstractArrayField() {
		AbstractAndInterfaceArrayContainer foo = new AbstractAndInterfaceArrayContainer();
		changeArrayField(foo, "abstractClasses", 0);
		assertNotNull(foo.abstractClasses[0]);
	}
	
	@Test
	public void changeInterfaceArrayField() {
		AbstractAndInterfaceArrayContainer foo = new AbstractAndInterfaceArrayContainer();
		changeArrayField(foo, "interfaces", 0);
		assertNotNull(foo.interfaces[0]);
	}
	
	private Object getValue(Object object, String fieldName) {
		return getAccessorFor(object, fieldName).get();
	}
	
	private void setField(Object object, String fieldName, Object value) {
		getAccessorFor(object, fieldName).set(value);
	}

	private void nullField(Object object, String fieldName) {
		getAccessorFor(object, fieldName).nullField();
	}
	
	private void copyField(Object to, Object from, String fieldName) {
		getAccessorFor(from, fieldName).copyTo(to);
	}
	
	private void changeField(Object object, String fieldName) {
		getAccessorFor(object, fieldName).changeField(prefabValues);
	}
	
	private void changeArrayField(Object object, String fieldName, int index) {
		getAccessorFor(object, fieldName).changeArrayField(index, prefabValues);
	}
	
	private FieldAccessor getAccessorFor(Object object, String fieldName) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			return new FieldAccessor(object, field);
		}
		catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("fieldName: " + fieldName);
		}
	}
	
	private String getSyntheticFieldName(Object object, String prefix) {
		for (Field field : object.getClass().getDeclaredFields()) {
			if (field.getName().startsWith(prefix)) {
				return field.getName();
			}
		}
		throw new IllegalStateException("Cannot find internal field starting with " + prefix);
	}
}
