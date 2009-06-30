/*
 * Copyright 2009 Jan Ouwens
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class PrefabInstantiatorTest {
	@Test
	public void addPrefabValue() {
		Instantiator<PointContainer> instantiator = Instantiator.forClass(PointContainer.class);
		instantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(2, 3));
		
		PointContainer pc = new PointContainer();
		instantiator.scramble(pc);
	}
	
	@Test
	public void addPrefabArrayValue() {
		Instantiator<PointArrayContainer> instantiator = Instantiator.forClass(PointArrayContainer.class);
		instantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(2, 3));
		
		PointArrayContainer pc = new PointArrayContainer();
		instantiator.scramble(pc);
	}
	
	@Test
	public void dontAddPrefabValue() {
		Instantiator<PointContainer> instantiator = Instantiator.forClass(PointContainer.class);
		PointContainer pc = new PointContainer();
		try {
			instantiator.scramble(pc);
			fail("No exception thrown.");
		}
		catch (IllegalStateException e) {
			assertEquals("No values for class nl.jqno.equalsverifier.points.Point.", e.getMessage());
		}
	}
	
	@Test
	public void addNullClass() {
		Instantiator<PointContainer> instantiator = Instantiator.forClass(PointContainer.class);
		try {
			instantiator.addPrefabValues(null, new Point(1, 2), new Point(2, 3));
			fail("No exception thrown.");
		}
		catch (NullPointerException e) {
			assertEquals("klass is null.", e.getMessage());
		}
	}
	
	@Test
	public void addNullPrefabValues() {
		Instantiator<PointContainer> instantiator = Instantiator.forClass(PointContainer.class);
		try {
			instantiator.addPrefabValues(Point.class, null, new Point(1, 2));
			fail("No exception thrown.");
		}
		catch (NullPointerException e) {
			assertEquals("Added null prefab value.", e.getMessage());
		}
		try {
			instantiator.addPrefabValues(Point.class, new Point(1, 2), null);
			fail("No exception thrown.");
		}
		catch (NullPointerException e) {
			assertEquals("Added null prefab value.", e.getMessage());
		}
		
	}
	
	@Test
	public void addEqualPrefabValues() {
		Instantiator<PointContainer> instantiator = Instantiator.forClass(PointContainer.class);
		try {
			instantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(1, 2));
			fail("No exception thrown.");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Added equal prefab values: Point:1,2.", e.getMessage());
		}
	}
	
	@Test
	public void copyPrefabValues() {
		Instantiator<AllTypesContainer> firstInstantiator = Instantiator.forClass(AllTypesContainer.class);
		firstInstantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(2, 3));
		
		Instantiator<PointContainer> secondInstantiator = Instantiator.forClass(PointContainer.class);
		secondInstantiator.copyPrefabValues(firstInstantiator);
		
		PointContainer pc = new PointContainer();
		secondInstantiator.scramble(pc);
	}
	
	@Test
	public void changeFieldPrefabValues() {
		Instantiator<AllTypesContainer> instantiator = Instantiator.forClass(AllTypesContainer.class);
		AllTypesContainer atc = new AllTypesContainer();
		List<Object> objects = new ArrayList<Object>();
		objects.add(atc._object);
		
		instantiator.scramble(atc);
		assertEquals("one", atc._string);
		assertTrue(true == atc._Boolean);
		assertTrue(1 == atc._Byte);
		assertTrue('a' == atc._Char);
		assertTrue(Double.compare(0.5D, atc._Double) == 0);
		assertTrue(Float.compare(0.5F, atc._Float) == 0);
		assertTrue(1 == atc._Int);
		assertTrue(1L == atc._Long);
		assertTrue(1 == atc._Short);
		assertFalse(objects.contains(atc._object));
		objects.add(atc._object);
		
		instantiator.scramble(atc);
		assertEquals("two", atc._string);
		assertTrue(false == atc._Boolean);
		assertTrue(2 == atc._Byte);
		assertTrue('b' == atc._Char);
		assertTrue(Double.compare(1.0D, atc._Double) == 0);
		assertTrue(Float.compare(1.0F, atc._Float) == 0);
		assertTrue(2 == atc._Int);
		assertTrue(2L == atc._Long);
		assertTrue(2 == atc._Short);
		assertFalse(objects.contains(atc._object));
		objects.add(atc._object);
		
		instantiator.scramble(atc);
		assertEquals("one", atc._string);
		assertTrue(true == atc._Boolean);
		assertTrue(1 == atc._Byte);
		assertTrue('a' == atc._Char);
		assertTrue(Double.compare(0.5D, atc._Double) == 0);
		assertTrue(Float.compare(0.5F, atc._Float) == 0);
		assertTrue(1 == atc._Int);
		assertTrue(1L == atc._Long);
		assertTrue(1 == atc._Short);
		assertTrue(objects.get(1) == atc._object);
	}
	
	@Test
	public void changePrefabArrayElement() {
		Instantiator<AllArrayBoxedTypesContainer> instantiator = Instantiator.forClass(AllArrayBoxedTypesContainer.class);
		AllArrayBoxedTypesContainer aabtc = new AllArrayBoxedTypesContainer();
		
		changePrefabArrayElementThreeTimes(instantiator, aabtc.strings, "one", "two");
		changePrefabArrayElementThreeTimes(instantiator, aabtc.bytes, (byte)1, (byte)2);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.chars, 'a', 'b');
		changePrefabArrayElementThreeTimes(instantiator, aabtc.doubles, 0.5D, 1.0D);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.floats, 0.5F, 1.0F);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.ints, 1, 2);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.longs, 1L, 2L);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.shorts, (short)1, (short)2);
	}
	
	private void changePrefabArrayElementThreeTimes(Instantiator<?> instantiator, Object array, Object... values) {
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[0], Array.get(array, 0));
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[1], Array.get(array, 0));
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[0], Array.get(array, 0));
	}
	
	private static final class PointContainer {
		Point point = new Point(1, 2);
	}
	
	private static final class PointArrayContainer {
		Point[] point = { new Point(1, 2) };
	}
	
	private static final class AllTypesContainer {
		Boolean _Boolean = false;
		Byte _Byte = 0;
		Character _Char = '\u0000';
		Double _Double = 0.0D;
		Float _Float = 0.0F;
		Integer _Int = 0;
		Long _Long = 0L;
		Short _Short = 0;
		
		Object _object = new Object();
		String _string = "";
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllTypesContainer)) {
				return false;
			}
			AllTypesContainer other = (AllTypesContainer)obj;
			return
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
					_object == other._object &&
					(_string == null ? other._string == null : _string.equals(other._string));
		}
	}
	
	private static final class AllArrayBoxedTypesContainer {
		Boolean[] booleans = { false };
		Byte[] bytes = { 0 };
		Character[] chars = { '\u0000' };
		Double[] doubles = { 0.0D };
		Float[] floats = { 0.0F };
		Integer[] ints = { 0 };
		Long[] longs = { 0L };
		Short[] shorts = { 0 };
		String[] strings = { "" };
		Object[] objects = { new Object() };
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllArrayBoxedTypesContainer)) {
				return false;
			}
			AllArrayBoxedTypesContainer other = (AllArrayBoxedTypesContainer)obj;
			return
					Arrays.equals(booleans, other.booleans) &&
					Arrays.equals(bytes, other.bytes) &&
					Arrays.equals(chars, other.chars) &&
					Arrays.equals(doubles, other.doubles) &&
					Arrays.equals(floats, other.floats) &&
					Arrays.equals(ints, other.ints) &&
					Arrays.equals(longs, other.longs) &&
					Arrays.equals(shorts, other.shorts) &&
					Arrays.equals(strings, other.strings) &&
					Arrays.equals(objects, other.objects);
		}
	}
}
