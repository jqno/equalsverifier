/*
 * Copyright 2009 Jan Ouwens
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
package nl.jqno.instantiator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

import nl.jqno.instantiator.Instantiator;

import org.junit.Test;

public class FieldModificationTest {
	private static enum Enum { FIRST, SECOND }
	private static final Object OBJECT = new Object();
	private static final int[] INT_ARRAY = { 1 };
	
	@Test
	public void nullField() throws SecurityException, NoSuchFieldException {
		Class<ObjectContainer> klass = ObjectContainer.class;
		Instantiator<ObjectContainer> instantiator = Instantiator.forClass(klass);
		
		ObjectContainer oc = new ObjectContainer();
		instantiator.nullField(klass.getField("_object"), oc);
		
		assertNull(oc._object);
	}
	
	@Test
	public void nullFieldPrimitive() throws SecurityException, NoSuchFieldException {
		Class<PrimitiveContainer> klass = PrimitiveContainer.class;
		Instantiator<PrimitiveContainer> instantiator = Instantiator.forClass(klass);
		
		PrimitiveContainer pc = new PrimitiveContainer();
		instantiator.nullField(klass.getField("i"), pc);
		
		assertEquals(10, pc.i);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void nullFieldStaticFinal() throws SecurityException, NoSuchFieldException {
		Class<StaticFinalContainer> klass = StaticFinalContainer.class;
		Instantiator<StaticFinalContainer> instantiator = Instantiator.forClass(klass);
		StaticFinalContainer sfc = new StaticFinalContainer();
		
		instantiator.nullField(klass.getField("CONST"), sfc);
		assertEquals(42, sfc.CONST);
		
		Object original = sfc.OBJECT;
		instantiator.nullField(klass.getField("OBJECT"), sfc);
		assertEquals(original, sfc.OBJECT);
	}
	
	@Test
	public void changeField() {
		Instantiator<AllTypesContainer> instantiator = Instantiator.forClass(AllTypesContainer.class);
		AllTypesContainer reference = new AllTypesContainer();
		AllTypesContainer changed = new AllTypesContainer();
		
		assertEquals(reference, changed);
		for (Field field : AllTypesContainer.class.getDeclaredFields()) {
			instantiator.changeField(field, changed);
			assertFalse(reference.equals(changed));
			instantiator.changeField(field, reference);
			assertEquals(reference, changed);
		}
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void changeFieldStaticFinal() throws SecurityException, NoSuchFieldException {
		Class<StaticFinalContainer> klass = StaticFinalContainer.class;
		Instantiator<StaticFinalContainer> instantiator = Instantiator.forClass(klass);
		StaticFinalContainer sfc = new StaticFinalContainer();
		
		instantiator.changeField(klass.getField("CONST"), sfc);
		assertEquals(42, sfc.CONST);

		Object original = sfc.OBJECT;
		instantiator.changeField(klass.getField("OBJECT"), sfc);
		assertEquals(original, sfc.OBJECT);
	}
	
	@Test
	public void changeFieldAbstract() throws SecurityException, NoSuchFieldException {
		Class<AbstractClassContainer> klass = AbstractClassContainer.class;
		Instantiator<AbstractClassContainer> instantiator = Instantiator.forClass(klass);
		
		AbstractClassContainer acc = new AbstractClassContainer();
		instantiator.changeField(klass.getField("ac"), acc);
		assertNotNull(acc.ac);
	}
	
	@Test
	public void changeFieldInterface() throws SecurityException, NoSuchFieldException {
		Class<InterfaceContainer> klass = InterfaceContainer.class;
		Instantiator<InterfaceContainer> instantiator = Instantiator.forClass(klass);
		
		InterfaceContainer ic = new InterfaceContainer();
		instantiator.changeField(klass.getField("_interface"), ic);
		assertNotNull(ic._interface);
	}
	
	@Test
	public void changeFieldPrefabValues() {
		Instantiator<AllTypesContainer> instantiator = Instantiator.forClass(AllTypesContainer.class);
		AllTypesContainer atc = new AllTypesContainer();
		
		instantiator.scramble(atc);
		assertEquals("one", atc._string);
		assertTrue(1 == atc._Byte);
		assertTrue('a' == atc._Char);
		assertTrue(Double.compare(0.5D, atc._Double) == 0);
		assertTrue(Float.compare(0.5F, atc._Float) == 0);
		assertTrue(1 == atc._Int);
		assertTrue(1L == atc._Long);
		assertTrue(1 == atc._Short);
		
		instantiator.scramble(atc);
		assertEquals("two", atc._string);
		assertTrue(2 == atc._Byte);
		assertTrue('b' == atc._Char);
		assertTrue(Double.compare(1.0D, atc._Double) == 0);
		assertTrue(Float.compare(1.0F, atc._Float) == 0);
		assertTrue(2 == atc._Int);
		assertTrue(2L == atc._Long);
		assertTrue(2 == atc._Short);
		
		instantiator.scramble(atc);
		assertEquals("three", atc._string);
		assertTrue(3 == atc._Byte);
		assertTrue('c' == atc._Char);
		assertTrue(Double.compare(1.5D, atc._Double) == 0);
		assertTrue(Float.compare(1.5F, atc._Float) == 0);
		assertTrue(3 == atc._Int);
		assertTrue(3L == atc._Long);
		assertTrue(3 == atc._Short);
		
		instantiator.scramble(atc);
		assertEquals("one", atc._string);
		assertTrue(1 == atc._Byte);
		assertTrue('a' == atc._Char);
		assertTrue(Double.compare(0.5D, atc._Double) == 0);
		assertTrue(Float.compare(0.5F, atc._Float) == 0);
		assertTrue(1 == atc._Int);
		assertTrue(1L == atc._Long);
		assertTrue(1 == atc._Short);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void changeNotArrayElement() {
		Object o = new Object();
		Instantiator<Object> instantiator = Instantiator.forClass(Object.class);
		instantiator.changeArrayElement(o, 0);
	}
	
	@Test
	public void changeArrayElement() throws IllegalArgumentException, IllegalAccessException {
		Instantiator<AllArrayTypesContainer> instantiator = Instantiator.forClass(AllArrayTypesContainer.class);
		AllArrayTypesContainer reference = new AllArrayTypesContainer();
		AllArrayTypesContainer changed = new AllArrayTypesContainer();
		
		assertEquals(reference, changed);
		for (Field field : AllArrayTypesContainer.class.getDeclaredFields()) {
			instantiator.changeArrayElement(field.get(changed), 0);
			assertFalse(reference.equals(changed));
			instantiator.changeArrayElement(field.get(reference), 0);
			assertEquals(reference, changed);
		}
	}
	
	@Test
	public void changeAbstractArrayElement() {
		Instantiator<AbstractAndInterfaceArrayContainer> instantiator = Instantiator.forClass(AbstractAndInterfaceArrayContainer.class);
		AbstractAndInterfaceArrayContainer aac = new AbstractAndInterfaceArrayContainer();
		instantiator.changeArrayElement(aac.abstractClasses, 0);
		assertNotNull(aac.abstractClasses[0]);
	}
	
	@Test
	public void changeInterfaceArrayElement() {
		Instantiator<AbstractAndInterfaceArrayContainer> instantiator = Instantiator.forClass(AbstractAndInterfaceArrayContainer.class);
		AbstractAndInterfaceArrayContainer aac = new AbstractAndInterfaceArrayContainer();
		instantiator.changeArrayElement(aac.interfaces, 0);
		assertNotNull(aac.interfaces[0]);
	}
	
	@Test
	public void changePrefabArrayElement() {
		Instantiator<AllArrayBoxedTypesContainer> instantiator = Instantiator.forClass(AllArrayBoxedTypesContainer.class);
		AllArrayBoxedTypesContainer aabtc = new AllArrayBoxedTypesContainer();
		
		changePrefabArrayElementFourTimes(instantiator, aabtc.strings, "one", "two", "three");
		changePrefabArrayElementFourTimes(instantiator, aabtc.bytes, (byte)1, (byte)2, (byte)3);
		changePrefabArrayElementFourTimes(instantiator, aabtc.chars, 'a', 'b', 'c');
		changePrefabArrayElementFourTimes(instantiator, aabtc.doubles, 0.5D, 1.0D, 1.5D);
		changePrefabArrayElementFourTimes(instantiator, aabtc.floats, 0.5F, 1.0F, 1.5F);
		changePrefabArrayElementFourTimes(instantiator, aabtc.ints, 1, 2, 3);
		changePrefabArrayElementFourTimes(instantiator, aabtc.longs, 1L, 2L, 3L);
		changePrefabArrayElementFourTimes(instantiator, aabtc.shorts, (short)1, (short)2, (short)3);
	}
	
	private void changePrefabArrayElementFourTimes(Instantiator<?> instantiator, Object array, Object... values) {
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[0], Array.get(array, 0));
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[1], Array.get(array, 0));
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[2], Array.get(array, 0));
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[0], Array.get(array, 0));
	}
	
	private static final class ObjectContainer {
		public Object _object = new Object();
	}
	
	private static final class PrimitiveContainer {
		public int i = 10;
	}
	
	private static final class StaticFinalContainer {
		public static final int CONST = 42;
		public static final Object OBJECT = new Object();
	}
	
	private static final class AbstractClassContainer {
		public AbstractClass ac;
	}
	
	private static final class InterfaceContainer {
		public Interface _interface;
	}
	
	private static final class AllTypesContainer {
		boolean _boolean = false;
		byte _byte = 0;
		char _char = '\u0000';
		double _double = 0.0D;
		float _float = 0.0F;
		int _int = 0;
		long _long = 0L;
		short _short = 0;

		Boolean _Boolean = false;
		Byte _Byte = 0;
		Character _Char = '\u0000';
		Double _Double = 0.0D;
		Float _Float = 0.0F;
		Integer _Int = 0;
		Long _Long = 0L;
		Short _Short = 0;
		
		Enum _enum = Enum.FIRST;
		int[] _array = {1, 2, 3};
		Object _object = OBJECT;
		String _string = "";
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllTypesContainer)) {
				return false;
			}
			AllTypesContainer other = (AllTypesContainer)obj;
			return
					_boolean == other._boolean &&
					_byte == other._byte &&
					_char == other._char &&
					(Double.compare(_double, other._double) == 0) &&
					(Float.compare(_float, other._float) == 0) &&
					_int == other._int &&
					_long == other._long &&
					_short == other._short &&
					_Boolean == other._Boolean &&
					_Byte == other._Byte &&
					_Char == other._Char &&
					((_Double == null || other._Double == null) ?
							_Double == other._Double :
							Double.compare(_Double, other._Double) == 0) &&
					((_Float == null || other._Float == null) ?
							_Float == other._Float :
							Float.compare(_Float, other._Float) == 0) &&
					_Int == other._Int &&
					_Long == other._Long &&
					_Short == other._Short &&
					_enum == other._enum &&
					Arrays.equals(_array, other._array) &&
					(_object == null ? other._object == null : _object.equals(other._object)) &&
					(_string == null ? other._string == null : _string.equals(other._string));
		}
	}
	
	private static final class AllArrayTypesContainer {
		boolean[] booleans = { true };
		byte[] bytes = { 1 };
		char[] chars = { 'a' };
		double[] doubles = { 1.0D };
		float[] floats = { 1.0F };
		int[] ints = { 1 };
		long[] longs = { 1L };
		short[] shorts = { 1 };
		Enum[] enums = { Enum.FIRST };
		int[][] arrays = { INT_ARRAY };
		Object[] objects = { OBJECT };
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllArrayTypesContainer)) {
				return false;
			}
			AllArrayTypesContainer other = (AllArrayTypesContainer)obj;
			return
					Arrays.equals(booleans, other.booleans) &&
					Arrays.equals(bytes, other.bytes) &&
					Arrays.equals(chars, other.chars) &&
					Arrays.equals(doubles, other.doubles) &&
					Arrays.equals(floats, other.floats) &&
					Arrays.equals(ints, other.ints) &&
					Arrays.equals(longs, other.longs) &&
					Arrays.equals(shorts, other.shorts) &&
					Arrays.equals(enums, other.enums) &&
					Arrays.equals(arrays, other.arrays) &&
					Arrays.equals(objects, other.objects);
		}
	}
	
	private static final class AllArrayBoxedTypesContainer {
		Byte[] bytes = { 0 };
		Character[] chars = { '\u0000' };
		Double[] doubles = { 0.0D };
		Float[] floats = { 0.0F };
		Integer[] ints = { 0 };
		Long[] longs = { 0L };
		Short[] shorts = { 0 };
		String[] strings = { "" };
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllArrayBoxedTypesContainer)) {
				return false;
			}
			AllArrayBoxedTypesContainer other = (AllArrayBoxedTypesContainer)obj;
			return
					Arrays.equals(bytes, other.bytes) &&
					Arrays.equals(chars, other.chars) &&
					Arrays.equals(doubles, other.doubles) &&
					Arrays.equals(floats, other.floats) &&
					Arrays.equals(ints, other.ints) &&
					Arrays.equals(longs, other.longs) &&
					Arrays.equals(shorts, other.shorts) &&
					Arrays.equals(strings, other.strings);
		}
	}
	
	private static final class AbstractAndInterfaceArrayContainer {
		AbstractClass[] abstractClasses = new AbstractClass[] { null };
		Interface[] interfaces = new Interface[] { null };
	}
	
	private static abstract class AbstractClass {}
	private static interface Interface {}
}
