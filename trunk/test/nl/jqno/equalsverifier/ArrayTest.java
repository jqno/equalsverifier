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
		verifyFailure("Array: == used instead of Arrays.equals() for field array.", ev);
		
		EqualsVerifier.forClass(PrimitiveArrayContainerCorrect.class).verify();
	}
	
	@Test
	public void multidimensionalArrayShouldUseDeepEquals() {
		EqualsVerifier<MultidimensionalArrayContainer> ev = EqualsVerifier.forClass(MultidimensionalArrayContainer.class);
		verifyFailure("Multidimensional or Object array: == or Arrays.equals used instead of Arrays.deepEquals() for field array.", ev);
		
		EqualsVerifier.forClass(MultidimensionalArrayContainerCorrect.class).verify();
	}
	
	@Test
	public void ObjectArrayShouldUseDeepEquals() {
		EqualsVerifier<ObjectArrayContainer> ev = EqualsVerifier.forClass(ObjectArrayContainer.class);
		verifyFailure("Multidimensional or Object array: == or Arrays.equals used instead of Arrays.deepEquals() for field array.", ev);
		
		EqualsVerifier.forClass(ObjectArrayContainerCorrect.class).verify();
	}
	
	static final class PrimitiveArrayContainer {
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
	
	static final class PrimitiveArrayContainerCorrect {
		private final int[] array;
		
		PrimitiveArrayContainerCorrect(int[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveArrayContainerCorrect)) {
				return false;
			}
			PrimitiveArrayContainerCorrect other = (PrimitiveArrayContainerCorrect)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
	
	static final class MultidimensionalArrayContainer {
		private final int[][] array;
		
		MultidimensionalArrayContainer(int[][] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayContainer)) {
				return false;
			}
			MultidimensionalArrayContainer other = (MultidimensionalArrayContainer)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
	
	static final class MultidimensionalArrayContainerCorrect {
		private final int[][] array;
		
		MultidimensionalArrayContainerCorrect(int[][] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayContainerCorrect)) {
				return false;
			}
			MultidimensionalArrayContainerCorrect other = (MultidimensionalArrayContainerCorrect)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
	
	static final class ObjectArrayContainer {
		private final Object[] array;
		
		ObjectArrayContainer(Object[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayContainer)) {
				return false;
			}
			ObjectArrayContainer other = (ObjectArrayContainer)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
	
	static final class ObjectArrayContainerCorrect {
		private final Object[] array;
		
		ObjectArrayContainerCorrect(Object[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayContainerCorrect)) {
				return false;
			}
			ObjectArrayContainerCorrect other = (ObjectArrayContainerCorrect)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
}
