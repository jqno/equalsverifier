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

import org.junit.Test;
import static nl.jqno.equalsverifier.Helper.assertFailure;

public class TransientFieldsTest {
	@Test
	public void transientFieldsDontCount() {
		EqualsVerifier<TransientFields> ev = EqualsVerifier.forClass(TransientFields.class);
		assertFailure(ev, "Transient field", "should not be included in equals/hashCode contract");
	}
	
	@Test
	public void suppressTransienceWarning() {
		EqualsVerifier.forClass(TransientFields.class)
				.suppress(Warning.TRANSIENT_FIELDS)
				.verify();
	}
	
	static final class TransientFields {
		private final int i;
		private final transient int j;
		
		public TransientFields(int i, int j) {
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TransientFields)) {
				return false;
			}
			TransientFields other = (TransientFields)obj;
			return i == other.i && j == other.j;
		}
		
		@Override
		public int hashCode() {
			return i + (31 * j);
		}
	}
}
