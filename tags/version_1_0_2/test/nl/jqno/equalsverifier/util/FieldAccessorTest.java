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

import nl.jqno.equalsverifier.testhelpers.TypeHelper.AbstractAndInterfaceArrayContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.FinalContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.InterfaceContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.ObjectContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.Outer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.PointArrayContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.PrimitiveContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.PrivateObjectContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.StaticContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.StaticFinalContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.TransientContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.Outer.Inner;
import nl.jqno.equalsverifier.testhelpers.points.Point;
import nl.jqno.equalsverifier.testhelpers.points.PointContainer;

import org.junit.Before;
import org.junit.Test;

public class FieldAccessorTest {
	private static final Point RED_NEW_POINT = new Point(10, 20);
	private static final Point BLACK_NEW_POINT = new Point(20, 10);
	private static final String FIELD_NAME = "field";
	
	private PrefabValues prefabValues;
	
	@Before
	public void setup() {
		prefabValues = PrefabValuesFactory.withJavaClasses();
	}
	
	@Test
	public void getObject() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertSame(foo, fieldAccessor.getObject());
	}
	
	@Test
	public void getField() throws NoSuchFieldException {
		ObjectContainer foo = new ObjectContainer();
		Field field = foo.getClass().getDeclaredField(FIELD_NAME);
		FieldAccessor fieldAccessor = new FieldAccessor(foo, field);
		assertSame(field, fieldAccessor.getField());
	}
	
	@Test
	public void getFieldType() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertEquals(Object.class, fieldAccessor.getFieldType());
	}
	
	@Test
	public void getFieldName() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertEquals(FIELD_NAME, fieldAccessor.getFieldName());
	}
	
	@Test
	public void isNotFinal() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertFalse(fieldAccessor.fieldIsFinal());
	}
	
	@Test
	public void isFinal() {
		FinalContainer foo = new FinalContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertTrue(fieldAccessor.fieldIsFinal());
	}
	
	@Test
	public void isNotStatic() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertFalse(fieldAccessor.fieldIsStatic());
	}
	
	@Test
	public void isStatic() {
		StaticContainer foo = new StaticContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertTrue(fieldAccessor.fieldIsStatic());
	}
	
	@Test
	public void isNotTransient() {
		ObjectContainer foo = new ObjectContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertFalse(fieldAccessor.fieldIsTransient());
	}
	
	@Test
	public void isTransient() {
		TransientContainer foo = new TransientContainer();
		FieldAccessor fieldAccessor = getAccessorFor(foo, FIELD_NAME);
		assertTrue(fieldAccessor.fieldIsTransient());
	}
	
	@Test
	public void getValuePrimitive() {
		PrimitiveContainer foo = new PrimitiveContainer();
		foo.field = 10;
		Object value = getValue(foo, "field");
		assertEquals(10, value);
	}
	
	@Test
	public void getValueObject() {
		Object object = new Object();
		ObjectContainer foo = new ObjectContainer();
		foo.field = object;
		Object value = getValue(foo, FIELD_NAME);
		assertEquals(object, value);
	}
	
	@Test
	public void getPrivateValue() {
		PrivateObjectContainer foo = new PrivateObjectContainer();
		getValue(foo, FIELD_NAME);
	}
	
	@Test
	public void setValuePrimitive() {
		PrimitiveContainer foo = new PrimitiveContainer();
		setField(foo, FIELD_NAME, 20);
		assertEquals(20, foo.field);
	}
	
	@Test
	public void setValueObject() {
		Object object = new Object();
		ObjectContainer foo = new ObjectContainer();
		setField(foo, FIELD_NAME, object);
		assertEquals(object, foo.field);
	}
	
	@Test
	public void nullFieldHappyPath() throws NoSuchFieldException {
		ObjectContainer foo = new ObjectContainer();
		foo.field = new Object();
		nullField(foo, FIELD_NAME);
		assertNull(foo.field);
	}
	
	@Test
	public void nullFieldOnPrimitiveIsNoOp() throws NoSuchFieldException {
		PrimitiveContainer foo = new PrimitiveContainer();
		foo.field = 10;
		nullField(foo, FIELD_NAME);
		assertEquals(10, foo.field);
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
		nullField(foo, FIELD_NAME);
		assertNull(foo.get());
	}
	
	@Test
	public void copyToPrimitiveField() {
		int value = 10;
		
		PrimitiveContainer from = new PrimitiveContainer();
		from.field = value;
		
		PrimitiveContainer to = new PrimitiveContainer();
		copyField(to, from, FIELD_NAME);
		
		assertEquals(value, to.field);
	}
	
	@Test
	public void copyToObjectField() {
		Object value = new Object();
		
		ObjectContainer from = new ObjectContainer();
		from.field = value;
		
		ObjectContainer to = new ObjectContainer();
		copyField(to, from, FIELD_NAME);
		
		assertSame(value, to.field);
	}

	@Test
	public void changeField() {
		AllTypesContainer reference = new AllTypesContainer();
		AllTypesContainer changed = new AllTypesContainer();
		assertTrue(reference.equals(changed));
		
		for (Field field : new FieldIterable(AllTypesContainer.class)) {
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
		changeField(foo, FIELD_NAME);
		assertNotNull(foo.field);
	}
	
	@Test
	public void changeInterfaceField() {
		InterfaceContainer foo = new InterfaceContainer();
		changeField(foo, FIELD_NAME);
		assertNotNull(foo.field);
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
		
		for (Field field : new FieldIterable(AllArrayTypesContainer.class)) {
			new FieldAccessor(changed, field).changeField(prefabValues);
			assertFalse("On field: " + field.getName(), reference.equals(changed));
			new FieldAccessor(reference, field).changeField(prefabValues);
			assertTrue("On field: " + field.getName(), reference.equals(changed));
		}
	}
	
	@Test
	public void changeAbstractArrayField() {
		AbstractAndInterfaceArrayContainer foo = new AbstractAndInterfaceArrayContainer();
		changeField(foo, "abstractClasses");
		assertNotNull(foo.abstractClasses[0]);
	}
	
	@Test
	public void changeInterfaceArrayField() {
		AbstractAndInterfaceArrayContainer foo = new AbstractAndInterfaceArrayContainer();
		changeField(foo, "interfaces");
		assertNotNull(foo.interfaces[0]);
	}
	
	@Test
	public void addPrefabValues() {
		PointContainer foo = new PointContainer(new Point(1, 2));
		prefabValues.put(Point.class, RED_NEW_POINT, BLACK_NEW_POINT);
		
		changeField(foo, "point");
		assertEquals(RED_NEW_POINT, foo.getPoint());
		
		changeField(foo, "point");
		assertEquals(BLACK_NEW_POINT, foo.getPoint());
		
		changeField(foo, "point");
		assertEquals(RED_NEW_POINT, foo.getPoint());
	}
	
	@Test
	public void addPrefabArrayValues() {
		PointArrayContainer foo = new PointArrayContainer();
		prefabValues.put(Point.class, RED_NEW_POINT, BLACK_NEW_POINT);
		
		changeField(foo, "points");
		assertEquals(RED_NEW_POINT, foo.points[0]);
		
		changeField(foo, "points");
		assertEquals(BLACK_NEW_POINT, foo.points[0]);
		
		changeField(foo, "points");
		assertEquals(RED_NEW_POINT, foo.points[0]);
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
