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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.points.ColorPoint3D;
import nl.jqno.equalsverifier.points.Point;
import nl.jqno.equalsverifier.points.Point3D;
import nl.jqno.equalsverifier.util.TypeHelper.StaticFinalContainer;

import org.junit.Before;
import org.junit.Test;

public class InstantiatorCloningAndScramblingTest {
	private InstantiatorFacade<Point> instantiator;
	
	@Before
	public void setup() {
		instantiator = InstantiatorFacade.forClass(Point.class);
	}

	@Test
	public void cloneFrom() throws IllegalArgumentException, IllegalAccessException {
		Point original = new Point(2, 3);
		Point clone = instantiator.cloneFrom(original);
		
		assertNotSame(original, clone);
		assertAllFieldsEqual(original, clone, Point.class);
	}
	
	@Test
	public void shallowClone() {
		InstantiatorFacade<PointContainer> instantiator = InstantiatorFacade.forClass(PointContainer.class);
		PointContainer original = new PointContainer(new Point(1, 2));
		PointContainer clone = instantiator.cloneFrom(original);
		
		assertTrue(original.point == clone.point);
	}
	
	@Test
	public void inheritanceClone() throws IllegalArgumentException, IllegalAccessException {
		InstantiatorFacade<Point3D> subInstantiator = InstantiatorFacade.forClass(Point3D.class);
		Point3D original = new Point3D(2, 3, 4);
		Point3D clone = subInstantiator.cloneFrom(original);

		assertAllFieldsEqual(original, clone, Point.class);
		assertAllFieldsEqual(original, clone, Point3D.class);
	}
	
	@Test
	public void cloneFromSub() throws IllegalArgumentException, IllegalAccessException {
		Point3D original = new Point3D(2, 3, 4);
		Point clone = instantiator.cloneFrom(original);
		
		assertEquals(Point.class, clone.getClass());
		assertAllFieldsEqual(original, clone, Point.class);
	}

	@Test
	public void cloneToSub() throws IllegalArgumentException, IllegalAccessException {
		Point original = new Point(2, 3);
		Point clone = instantiator.cloneToSubclass(original, Point3D.class);
		
		assertAllFieldsEqual(original, clone, Point.class);
	}
	
	@Test
	public void shallowCloneToSub() {
		InstantiatorFacade<PointContainer> instantiator = InstantiatorFacade.forClass(PointContainer.class);
		PointContainer original = new PointContainer(new Point(1, 2));
		SubPointContainer clone = instantiator.cloneToSubclass(original, SubPointContainer.class);
		
		assertTrue(original.point == clone.point);
	}
	
	@Test
	public void inheritanceCloneToSub() throws IllegalArgumentException, IllegalAccessException {
		InstantiatorFacade<Point3D> subInstantiator = InstantiatorFacade.forClass(Point3D.class);
		Point3D original = new Point3D(2, 3, 4);
		Point3D clone = subInstantiator.cloneToSubclass(original, ColorPoint3D.class);

		assertAllFieldsEqual(original, clone, Point.class);
		assertAllFieldsEqual(original, clone, Point3D.class);
	}
	
	@Test
	public void cloneStaticFinal() {
		InstantiatorFacade<StaticFinalContainer> sfInstantiator = InstantiatorFacade.forClass(StaticFinalContainer.class);
		StaticFinalContainer sf = new StaticFinalContainer();
		sfInstantiator.cloneFrom(sf);
	}

	@Test
	public void scramble() {
		Point original = new Point(2, 3);
		Point clone = instantiator.cloneFrom(original);
		instantiator.scramble(clone);
		assertFalse(original.equals(clone));
	}
	
	@Test
	public void deepScramble() {
		InstantiatorFacade<Point3D> subInstantiator = InstantiatorFacade.forClass(Point3D.class);
		Point3D original = new Point3D(2, 3, 4);
		Point3D clone = subInstantiator.cloneFrom(original);
		subInstantiator.scramble(clone);
		clone.z = 4;
		assertFalse(original.equals(clone));
	}
	
	@Test
	public void shallowScramble() {
		InstantiatorFacade<Point3D> subInstantiator = InstantiatorFacade.forClass(Point3D.class);
		Point3D original = new Point3D(2, 3, 4);
		Point3D clone = subInstantiator.cloneFrom(original);
		subInstantiator.shallowScramble(clone);
		assertFalse(original.equals(clone));
		clone.z = 4;
		assertTrue(original.equals(clone));
	}
	
	@Test
	public void scrambleStaticFinal() {
		InstantiatorFacade<StaticFinalContainer> sfInstantiator = InstantiatorFacade.forClass(StaticFinalContainer.class);
		StaticFinalContainer sf = new StaticFinalContainer();
		sfInstantiator.scramble(sf);
	}
	
	@Test
	public void scrambleString() {
		InstantiatorFacade<StringContainer> scInstantiator = InstantiatorFacade.forClass(StringContainer.class);
		StringContainer sc = new StringContainer();
		String before = sc.s;
		scInstantiator.scramble(sc);
		assertFalse(before.equals(sc.s));
	}
	
	@Test
	public void scrambleUnscramblableString() {
		InstantiatorFacade<FinalAssignedStringContainer> fascInstantiator = InstantiatorFacade.forClass(FinalAssignedStringContainer.class);
		FinalAssignedStringContainer fasc = new FinalAssignedStringContainer();
		String before = fasc.s;
		fascInstantiator.scramble(fasc);
		assertEquals(before, fasc.s);
	}
	
	@Test
	public void scrambleUnscramblablePoint() {
		InstantiatorFacade<FinalAssignedPointContainer> fapcInstantiator = InstantiatorFacade.forClass(FinalAssignedPointContainer.class);
		fapcInstantiator.addPrefabValues(Point.class, new Point(1, 2), new Point(2, 3));
		
		FinalAssignedPointContainer fapc = new FinalAssignedPointContainer();
		Point before = fapc.p;
		assertTrue(before.equals(fapc.p));
		fapcInstantiator.scramble(fapc);
		assertFalse(before.equals(fapc.p));
	}

	private static <T> void assertAllFieldsEqual(T original, T clone, Class<? extends T> klass) throws IllegalAccessException {
		for (Field field : klass.getDeclaredFields()) {
			if (InstantiatorFacade.canBeModifiedReflectively(field)) {
				assertEquals(field.get(original), field.get(clone));
			}
		}
	}
	
	static class PointContainer {
		protected final Point point;
		
		public PointContainer(Point point) {
			this.point = point;
		}
	}
	
	static class SubPointContainer extends PointContainer {
		public SubPointContainer(Point point) {
			super(point);
		}
	}
	
	static final class FinalAssignedPointContainer {
		private final Point p = new Point(2, 3);
	}
	
	static final class StringContainer {
		private String s = "x";
	}
	
	static final class FinalAssignedStringContainer {
		private final String s = "x";
	}
}
