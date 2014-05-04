/*
 * Copyright 2009-2010, 2013 Jan Ouwens
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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AbstractEqualsAndHashCode;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.Empty;
import nl.jqno.equalsverifier.testhelpers.points.CanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.points.Color;
import nl.jqno.equalsverifier.testhelpers.points.ColorBlindColorPoint;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Test;

public class SuperclassTest {
	@Test
	public void happyPath() {
		EqualsVerifier.forClass(ColorBlindColorPoint.class).verify();
	}
	
	@Test
	public void symmetry() {
		EqualsVerifier<SymmetryBrokenColorPoint> ev =
				EqualsVerifier.forClass(SymmetryBrokenColorPoint.class);
		assertFailure(ev, "Symmetry", SymmetryBrokenColorPoint.class.getSimpleName(), "does not equal superclass instance", Point.class.getSimpleName());
	}
	
	@Test
	public void transitivity() {
		EqualsVerifier<TransitivityBrokenColorPoint> ev =
				EqualsVerifier.forClass(TransitivityBrokenColorPoint.class);
		assertFailure(ev, "Transitivity",
				"TransitivityBrokenColorPoint:1,1,YELLOW\nand\n  TransitivityBrokenColorPoint:1,1,BLUE",
				"both equal superclass instance",
				"Point:1,1",
				"which implies they equal each other.");
	}

	@Test
	public void referenceAndSuperSameHaveSameHashCode() {
		EqualsVerifier<HashCodeBrokenPoint> ev =
				EqualsVerifier.forClass(HashCodeBrokenPoint.class);
		assertFailure(ev, "Superclass", "hashCode for",	HashCodeBrokenPoint.class.getSimpleName(),
				"should be equal to hashCode for superclass instance", Point.class.getSimpleName());
	}
	
	@Test
	public void emptySuperclassesShouldNotFailOnEqualSuperclassInstance() {
		EqualsVerifier.forClass(SubclassOfEmpty.class).verify();
		EqualsVerifier.forClass(SubOfEmptySubOfEmpty.class).verify();
		EqualsVerifier.forClass(SubOfEmptySubOfAbstract.class).verify();
	}
	
	@Test
	public void emptySuperclassIsIrrelevantWhenTheresEqualsHigherUp() {
		EqualsVerifier<BrokenCanEqualColorPointWithEmptySuper> ev =
				EqualsVerifier.forClass(BrokenCanEqualColorPointWithEmptySuper.class);
		assertFailure(ev, "Symmetry", BrokenCanEqualColorPointWithEmptySuper.class.getSimpleName());
	}
	
	@Test
	public void invalidWithRedefinedSuperclass() {
		EqualsVerifier<ColorBlindColorPoint> ev = EqualsVerifier.forClass(ColorBlindColorPoint.class)
				.withRedefinedSuperclass();
		assertFailure(ev, "Redefined superclass", ColorBlindColorPoint.class.getSimpleName(),
				"should not equal superclass instance", Point.class.getSimpleName(), "but it does");
	}
	
	static class SymmetryBrokenColorPoint extends Point {
		private final Color color;
		
		public SymmetryBrokenColorPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SymmetryBrokenColorPoint)) {
				return false;
			}
			SymmetryBrokenColorPoint p = (SymmetryBrokenColorPoint)obj;
			return super.equals(obj) && p.color == color;
		}
		
		@Override
		public String toString() {
			return super.toString() + "," + color;
		}
	}

	static class TransitivityBrokenColorPoint extends Point {
		private final Color color;

		public TransitivityBrokenColorPoint(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Point)) {
				return false;
			}
			if (!(obj instanceof TransitivityBrokenColorPoint)) {
				return obj.equals(this);
			}
			TransitivityBrokenColorPoint p = (TransitivityBrokenColorPoint)obj;
			return super.equals(obj) && p.color == color;
		}
		
		@Override
		public String toString() {
			return super.toString() + "," + color;
		}
	}
	
	static class HashCodeBrokenPoint extends Point {
		public HashCodeBrokenPoint(int x, int y) {
			super(x, y);
		}

		@Override
		public int hashCode() {
			return super.hashCode() + 1;
		}
	}
	
	static final class SubclassOfEmpty extends Empty {
		private final Color color;
		
		public SubclassOfEmpty(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubclassOfEmpty)) {
				return false;
			}
			return color == ((SubclassOfEmpty)obj).color;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
	}
	
	static class EmptySubOfEmpty extends Empty {}
	
	static final class SubOfEmptySubOfEmpty extends EmptySubOfEmpty {
		private final Color color;
		
		public SubOfEmptySubOfEmpty(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubOfEmptySubOfEmpty)) {
				return false;
			}
			return color == ((SubOfEmptySubOfEmpty)obj).color;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
	}
	
	static abstract class EmptySubOfAbstract extends AbstractEqualsAndHashCode {}
	
	static final class SubOfEmptySubOfAbstract extends EmptySubOfAbstract {
		private final Color color;
		
		public SubOfEmptySubOfAbstract(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubOfEmptySubOfAbstract)) {
				return false;
			}
			return color == ((SubOfEmptySubOfAbstract)obj).color;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
	}
	
	static class EmptySubOfCanEqualPoint extends CanEqualPoint {
		public EmptySubOfCanEqualPoint(int x, int y) {
			super(x, y);
		}
	}
	
	static final class BrokenCanEqualColorPointWithEmptySuper extends EmptySubOfCanEqualPoint {
		private final Color color;
		
		public BrokenCanEqualColorPointWithEmptySuper(int x, int y, Color color) {
			super(x, y);
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof BrokenCanEqualColorPointWithEmptySuper)) {
				return false;
			}
			BrokenCanEqualColorPointWithEmptySuper p = (BrokenCanEqualColorPointWithEmptySuper)obj;
			return super.equals(p) && color == p.color;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color) + (31 * super.hashCode());
		}
	}
}
