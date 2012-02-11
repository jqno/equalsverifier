/*
 * Copyright 2009-2010,2012 Jan Ouwens
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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class SymmetryTest {
	private static final String SYMMETRY = "Symmetry";
	private static final String NOT_SYMMETRIC = "objects are not symmetric";
	private static final String AND = "and";

	@Test
	public void symmetryEquals() {
		EqualsVerifier<SymmetryEqualsBrokenPoint> ev = EqualsVerifier.forClass(SymmetryEqualsBrokenPoint.class);
		assertFailure(ev, SYMMETRY, NOT_SYMMETRIC, AND, SymmetryEqualsBrokenPoint.class.getSimpleName());
	}
	
	@Test
	public void symmetryNotEquals() {
		EqualsVerifier<SymmetryNotEqualsBrokenPoint> ev = EqualsVerifier.forClass(SymmetryNotEqualsBrokenPoint.class);
		assertFailure(ev, SYMMETRY, NOT_SYMMETRIC, AND, SymmetryNotEqualsBrokenPoint.class.getSimpleName());
	}
	
	private static int seed = 1;
	private static final Set<Integer> seeded = new HashSet<Integer>();
	
	static class SymmetryEqualsBrokenPoint extends SymmetryBrokenPoint {
		private int n = 0;
		
		public SymmetryEqualsBrokenPoint(int x, int y) {
			super(x, y);
		}
		
		// In the symmetry test, setN will make sure that n is different for
		// both objects, which will cause this test to fail.
		// In the reflexivity test, n is copied along with the rest of the
		// object, and therefore the instances will have equal n's, allowing
		// the reflexivity test to pass.
		// The seed has to be defined outside this class, or else
		// EqualsVerifier will make changes.
		private void setN() {
			if (!seeded.contains(hashCode())) {
				seeded.add(hashCode());
				seed++;
				n = seed;
			}
		}

		@Override
		public boolean equals(Object obj) {
			setN();
			
			if (obj != this && goodEquals(obj)) {
				return n == ((SymmetryEqualsBrokenPoint)obj).n;
			}
			return goodEquals(obj);
		}
	}
	
	static class SymmetryNotEqualsBrokenPoint extends SymmetryBrokenPoint {
		public SymmetryNotEqualsBrokenPoint(int x, int y) {
			super(x, y);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (goodEquals(obj)) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			return hashCode() > obj.hashCode();
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
	}
	
	abstract static class SymmetryBrokenPoint {
		public final int x;
		public final int y;

		public SymmetryBrokenPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean goodEquals(Object obj) {
			if (!(obj instanceof SymmetryBrokenPoint)) {
				return false;
			}
			SymmetryBrokenPoint p = (SymmetryBrokenPoint)obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + x + "," + y;
		}
	}
}
