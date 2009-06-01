/*
 * Copyright 2009 Jan Ouwens
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

import java.util.Arrays;

import org.junit.Test;

public class ArrayTest extends EqualsVerifierTestBase {
	@Test
	public void equalButNotSamePrimitiveArray() {
		EqualsVerifier<PrimitiveArrayContainer> ev = EqualsVerifier.forClass(PrimitiveArrayContainer.class);
		verifyFailure("Array: == used instead of Arrays.equals().", ev);
	}
	
	private static final class PrimitiveArrayContainer {
		private final int[] array;
		
		PrimitiveArrayContainer(int[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveArrayContainer)) {
				return false;
			}
			PrimitiveArrayContainer other = (PrimitiveArrayContainer)obj;
			return array == other.array;
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
}
