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

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;

import org.junit.Test;

public class SignatureTest {
	private static final String OVERLOADED = "Overloaded";
	private static final String SIGNATURE_SHOULD_BE = "Signature should be";
	private static final String SIGNATURE = "public boolean equals(Object obj)";

	@Test
	public void overloadedWithOwnTypeInParameter() {
		EqualsVerifier<OverloadedWithOwnTypeInParameter> ev = EqualsVerifier.forClass(OverloadedWithOwnTypeInParameter.class);
		assertOverloadFailure(ev, "Parameter should be an Object, not " + OverloadedWithOwnTypeInParameter.class.getSimpleName());
	}
	
	@Test
	public void overloadedWithTwoParameters() {
		EqualsVerifier<OverloadedWithTwoParameters> ev = EqualsVerifier.forClass(OverloadedWithTwoParameters.class);
		assertOverloadFailure(ev, "Too many parameters");
	}
	
	@Test
	public void overloadedWithNoParameter() {
		EqualsVerifier<OverloadedWithNoParameter> ev = EqualsVerifier.forClass(OverloadedWithNoParameter.class);
		assertOverloadFailure(ev, "No parameter");
	}
	
	@Test
	public void overloadedWithRandomParameters() {
		EqualsVerifier<OverloadedWithRandomParameter> ev = EqualsVerifier.forClass(OverloadedWithRandomParameter.class);
		assertOverloadFailure(ev, "Parameter should be an Object");
	}
	
	@Test
	public void overloadedAndOverridden() {
		EqualsVerifier<OverloadedAndOverridden> ev = EqualsVerifier.forClass(OverloadedAndOverridden.class);
		assertOverloadFailure(ev, "More than one equals method found");
	}
	
	@Test
	public void noEqualsMethodShouldJustWork() {
		EqualsVerifier.forClass(NoEqualsMethod.class).verify();
	}
	
	private void assertOverloadFailure(EqualsVerifier<?> ev, String extraMessage) {
		assertFailure(ev, OVERLOADED, SIGNATURE_SHOULD_BE, SIGNATURE, extraMessage);
	}
	
	static final class OverloadedWithOwnTypeInParameter {
		private final int i;
		
		OverloadedWithOwnTypeInParameter(int i) {
			this.i = i;
		}
		
		public boolean equals(OverloadedWithOwnTypeInParameter obj) {
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
	
	static final class OverloadedWithRandomParameter {
		private final int i;
		
		OverloadedWithRandomParameter(int i) {
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
