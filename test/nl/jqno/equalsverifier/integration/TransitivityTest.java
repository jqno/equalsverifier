/*
 * Copyright 2013-2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Ignore;
import org.junit.Test;

public class TransitivityTest {
	@Test
	public void succeed_whenEqualityForTwoFieldsIsCombinedUsingAND() {
		EqualsVerifier.forClass(TwoFieldsUsingAND.class).verify();
	}
	
	@Test
	public void fail_whenEqualityForTwoFieldsIsCombinedUsingOR() {
		EqualsVerifier<TwoFieldsUsingOR> ev = EqualsVerifier.forClass(TwoFieldsUsingOR.class);
		assertFailure(ev, "Transitivity", "two of these three instances are equal to each other, so the third one should be, too", TwoFieldsUsingOR.class.getSimpleName());
	}
	
	@Test
	public void succeed_whenEqualityForThreeFieldsIsCombinedUsingAND() {
		EqualsVerifier.forClass(ThreeFieldsUsingAND.class).verify();
	}
	
	@Test
	public void fail_whenEqualityForThreeFieldsIsCombinedUsingOR() {
		EqualsVerifier<ThreeFieldsUsingOR> ev = EqualsVerifier.forClass(ThreeFieldsUsingOR.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void fail_whenEqualityForThreeFieldsIsCombinedUsingOR_givenRelaxedEqualExamples() {
		ThreeFieldsUsingOR one = new ThreeFieldsUsingOR("a", "1", "alpha");
		ThreeFieldsUsingOR two = new ThreeFieldsUsingOR("b", "1", "alpha");
		ThreeFieldsUsingOR three = new ThreeFieldsUsingOR("c", "1", "alpha");
		ThreeFieldsUsingOR other = new ThreeFieldsUsingOR("d", "4", "delta");
		
		EqualsVerifier<ThreeFieldsUsingOR> ev = EqualsVerifier.forRelaxedEqualExamples(one, two, three)
				.andUnequalExample(other);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void fail_whenEqualityForThreeFieldsIsCombinedUsingANDAndOR() {
		EqualsVerifier<ThreeFieldsUsingANDOR> ev = EqualsVerifier.forClass(ThreeFieldsUsingANDOR.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void fail_whenEqualityForThreeFieldsIsCombinedUsingORAndAND() {
		EqualsVerifier<ThreeFieldsUsingORAND> ev = EqualsVerifier.forClass(ThreeFieldsUsingORAND.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void fail_whenEqualityForFiveFieldsIsCombinedUsingOR() {
		EqualsVerifier<FiveFieldsUsingOR> ev = EqualsVerifier.forClass(FiveFieldsUsingOR.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void fail_whenEqualityForFiveFieldsIsCombinedUsingANDsAndORs() {
		EqualsVerifier<FiveFieldsUsingANDsAndORs> ev = EqualsVerifier.forClass(FiveFieldsUsingANDsAndORs.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Ignore
	@Test
	public void fail_whenInstancesAreEqualIfAtLeastTwoFieldsAreEqual() {
		// TODO: This class is not transitive, and it should fail. See issue 78.
		EqualsVerifier<AtLeast2FieldsAreEqual> ev = EqualsVerifier.forClass(AtLeast2FieldsAreEqual.class);
		assertFailure(ev, "Transitivity");
	}
	
	static final class TwoFieldsUsingAND {
		private final String f;
		private final String g;
		
		public TwoFieldsUsingAND(String f, String g) {
			this.f = f;
			this.g = g;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TwoFieldsUsingAND)) {
				return false;
			}
			TwoFieldsUsingAND other = (TwoFieldsUsingAND)obj;
			return nullSafeEquals(f, other.f) && nullSafeEquals(g, other.g);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result = result * 31 + nullSafeHashCode(f);
			result = result * 31 + nullSafeHashCode(g);
			return result;
		}
	}
	
	static final class TwoFieldsUsingOR {
		private final String f;
		private final String g;
		
		public TwoFieldsUsingOR(String f, String g) {
			this.f = f;
			this.g = g;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TwoFieldsUsingOR)) {
				return false;
			}
			TwoFieldsUsingOR other = (TwoFieldsUsingOR)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class ThreeFieldsUsingAND {
		private final String f;
		private final String g;
		private final String h;
		
		public ThreeFieldsUsingAND(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingAND)) {
				return false;
			}
			ThreeFieldsUsingAND other = (ThreeFieldsUsingAND)obj;
			return nullSafeEquals(f, other.f) && nullSafeEquals(g, other.g) && nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result = result * 31 + nullSafeHashCode(f);
			result = result * 31 + nullSafeHashCode(g);
			result = result * 31 + nullSafeHashCode(h);
			return result;
		}
	}
	
	static final class ThreeFieldsUsingOR {
		private final String f;
		private final String g;
		private final String h;
		
		public ThreeFieldsUsingOR(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingOR)) {
				return false;
			}
			ThreeFieldsUsingOR other = (ThreeFieldsUsingOR)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) || nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class ThreeFieldsUsingANDOR {
		private final String f;
		private final String g;
		private final String h;
		
		public ThreeFieldsUsingANDOR(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingANDOR)) {
				return false;
			}
			ThreeFieldsUsingANDOR other = (ThreeFieldsUsingANDOR)obj;
			return nullSafeEquals(f, other.f) && nullSafeEquals(g, other.g) || nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class ThreeFieldsUsingORAND {
		private final String f;
		private final String g;
		private final String h;
		
		public ThreeFieldsUsingORAND(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingORAND)) {
				return false;
			}
			ThreeFieldsUsingORAND other = (ThreeFieldsUsingORAND)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) && nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class FiveFieldsUsingOR {
		private final String f;
		private final String g;
		private final String h;
		private final String i;
		private final String j;
		
		public FiveFieldsUsingOR(String f, String g, String h, String i, String j) {
			this.f = f;
			this.g = g;
			this.h = h;
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FiveFieldsUsingOR)) {
				return false;
			}
			FiveFieldsUsingOR other = (FiveFieldsUsingOR)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) ||
					nullSafeEquals(h, other.h) || nullSafeEquals(i, other.i) || nullSafeEquals(j, other.j);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}
	}
	
	static final class FiveFieldsUsingANDsAndORs {
		private final String f;
		private final String g;
		private final String h;
		private final String i;
		private final String j;
		
		public FiveFieldsUsingANDsAndORs(String f, String g, String h, String i, String j) {
			this.f = f;
			this.g = g;
			this.h = h;
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FiveFieldsUsingANDsAndORs)) {
				return false;
			}
			FiveFieldsUsingANDsAndORs other = (FiveFieldsUsingANDsAndORs)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) && 
					nullSafeEquals(h, other.h) || nullSafeEquals(i, other.i) && nullSafeEquals(j, other.j);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}
	}
	
	static final class AtLeast2FieldsAreEqual {
		private final int i;
		private final int j;
		private final int k;
		private final int l;

		public AtLeast2FieldsAreEqual(int i, int j, int k, int l) {
			this.i = i;
			this.j = j;
			this.k = k;
			this.l = l;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AtLeast2FieldsAreEqual)) {
				return false;
			}
			AtLeast2FieldsAreEqual other = (AtLeast2FieldsAreEqual) obj;
			int x = 0;
			if (i == other.i) x++;
			if (j == other.j) x++;
			if (k == other.k) x++;
			if (l == other.l) x++;
			return x >= 2;
		}

		@Override
		public int hashCode() {
			return 42;
		}

		@Override
		public String toString() {
			return String.format("i=%d, j=%d, k=%d, l=%d", i, j, k, l);
		}
	}
}
