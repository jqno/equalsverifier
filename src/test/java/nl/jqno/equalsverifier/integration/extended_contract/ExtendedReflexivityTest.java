/*
 * Copyright 2015 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Test;

public class ExtendedReflexivityTest extends IntegrationTestBase {
	@Test
	public void succeed_whenEqualsUsesEqualsMethodForObjects() {
		EqualsVerifier.forClass(UsesEqualsMethod.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsUsesDoubleEqualSignForObjects() {
		expectFailure("Reflexivity");
		EqualsVerifier.forClass(UsesDoubleEqualSign.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsUsesDoubleEqualSignForObject_givenDoubleEqualWarningIsSuppressed() {
		EqualsVerifier.forClass(UsesDoubleEqualSign.class)
				.suppress(Warning.DOUBLE_EQUAL_SIGN)
				.verify();
	}
	
	static final class UsesEqualsMethod {
		private final String s;
		
		public UsesEqualsMethod(String s) { this.s = s; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof UsesEqualsMethod)) {
				return false;
			}
			UsesEqualsMethod other = (UsesEqualsMethod)obj;
			return nullSafeEquals(s, other.s);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	static final class UsesDoubleEqualSign {
		private final String s;
		
		public UsesDoubleEqualSign(String s) { this.s = s; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof UsesDoubleEqualSign)) {
				return false;
			}
			UsesDoubleEqualSign other = (UsesDoubleEqualSign)obj;
			return s == other.s;
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
