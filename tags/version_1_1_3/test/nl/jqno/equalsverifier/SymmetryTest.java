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

import org.junit.Test;

public class SymmetryTest {
	private static final String SYMMETRY = "Symmetry";
	private static final String NOT_SYMMETRIC = "objects are not symmetric";
	private static final String AND = "and";

	@Test
	public void symmetryNotEquals() {
		EqualsVerifier<SymmetryBrokenPoint> ev = EqualsVerifier.forClass(SymmetryBrokenPoint.class);
		assertFailure(ev, SYMMETRY, NOT_SYMMETRIC, AND, SymmetryBrokenPoint.class.getSimpleName());
	}
	
	abstract static class SymmetryBrokenPoint {
		public final int x;
		public final int y;

		public SymmetryBrokenPoint(int x, int y) {
			this.x = x;
			this.y = y;
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

		public boolean goodEquals(Object obj) {
			if (!(obj instanceof SymmetryBrokenPoint)) {
				return false;
			}
			SymmetryBrokenPoint p = (SymmetryBrokenPoint)obj;
			return p.x == x && p.y == y;
		}
		
		@Override
		public int hashCode() {
			return x + (31 * y);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + ":" + x + "," + y;
		}
	}
}
