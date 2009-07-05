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
package nl.jqno.equalsverifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class PrefabEqualsVerifierTest {
	@Test
	public void happy() {
		EqualsVerifier.forClass(PointContainer.class)
				.withPrefabValues(Point.class, new Point(1, 2), new Point(2, 3))
				.verify();
	}
	
	@Test
	public void dontAddPrefabValues() {
		try {
			EqualsVerifier.forClass(PointContainer.class).verify();
			fail("No exception thrown.");
		}
		catch (IllegalStateException e) {
			assertEquals("No values for class nl.jqno.equalsverifier.points.Point.", e.getMessage());
		}
	}
	
	@Test
	public void testSuper() {
		EqualsVerifier.forClass(PointContainerSub.class)
				.withPrefabValues(Point.class, new Point(1, 2), new Point(2, 3))
				.verify();
	}
	
	@Test
	public void testRedefinable() {
		EqualsVerifier.forClass(RedefinablePointContainer.class)
				.withRedefinedSubclass(RedefinedPointContainer.class)
				.withPrefabValues(Point.class, new Point(1, 2), new Point(2, 3))
				.verify();
	}
	
	@Test
	public void testRedefined() {
		EqualsVerifier.forClass(RedefinedPointContainer.class)
				.with(Feature.REDEFINED_SUPERCLASS)
				.withPrefabValues(Point.class, new Point(1, 2), new Point(2, 3))
				.verify();
	}
	
	private static class PointContainer {
		public final Point point;
		
		public PointContainer(Point point) {
			this.point = point;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof PointContainer)) {
				return false;
			}
			PointContainer other = (PointContainer)obj;
			return point == null ? other.point == null : point.equals(other.point);
		}
		
		@Override
		public final int hashCode() {
			return point == null ? 0 : point.hashCode();
		}
	}
	
	private static final class PointContainerSub extends PointContainer {
		public PointContainerSub(Point point) {
			super(point);
		}
	}
	
	private static class RedefinablePointContainer {
		private final Point point;
		
		public RedefinablePointContainer(Point point) {
			this.point = point;
		}
		
		public boolean canEqual(Object obj) {
			return obj instanceof RedefinablePointContainer;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof RedefinablePointContainer)) {
				return false;
			}
			RedefinablePointContainer other = (RedefinablePointContainer)obj;
			return other.canEqual(this) &&
					(point == null ? other.point == null : point.equals(other.point));
		}
		
		@Override
		public int hashCode() {
			return point == null ? 0 : point.hashCode();
		}
		
		@Override
		public String toString() {
			return getClass() + ":" + point;
		}
	}
	
	private static final class RedefinedPointContainer extends RedefinablePointContainer {
		private final Point anotherPoint;
		
		public RedefinedPointContainer(Point point, Point anotherPoint) {
			super(point);
			this.anotherPoint = anotherPoint;
		}
		
		@Override
		public boolean canEqual(Object obj) {
			return obj instanceof RedefinedPointContainer;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof RedefinedPointContainer)) {
				return false;
			}
			RedefinedPointContainer other = (RedefinedPointContainer)obj;
			return other.canEqual(this) && super.equals(other) && 
					(anotherPoint == null ? other.anotherPoint == null : anotherPoint.equals(other.anotherPoint));
		}
		
		@Override
		public int hashCode() {
			return (anotherPoint == null ? 0 : anotherPoint.hashCode())	+ (31 * super.hashCode());
		}
		
		@Override
		public String toString() {
			return super.toString() + "," + anotherPoint;
		}
	}
}
