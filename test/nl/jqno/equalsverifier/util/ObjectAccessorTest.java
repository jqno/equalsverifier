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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.points.ColorPoint3D;
import nl.jqno.equalsverifier.points.Point;
import nl.jqno.equalsverifier.points.Point3D;
import nl.jqno.equalsverifier.util.InstantiatorCloningAndScramblingTest.PointContainer;
import nl.jqno.equalsverifier.util.InstantiatorCloningAndScramblingTest.SubPointContainer;
import nl.jqno.equalsverifier.util.TypeHelper.StaticFinalContainer;

import org.junit.Test;

public class ObjectAccessorTest {
	@Test
	public void constructInvalidClass() {
		ObjectAccessor.of("string", Object.class);
	}
	
	@Test
	public void cloneHappyPath() {
		Point original = new Point(2, 3);
		Point clone = cloneOf(original);
		
		assertNotSame(original, clone);
		assertAllFieldsEqual(original, clone, Point.class);
	}
	
	@Test
	public void shallowClone() {
		PointContainer original = new PointContainer(new Point(1, 2));
		PointContainer clone = cloneOf(original);
		
		assertNotSame(original, clone);
		assertTrue(original.point == clone.point);
	}
	
	@Test
	public void cloneStaticFinal() {
		StaticFinalContainer foo = new StaticFinalContainer();
		cloneOf(foo);
	}
	
	@Test
	public void inheritanceClone() {
		Point3D original = new Point3D(2, 3, 4);
		Point3D clone = cloneOf(original);

		assertNotSame(original, clone);
		assertAllFieldsEqual(original, clone, Point.class);
		assertAllFieldsEqual(original, clone, Point3D.class);
	}
	
	@Test
	public void cloneFromSub() {
		Point3D original = new Point3D(2, 3, 4);
		Point clone = cloneOf(original, Point.class);
		
		assertEquals(Point.class, clone.getClass());
		assertNotSame(original, clone);
		assertAllFieldsEqual(original, clone, Point.class);
	}
	
	@Test
	public void cloneToSub() {
		Point original = new Point(2, 3);
		Point3D clone = cloneIntoSubclass(original, Point3D.class);
		
		assertNotSame(original, clone);
		assertAllFieldsEqual(original, clone, Point.class);
	}

	@Test
	public void shallowCloneToSub() {
		PointContainer original = new PointContainer(new Point(1, 2));
		SubPointContainer clone = cloneIntoSubclass(original, SubPointContainer.class);
		
		assertNotSame(original, clone);
		assertTrue(original.point == clone.point);
	}
	
	@Test
	public void inheritanceCloneToSub() {
		Point3D original = new Point3D(2, 3, 4);
		Point3D clone = cloneIntoSubclass(original, ColorPoint3D.class);

		assertNotSame(original, clone);
		assertAllFieldsEqual(original, clone, Point.class);
		assertAllFieldsEqual(original, clone, Point3D.class);
	}

	@Test
	public void cloneToAnonymousSub() {
		Point original = new Point(2, 3);
		ObjectAccessor<Point> accessor = ObjectAccessor.of(original);
		Point clone = accessor.cloneIntoAnonymousSubclass();
		
		assertNotSame(original, clone);
		assertAllFieldsEqual(original, clone, Point.class);
		
		assertNotSame(original.getClass(), clone.getClass());
		assertTrue(original.getClass().isAssignableFrom(clone.getClass()));
	}

	private <T> T cloneOf(T from) {
		return ObjectAccessor.of(from).clone();
	}
	
	private <T> T cloneOf(T from, Class<T> klass) {
		return ObjectAccessor.of(from, klass).clone();
	}
	
	private <T, S extends T> S cloneIntoSubclass(T from, Class<S> subclass) {
		return ObjectAccessor.of(from).cloneIntoSubclass(subclass);
	}
	
	private static <T> void assertAllFieldsEqual(T original, T copy, Class<? extends T> klass) {
		for (Field field : new FieldIterable(klass)) {
			try {
				assertEquals(field.get(original), field.get(copy));
			}
			catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
