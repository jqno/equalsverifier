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
package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.points.Color;
import nl.jqno.equalsverifier.points.ColorBlindColorPoint;
import nl.jqno.equalsverifier.points.Point;

import org.junit.Test;

public class SuperclassTest extends EqualsVerifierTestBase {
	@Test
	public void happyPath() {
		EqualsVerifier.forClass(ColorBlindColorPoint.class).verify();
	}
	
	@Test
	public void symmetry() {
		EqualsVerifier<SymmetryBrokenColorPoint> ev =
				EqualsVerifier.forClass(SymmetryBrokenColorPoint.class);
		verifyFailure("Symmetry:\n  SymmetryBrokenColorPoint:1,1,YELLOW\ndoes not equal superclass instance\n  Point:1,1", ev);
	}
	
	@Test
	public void transitivity() {
		EqualsVerifier<TransitivityBrokenColorPoint> ev =
				EqualsVerifier.forClass(TransitivityBrokenColorPoint.class);
		verifyFailure("Transitivity:\n  TransitivityBrokenColorPoint:1,1,YELLOW\nand\n  " +
				"TransitivityBrokenColorPoint:1,1,BLUE\nboth equal superclass instance\n  " +
				"Point:1,1\nwhich implies they equal each other.",
				ev);
	}

	@Test
	public void referenceAndSuperSameHaveSameHashCode() {
		EqualsVerifier<HashCodeBrokenPoint> ev =
				EqualsVerifier.forClass(HashCodeBrokenPoint.class);
		verifyFailure("Superclass: hashCode for\n  HashCodeBrokenPoint:1,1 (33)" +
				"\nshould be equal to hashCode for superclass instance\n  Point:1,1 (32)", ev);
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
}
