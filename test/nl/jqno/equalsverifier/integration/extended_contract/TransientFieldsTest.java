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

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.Util;

import org.junit.Test;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class TransientFieldsTest extends IntegrationTestBase {
	@Test
	public void fail_whenTransientFieldsAreUsedInEquals() {
		expectFailure("Transient field", "should not be included in equals/hashCode contract");
		EqualsVerifier.forClass(TransientFields.class)
				.verify();
	}
	
	@Test
	public void succeed_whenTransientFieldsAreUsedInEquals_givenWarningsAreSuppressed() {
		EqualsVerifier.forClass(TransientFields.class)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
	
	static final class TransientFields {
		private final int i;
		private final transient int j;
		
		public TransientFields(int i, int j) { this.i = i; this.j = j; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return Util.defaultHashCode(this); }
	}
}
