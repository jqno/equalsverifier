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

import static nl.jqno.equalsverifier.Helper.assertFailure;

import java.util.Arrays;

import org.junit.Test;

public class ArrayTest {
	private static final String PRIMITIVE_EQUALS = "Array: == used instead of Arrays.equals() for field";
	private static final String PRIMITIVE_HASHCODE = "Array: regular hashCode() used instead of Arrays.hashCode() for field";
	private static final String OBJECT_EQUALS = "Multidimensional or Object array: == or Arrays.equals() used instead of Arrays.deepEquals() for field";
	private static final String OBJECT_HASHCODE = "Multidimensional or Object array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field";

	@Test
	public void primitiveArrayShouldUseArraysEquals() {
		EqualsVerifier<PrimitiveArrayContainerWrongEquals> ev = EqualsVerifier.forClass(PrimitiveArrayContainerWrongEquals.class);
		assertFailure(ev, PRIMITIVE_EQUALS, "array");
	}
	
	@Test
	public void primitiveArrayShouldUseArraysHashCode() {
		EqualsVerifier<PrimitiveArrayContainerWrongHashCode> ev = EqualsVerifier.forClass(PrimitiveArrayContainerWrongHashCode.class);
		assertFailure(ev, PRIMITIVE_HASHCODE, "array");
	}
	
	@Test
	public void primitiveArrayHappyPath() {
		EqualsVerifier.forClass(PrimitiveArrayContainerCorrect.class).verify();
	}
	
	@Test
	public void multidimensionalArrayShouldUseDeepEquals() {
		EqualsVerifier<MultidimensionalArrayContainerWrongEquals> ev = EqualsVerifier.forClass(MultidimensionalArrayContainerWrongEquals.class);
		assertFailure(ev, OBJECT_EQUALS, "array");
	}
	
	@Test
	public void multidimensionalArrayShouldNotUseRegularHashCode() {
		EqualsVerifier<MultidimensionalArrayContainerWrongHashCode> ev = EqualsVerifier.forClass(MultidimensionalArrayContainerWrongHashCode.class);
		assertFailure(ev, OBJECT_HASHCODE, "array");
	}
	
	@Test
	public void multidimensionalArrayShouldUseDeepHashCode() {
		EqualsVerifier<MultidimensionalArrayContainerNotDeepHashCode> ev = EqualsVerifier.forClass(MultidimensionalArrayContainerNotDeepHashCode.class);
		assertFailure(ev, OBJECT_HASHCODE, "array");
	}
	
	@Test
	public void multidimensionalArrayHappyPath() {
		EqualsVerifier.forClass(MultidimensionalArrayContainerCorrect.class).verify();
	}
	
	@Test
	public void objectArrayShouldUseDeepEquals() {
		EqualsVerifier<ObjectArrayContainerWrongEquals> ev = EqualsVerifier.forClass(ObjectArrayContainerWrongEquals.class);
		assertFailure(ev, OBJECT_EQUALS, "array");
	}
	
	@Test
	public void objectArrayShouldNotUseRegularHashCode() {
		EqualsVerifier<ObjectArrayContainerWrongHashCode> ev = EqualsVerifier.forClass(ObjectArrayContainerWrongHashCode.class);
		assertFailure(ev, OBJECT_HASHCODE, "array");
	}
	
	@Test
	public void objectArrayShouldUseDeepHashCode() {
		EqualsVerifier<ObjectArrayContainerNotDeepHashCode> ev = EqualsVerifier.forClass(ObjectArrayContainerNotDeepHashCode.class);
		assertFailure(ev, OBJECT_HASHCODE, "array");
	}
	
	@Test
	public void objectArrayHappyPath() {
		EqualsVerifier.forClass(ObjectArrayContainerCorrect.class).verify();
	}
	
	@Test
	public void objectWithAnArrayAndAnUnusedFieldShouldWork() {
		EqualsVerifier.forClass(ArrayAndSomethingUnusedContainer.class).verify();
	}
	
	static final class PrimitiveArrayContainerWrongEquals {
		private final int[] array;
		
		PrimitiveArrayContainerWrongEquals(int[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveArrayContainerWrongEquals)) {
				return false;
			}
			PrimitiveArrayContainerWrongEquals other = (PrimitiveArrayContainerWrongEquals)obj;
			return array == other.array;
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
	
	static final class PrimitiveArrayContainerWrongHashCode {
		private final int[] array;
		
		PrimitiveArrayContainerWrongHashCode(int[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveArrayContainerWrongHashCode)) {
				return false;
			}
			PrimitiveArrayContainerWrongHashCode other = (PrimitiveArrayContainerWrongHashCode)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null ? 0 : array.hashCode());
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

	static final class MultidimensionalArrayContainerWrongEquals {
		private final int[][] array;
		
		MultidimensionalArrayContainerWrongEquals(int[][] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayContainerWrongEquals)) {
				return false;
			}
			MultidimensionalArrayContainerWrongEquals other = (MultidimensionalArrayContainerWrongEquals)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class MultidimensionalArrayContainerWrongHashCode {
		private final int[][] array;
		
		MultidimensionalArrayContainerWrongHashCode(int[][] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayContainerWrongHashCode)) {
				return false;
			}
			MultidimensionalArrayContainerWrongHashCode other = (MultidimensionalArrayContainerWrongHashCode)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : array.hashCode();
		}
	}
	
	static final class MultidimensionalArrayContainerNotDeepHashCode {
		private final int[][] array;
		
		MultidimensionalArrayContainerNotDeepHashCode(int[][] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayContainerNotDeepHashCode)) {
				return false;
			}
			MultidimensionalArrayContainerNotDeepHashCode other = (MultidimensionalArrayContainerNotDeepHashCode)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : array.hashCode();
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
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class ObjectArrayContainerWrongEquals {
		private final Object[] array;
		
		ObjectArrayContainerWrongEquals(Object[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayContainerWrongEquals)) {
				return false;
			}
			ObjectArrayContainerWrongEquals other = (ObjectArrayContainerWrongEquals)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class ObjectArrayContainerWrongHashCode {
		private final Object[] array;
		
		ObjectArrayContainerWrongHashCode(Object[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayContainerWrongHashCode)) {
				return false;
			}
			ObjectArrayContainerWrongHashCode other = (ObjectArrayContainerWrongHashCode)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : array.hashCode();
		}
	}
	
	static final class ObjectArrayContainerNotDeepHashCode {
		private final Object[] array;
		
		ObjectArrayContainerNotDeepHashCode(Object[] array) {
			this.array = array;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayContainerNotDeepHashCode)) {
				return false;
			}
			ObjectArrayContainerNotDeepHashCode other = (ObjectArrayContainerNotDeepHashCode)obj;
			return Arrays.deepEquals(array, other.array);
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
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class ArrayAndSomethingUnusedContainer {
		private final int[] array;
		@SuppressWarnings("unused")
		private final int i;
		
		ArrayAndSomethingUnusedContainer(int[] array, int i) {
			this.array = array;
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ArrayAndSomethingUnusedContainer)) {
				return false;
			}
			ArrayAndSomethingUnusedContainer other = (ArrayAndSomethingUnusedContainer)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null ? 0 : Arrays.hashCode(array));
		}
	}
}
