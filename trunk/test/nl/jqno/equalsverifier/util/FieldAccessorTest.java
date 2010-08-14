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
		prefabValues = PrefabValuesFactory.get();
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
		String fieldName = getSyntheticFieldName(inner);
		nullField(inner, fieldName);
		assertSame(outer, inner.getOuter());
	}
	
	private String getSyntheticFieldName(Inner inner) {
		for (Field field : inner.getClass().getDeclaredFields()) {
			if (field.getName().startsWith("this$")) {
				return field.getName();
			}
		}
		throw new IllegalStateException("Cannot find internal reference to Outer");
	}
	
	@Test
	public void nullPrivateField() throws NoSuchFieldException {
		PrivateObjectContainer foo = new PrivateObjectContainer();
		nullField(foo, "object");
		assertNull(foo.get());
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

	private void nullField(Object object, String fieldName) {
		getAccessorFor(object, fieldName).nullField();
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
}
