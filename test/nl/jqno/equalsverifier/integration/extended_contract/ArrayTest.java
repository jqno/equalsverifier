/*
 * Copyright 2009-2010, 2013-2014 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Test;

public class ArrayTest extends IntegrationTestBase {
	private static final String PRIMITIVE_EQUALS = "Array: == used instead of Arrays.equals() for field";
	private static final String PRIMITIVE_HASHCODE = "Array: regular hashCode() used instead of Arrays.hashCode() for field";
	private static final String MULTIDIMENSIONAL_EQUALS = "Multidimensional array: == or Arrays.equals() used instead of Arrays.deepEquals() for field";
	private static final String MULTIDIMENSIONAL_HASHCODE = "Multidimensional array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field";
	private static final String OBJECT_EQUALS = "Object array: == or Arrays.equals() used instead of Arrays.deepEquals() for field";
	private static final String OBJECT_HASHCODE = "Object array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field";
	private static final String FIELD_NAME = "array";

	@Test
	public void fail_whenRegularEqualsIsUsedInsteadOfArraysEquals_givenAPrimitiveArray() {
		expectFailure(PRIMITIVE_EQUALS, FIELD_NAME);
		EqualsVerifier.forClass(PrimitiveArrayRegularEquals.class).verify();
	}
	
	@Test
	public void fail_whenRegularHashCodeIsUsedInsteadOfArraysHashCode_givenAPrimitiveArray() {
		expectFailure(PRIMITIVE_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(PrimitiveArrayRegularHashCode.class).verify();
	}
	
	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAPrimitiveArray() {
		EqualsVerifier.forClass(PrimitiveArrayCorrect.class).verify();
	}
	
	@Test
	public void fail_whenArraysEqualsIsUsedInsteadOfDeepEquals_givenAMultidimensionalArray() {
		expectFailure(MULTIDIMENSIONAL_EQUALS, FIELD_NAME);
		EqualsVerifier.forClass(MultidimensionalArrayArraysEquals.class).verify();
	}
	
	@Test
	public void fail_whenRegularHashCodeIsUsedInsteadOfDeepHashCode_givenAMultidimensionalArray() {
		expectFailure(MULTIDIMENSIONAL_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(MultidimensionalArrayRegularHashCode.class).verify();
	}
	
	@Test
	public void fail_whenArraysHashCodeIsUsedInsteadOfDeepHashCode_givenAMultidimensionalArray() {
		expectFailure(MULTIDIMENSIONAL_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(MultidimensionalArrayArraysHashCode.class).verify();
	}
	
	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAMultidimensionalArray() {
		EqualsVerifier.forClass(MultidimensionalArrayCorrect.class).verify();
	}
	
	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAThreedimensionalArray() {
		EqualsVerifier.forClass(ThreeDimensionalArrayCorrect.class).verify();
	}
	
	@Test
	public void fail_whenArraysEqualsIsUsedInsteadOfDeepEquals_givenAnObjectArray() {
		expectFailure(OBJECT_EQUALS, FIELD_NAME);
		EqualsVerifier.forClass(ObjectArrayArraysEquals.class).verify();
	}
	
	@Test
	public void fail_whenRegularHashCodeIsUsedInsteadOfDeepHashCode_givenAnObjectArray() {
		expectFailure(OBJECT_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(ObjectArrayRegularHashCode.class).verify();
	}
	
	@Test
	public void fail_whenArraysHashCodeIsUsedInsteadOfDeepHashCode_givenAnObjectArray() {
		expectFailure(OBJECT_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(ObjectArrayArraysHashCode.class).verify();
	}
	
	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAnObjectArray() {
		EqualsVerifier.forClass(ObjectArrayCorrect.class).verify();
	}
	
	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAnArrayAndAnUnusedField() {
		EqualsVerifier.forClass(ArrayAndSomethingUnused.class).verify();
	}
	
	@Test
	public void succeed_whenArraysAreNotUsedInEquals_givenArrayFields() {
		EqualsVerifier.forClass(ArrayAndNoEquals.class).verify();
	}
	
	static final class PrimitiveArrayRegularEquals {
		private final int[] array;
		
		public PrimitiveArrayRegularEquals(int[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveArrayRegularEquals)) {
				return false;
			}
			PrimitiveArrayRegularEquals other = (PrimitiveArrayRegularEquals)obj;
			return nullSafeEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
	
	static final class PrimitiveArrayRegularHashCode {
		private final int[] array;
		
		public PrimitiveArrayRegularHashCode(int[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveArrayRegularHashCode)) {
				return false;
			}
			PrimitiveArrayRegularHashCode other = (PrimitiveArrayRegularHashCode)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(array);
		}
	}
	
	static final class PrimitiveArrayCorrect {
		private final int[] array;
		
		public PrimitiveArrayCorrect(int[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveArrayCorrect)) {
				return false;
			}
			PrimitiveArrayCorrect other = (PrimitiveArrayCorrect)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}

	static final class MultidimensionalArrayArraysEquals {
		private final int[][] array;
		
		public MultidimensionalArrayArraysEquals(int[][] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayArraysEquals)) {
				return false;
			}
			MultidimensionalArrayArraysEquals other = (MultidimensionalArrayArraysEquals)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class MultidimensionalArrayRegularHashCode {
		private final int[][] array;
		
		public MultidimensionalArrayRegularHashCode(int[][] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayRegularHashCode)) {
				return false;
			}
			MultidimensionalArrayRegularHashCode other = (MultidimensionalArrayRegularHashCode)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : array.hashCode();
		}
	}
	
	static final class MultidimensionalArrayArraysHashCode {
		private final int[][] array;
		
		public MultidimensionalArrayArraysHashCode(int[][] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayArraysHashCode)) {
				return false;
			}
			MultidimensionalArrayArraysHashCode other = (MultidimensionalArrayArraysHashCode)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(array);
		}
	}
	
	static final class MultidimensionalArrayCorrect {
		private final int[][] array;
		
		public MultidimensionalArrayCorrect(int[][] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MultidimensionalArrayCorrect)) {
				return false;
			}
			MultidimensionalArrayCorrect other = (MultidimensionalArrayCorrect)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class ThreeDimensionalArrayCorrect {
		private final int[][][] array;
		
		public ThreeDimensionalArrayCorrect(int[][][] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreeDimensionalArrayCorrect)) {
				return false;
			}
			ThreeDimensionalArrayCorrect other = (ThreeDimensionalArrayCorrect)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class ObjectArrayArraysEquals {
		private final Object[] array;
		
		public ObjectArrayArraysEquals(Object[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayArraysEquals)) {
				return false;
			}
			ObjectArrayArraysEquals other = (ObjectArrayArraysEquals)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class ObjectArrayRegularHashCode {
		private final Object[] array;
		
		public ObjectArrayRegularHashCode(Object[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayRegularHashCode)) {
				return false;
			}
			ObjectArrayRegularHashCode other = (ObjectArrayRegularHashCode)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : array.hashCode();
		}
	}
	
	static final class ObjectArrayArraysHashCode {
		private final Object[] array;
		
		public ObjectArrayArraysHashCode(Object[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayArraysHashCode)) {
				return false;
			}
			ObjectArrayArraysHashCode other = (ObjectArrayArraysHashCode)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
		}
	}
	
	static final class ObjectArrayCorrect {
		private final Object[] array;
		
		public ObjectArrayCorrect(Object[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayCorrect)) {
				return false;
			}
			ObjectArrayCorrect other = (ObjectArrayCorrect)obj;
			return Arrays.deepEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.deepHashCode(array);
		}
	}
	
	static final class ArrayAndSomethingUnused {
		private final int[] array;
		@SuppressWarnings("unused")
		private final int i;
		
		public ArrayAndSomethingUnused(int[] array, int i) { this.array = array; this.i = i; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ArrayAndSomethingUnused)) {
				return false;
			}
			ArrayAndSomethingUnused other = (ArrayAndSomethingUnused)obj;
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(array);
		}
	}
	
	@SuppressWarnings("unused")
	static final class ArrayAndNoEquals {
		private final int[] ints;
		private final Object[] objects;
		private final int[][] arrays;
		
		public ArrayAndNoEquals(int[] ints, Object[] objects, int[][] arrays) { this.ints = ints; this.objects = objects; this.arrays = arrays; }
	}
}
