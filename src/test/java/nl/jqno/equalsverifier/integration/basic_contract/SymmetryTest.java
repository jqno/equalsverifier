/*
 * Copyright 2009-2010, 2012, 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.basic_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Test;

public class SymmetryTest extends IntegrationTestBase {
	private static final String SYMMETRY = "Symmetry";
	private static final String NOT_SYMMETRIC = "objects are not symmetric";
	private static final String AND = "and";

	@Test
	public void fail_whenEqualsIsNotSymmetrical() {
		expectFailure(SYMMETRY, NOT_SYMMETRIC, AND, SymmetryIntentionallyBroken.class.getSimpleName());
		EqualsVerifier.forClass(SymmetryIntentionallyBroken.class)
				.verify();
	}
	
	static final class SymmetryIntentionallyBroken {
		private final int x;
		private final int y;
		
		public SymmetryIntentionallyBroken(int x, int y) { this.x = x; this.y = y; }
		
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
			if (!(obj instanceof SymmetryIntentionallyBroken)) {
				return false;
			}
			SymmetryIntentionallyBroken p = (SymmetryIntentionallyBroken)obj;
			return p.x == x && p.y == y;
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
