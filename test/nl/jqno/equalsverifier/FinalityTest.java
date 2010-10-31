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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.Helper.assertFailure;

import org.junit.Test;

public class FinalityTest {
	private static final String BOTH_FINAL_OR_NONFINAL = "Finality: equals and hashCode must both be final or both be non-final";

	@Test
	public void finalEqualsNonFinalHashCode() {
		check(FinalEqualsNonFinalHashCode.class);
	}
	
	@Test
	public void suppressFinalEqualsNonFinalHashCode() {
		EqualsVerifier.forClass(FinalEqualsNonFinalHashCode.class)
				.usingGetClass()
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	@Test
	public void nonFinalEqualsFinalHashCode() {
		check(NonFinalEqualsFinalHashCode.class);
	}

	@Test
	public void suppressNonFinalEqualsFinalHashCode() {
		EqualsVerifier.forClass(NonFinalEqualsFinalHashCode.class)
				.usingGetClass()
				.suppress(Warning.STRICT_INHERITANCE)
				.verify();
	}
	
	private <T> void check(Class<T> type) {
		EqualsVerifier<T> ev = EqualsVerifier.forClass(type).usingGetClass();
		assertFailure(ev, BOTH_FINAL_OR_NONFINAL);
	}
	
	static class FinalEqualsNonFinalHashCode {
		private final int i;
		
		FinalEqualsNonFinalHashCode(int i) {
			this.i = i;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			FinalEqualsNonFinalHashCode other = (FinalEqualsNonFinalHashCode)obj;
			return other.i == i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static class NonFinalEqualsFinalHashCode {
		private final int i;
		
		NonFinalEqualsFinalHashCode(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			NonFinalEqualsFinalHashCode other = (NonFinalEqualsFinalHashCode)obj;
			return other.i == i;
		}
		
		@Override
		public final int hashCode() {
			return i;
		}
	}
}
