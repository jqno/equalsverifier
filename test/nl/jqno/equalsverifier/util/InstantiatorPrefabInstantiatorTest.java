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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import nl.jqno.equalsverifier.points.Point;
import nl.jqno.equalsverifier.util.InstantiatorFacade;
import nl.jqno.equalsverifier.util.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllTypesContainer;

import org.junit.Test;

public class InstantiatorPrefabInstantiatorTest {
	@Test
	public void dontAddPrefabValue() {
		InstantiatorFacade<PointContainer> instantiator = InstantiatorFacade.forClass(PointContainer.class);
		PointContainer pc = new PointContainer();
		Point p = pc.point;
		
		instantiator.scramble(pc);
		assertFalse(p.equals(pc.point));
	}

	@Test
	public void dontAddPrefabArrayValue() {
		InstantiatorFacade<PointArrayContainer> instantiator = InstantiatorFacade.forClass(PointArrayContainer.class);
		PointArrayContainer pc = new PointArrayContainer();
		Point p = pc.point[0];
		
		instantiator.scramble(pc);
		assertFalse(p.equals(pc.point));
	}
	
	@Test
	public void addPrefabValue() {
		InstantiatorFacade<PointContainer> instantiator = InstantiatorFacade.forClass(PointContainer.class);
		instantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(2, 3));
		
		PointContainer pc = new PointContainer();
		assertEquals(pc.point, new Point(1, 2));

		instantiator.scramble(pc);
		assertEquals(pc.point, new Point(2, 3));
	}
	
	@Test
	public void addPrefabArrayValue() {
		InstantiatorFacade<PointArrayContainer> instantiator = InstantiatorFacade.forClass(PointArrayContainer.class);
		instantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(2, 3));
		
		PointArrayContainer pc = new PointArrayContainer();
		assertEquals(pc.point[0], new Point(1, 2));

		instantiator.scramble(pc);
		assertEquals(pc.point[0], new Point(2, 3));
	}
	
	@Test
	public void addNullClass() {
		InstantiatorFacade<PointContainer> instantiator = InstantiatorFacade.forClass(PointContainer.class);
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
		InstantiatorFacade<PointContainer> instantiator = InstantiatorFacade.forClass(PointContainer.class);
		
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
		InstantiatorFacade<PointContainer> instantiator = InstantiatorFacade.forClass(PointContainer.class);
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
		InstantiatorFacade<AllTypesContainer> firstInstantiator = InstantiatorFacade.forClass(AllTypesContainer.class);
		firstInstantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(2, 3));
		
		InstantiatorFacade<PointContainer> secondInstantiator = InstantiatorFacade.forClass(PointContainer.class);
		secondInstantiator.copyPrefabValues(firstInstantiator);
		
		PointContainer pc = new PointContainer();
		secondInstantiator.scramble(pc);
	}
	
	@Test
	public void changeFieldPrefabValues() {
		InstantiatorFacade<AllTypesContainer> instantiator = InstantiatorFacade.forClass(AllTypesContainer.class);
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
		InstantiatorFacade<AllArrayTypesContainer> instantiator = InstantiatorFacade.forClass(AllArrayTypesContainer.class);
		AllArrayTypesContainer aabtc = new AllArrayTypesContainer();
		
		changePrefabArrayElementThreeTimes(instantiator, aabtc.strings, "one", "two");
		changePrefabArrayElementThreeTimes(instantiator, aabtc.Bytes, (byte)2, (byte)1);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.Characters, 'b', 'a');
		changePrefabArrayElementThreeTimes(instantiator, aabtc.Doubles, 0.5D, 1.0D);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.Floats, 0.5F, 1.0F);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.Integers, 2, 1);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.Longs, 2L, 1L);
		changePrefabArrayElementThreeTimes(instantiator, aabtc.Shorts, (short)2, (short)1);
	}
	
	private void changePrefabArrayElementThreeTimes(InstantiatorFacade<?> instantiator, Object array, Object... values) {
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[0], Array.get(array, 0));
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[1], Array.get(array, 0));
		instantiator.changeArrayElement(array, 0);
		assertEquals(values[0], Array.get(array, 0));
	}
	
	static final class PointContainer {
		Point point = new Point(1, 2);
	}
	
	static final class PointArrayContainer {
		Point[] point = { new Point(1, 2) };
	}
}
