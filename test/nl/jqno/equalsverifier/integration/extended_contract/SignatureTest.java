/*
 * Copyright 2010, 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class SignatureTest {
	private static final String OVERLOADED = "Overloaded";
	private static final String SIGNATURE_SHOULD_BE = "Signature should be";
	private static final String SIGNATURE = "public boolean equals(Object obj)";

	@Test
	public void fail_whenEqualsIsOverloadedWithTypeInsteadOfObject() {
		EqualsVerifier<OverloadedWithOwnType> ev = EqualsVerifier.forClass(OverloadedWithOwnType.class);
		assertOverloadFailure(ev, "Parameter should be an Object, not " + OverloadedWithOwnType.class.getSimpleName());
	}
	
	@Test
	public void fail_whenEqualsIsOverloadedWithTwoParameters() {
		EqualsVerifier<OverloadedWithTwoParameters> ev = EqualsVerifier.forClass(OverloadedWithTwoParameters.class);
		assertOverloadFailure(ev, "Too many parameters");
	}
	
	@Test
	public void fail_whenEqualsIsOverloadedWithNoParameter() {
		EqualsVerifier<OverloadedWithNoParameter> ev = EqualsVerifier.forClass(OverloadedWithNoParameter.class);
		assertOverloadFailure(ev, "No parameter");
	}
	
	@Test
	public void fail_whenEqualsIsOverloadedWithUnrelatedParameter() {
		EqualsVerifier<OverloadedWithUnrelatedParameter> ev = EqualsVerifier.forClass(OverloadedWithUnrelatedParameter.class);
		assertOverloadFailure(ev, "Parameter should be an Object");
	}
	
	@Test
	public void fail_whenEqualsIsProperlyOverriddenButAlsoOverloaded() {
		EqualsVerifier<OverloadedAndOverridden> ev = EqualsVerifier.forClass(OverloadedAndOverridden.class);
		assertOverloadFailure(ev, "More than one equals method found");
	}
	
	@Test
	public void succeed_whenEqualsIsNeitherOverriddenOrOverloaded() {
		EqualsVerifier.forClass(NoEqualsMethod.class).verify();
	}
	
	private void assertOverloadFailure(EqualsVerifier<?> ev, String extraMessage) {
		assertFailure(ev, OVERLOADED, SIGNATURE_SHOULD_BE, SIGNATURE, extraMessage);
	}
	
	static final class OverloadedWithOwnType {
		private final int i;
		
		OverloadedWithOwnType(int i) {
			this.i = i;
		}
		
		public boolean equals(OverloadedWithOwnType obj) {
			if (obj == null) {
				return false;
			}
			return i == obj.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class OverloadedWithTwoParameters {
		private final int i;
		
		OverloadedWithTwoParameters(int i) {
			this.i = i;
		}
		
		public boolean equals(Object red, Object black) {
			return red == null ? black == null : red.equals(black);
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class OverloadedWithNoParameter {
		private final int i;
		
		OverloadedWithNoParameter(int i) {
			this.i = i;
		}
		
		public boolean equals() {
			return false;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class OverloadedWithUnrelatedParameter {
		private final int i;
		
		OverloadedWithUnrelatedParameter(int i) {
			this.i = i;
		}
		
		public boolean equals(int i) {
			return this.i == i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class OverloadedAndOverridden {
		private final int i;
		
		OverloadedAndOverridden(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof OverloadedAndOverridden)) {
				return false;
			}
			return i == ((OverloadedAndOverridden)obj).i;
		}
		
		public boolean equals(OverloadedAndOverridden obj) {
			if (obj == null) {
				return false;
			}
			return i == obj.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class NoEqualsMethod {
		private final int i;
	
		public NoEqualsMethod(int i) {
			this.i = i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
}
