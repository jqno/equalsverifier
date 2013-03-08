/*
 * Copyright 2013 Jan Ouwens
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
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

import org.junit.Test;

public class TransitivityTest {
	@Test
	public void twoFieldsUsingAnd() {
		EqualsVerifier.forClass(TwoFieldsUsingAnd.class).verify();
	}
	
	@Test
	public void twoFieldsOneUnused() {
		EqualsVerifier.forClass(TwoFieldsOneUnused.class).verify();
	}
	
	@Test
	public void twoFieldsUsingOr() {
		EqualsVerifier<TwoFieldsUsingOr> ev = EqualsVerifier.forClass(TwoFieldsUsingOr.class);
		assertFailure(ev, "Transitivity", "two of these three instances are equal to each other, so the third one should be, too", TwoFieldsUsingOr.class.getSimpleName());
	}
	
	@Test
	public void threeFieldsUsingAnd() {
		EqualsVerifier.forClass(ThreeFieldsUsingAnd.class).verify();
	}
	
	@Test
	public void threeFieldsOneUnused() {
		EqualsVerifier.forClass(ThreeFieldsOneUnused.class).verify();
	}
	
	@Test
	public void threeFieldsTwoUnused() {
		EqualsVerifier.forClass(ThreeFieldsTwoUnused.class).verify();
	}
	
	@Test
	public void threeFieldsUsingOr() {
		EqualsVerifier<ThreeFieldsUsingOr> ev = EqualsVerifier.forClass(ThreeFieldsUsingOr.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void threeFieldsUsingOrWithRelaxedEqualExamples() {
		ThreeFieldsUsingOr one = new ThreeFieldsUsingOr("a", "1", "alpha");
		ThreeFieldsUsingOr two = new ThreeFieldsUsingOr("b", "1", "alpha");
		ThreeFieldsUsingOr three = new ThreeFieldsUsingOr("c", "1", "alpha");
		ThreeFieldsUsingOr other = new ThreeFieldsUsingOr("d", "4", "delta");
		
		EqualsVerifier<ThreeFieldsUsingOr> ev = EqualsVerifier.forRelaxedEqualExamples(one, two, three)
				.andUnequalExample(other);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void threeFieldsUsingAndOr() {
		EqualsVerifier<ThreeFieldsUsingAndOr> ev = EqualsVerifier.forClass(ThreeFieldsUsingAndOr.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void threeFieldsUsingOrAnd() {
		EqualsVerifier<ThreeFieldsUsingOrAnd> ev = EqualsVerifier.forClass(ThreeFieldsUsingOrAnd.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void fiveFieldsUsingOr() {
		EqualsVerifier<FiveFieldsUsingOr> ev = EqualsVerifier.forClass(FiveFieldsUsingOr.class);
		assertFailure(ev, "Transitivity");
	}
	
	@Test
	public void fiveFieldsUsingAndsAndOrs() {
		EqualsVerifier<FiveFieldsUsingOr> ev = EqualsVerifier.forClass(FiveFieldsUsingOr.class);
		assertFailure(ev, "Transitivity");
	}

	static final class TwoFieldsUsingAnd {
		public final String f;
		public final String g;
		
		public TwoFieldsUsingAnd(String f, String g) {
			this.f = f;
			this.g = g;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TwoFieldsUsingAnd)) {
				return false;
			}
			TwoFieldsUsingAnd other = (TwoFieldsUsingAnd)obj;
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
	
	static final class TwoFieldsOneUnused {
		public final String f;
		public final String g;
		
		public TwoFieldsOneUnused(String f, String g) {
			this.f = f;
			this.g = g;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TwoFieldsOneUnused)) {
				return false;
			}
			TwoFieldsOneUnused other = (TwoFieldsOneUnused)obj;
			return nullSafeEquals(g, other.g);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result = result * 31 + nullSafeHashCode(g);
			return result;
		}
	}
	
	static final class TwoFieldsUsingOr {
		public final String f;
		public final String g;
		
		public TwoFieldsUsingOr(String f, String g) {
			this.f = f;
			this.g = g;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TwoFieldsUsingOr)) {
				return false;
			}
			TwoFieldsUsingOr other = (TwoFieldsUsingOr)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class ThreeFieldsUsingAnd {
		public final String f;
		public final String g;
		public final String h;
		
		public ThreeFieldsUsingAnd(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingAnd)) {
				return false;
			}
			ThreeFieldsUsingAnd other = (ThreeFieldsUsingAnd)obj;
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
	
	static final class ThreeFieldsOneUnused {
		public final String f;
		public final String g;
		public final String h;
		
		public ThreeFieldsOneUnused(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsOneUnused)) {
				return false;
			}
			ThreeFieldsOneUnused other = (ThreeFieldsOneUnused)obj;
			return nullSafeEquals(g, other.g) && nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result = result * 31 + nullSafeHashCode(g);
			result = result * 31 + nullSafeHashCode(h);
			return result;
		}
	}
	
	static final class ThreeFieldsTwoUnused {
		public final String f;
		public final String g;
		public final String h;
		
		public ThreeFieldsTwoUnused(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsTwoUnused)) {
				return false;
			}
			ThreeFieldsTwoUnused other = (ThreeFieldsTwoUnused)obj;
			return nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result = result * 31 + nullSafeHashCode(h);
			return result;
		}
	}
	
	static final class ThreeFieldsUsingOr {
		public final String f;
		public final String g;
		public final String h;
		
		public ThreeFieldsUsingOr(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingOr)) {
				return false;
			}
			ThreeFieldsUsingOr other = (ThreeFieldsUsingOr)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) || nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class ThreeFieldsUsingAndOr {
		public final String f;
		public final String g;
		public final String h;
		
		public ThreeFieldsUsingAndOr(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingAndOr)) {
				return false;
			}
			ThreeFieldsUsingAndOr other = (ThreeFieldsUsingAndOr)obj;
			return nullSafeEquals(f, other.f) && nullSafeEquals(g, other.g) || nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class ThreeFieldsUsingOrAnd {
		public final String f;
		public final String g;
		public final String h;
		
		public ThreeFieldsUsingOrAnd(String f, String g, String h) {
			this.f = f;
			this.g = g;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeFieldsUsingOrAnd)) {
				return false;
			}
			ThreeFieldsUsingOrAnd other = (ThreeFieldsUsingOrAnd)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) && nullSafeEquals(h, other.h);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class FiveFieldsUsingOr {
		public final String f;
		public final String g;
		public final String h;
		public final String i;
		public final String j;
		
		public FiveFieldsUsingOr(String f, String g, String h, String i, String j) {
			this.f = f;
			this.g = g;
			this.h = h;
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FiveFieldsUsingOr)) {
				return false;
			}
			FiveFieldsUsingOr other = (FiveFieldsUsingOr)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) || 
					nullSafeEquals(h, other.h) || nullSafeEquals(i, other.i) || nullSafeEquals(j, other.j);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
	
	static final class FiveFieldsUsingAndsAndOrs {
		public final String f;
		public final String g;
		public final String h;
		public final String i;
		public final String j;
		
		public FiveFieldsUsingAndsAndOrs(String f, String g, String h, String i, String j) {
			this.f = f;
			this.g = g;
			this.h = h;
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof FiveFieldsUsingAndsAndOrs)) {
				return false;
			}
			FiveFieldsUsingAndsAndOrs other = (FiveFieldsUsingAndsAndOrs)obj;
			return nullSafeEquals(f, other.f) || nullSafeEquals(g, other.g) && 
					nullSafeEquals(h, other.h) || nullSafeEquals(i, other.i) && nullSafeEquals(j, other.j);
		}
		
		@Override
		public int hashCode() {
			return 42;
		}	
	}
}
