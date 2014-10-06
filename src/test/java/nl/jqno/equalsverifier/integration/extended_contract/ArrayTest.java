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

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import static org.junit.internal.matchers.StringContains.containsString;

public class ArrayTest extends IntegrationTestBase {
	private static final String REGULAR_EQUALS = "Array: == or regular equals() used instead of Arrays.equals() for field";
	private static final String REGULAR_HASHCODE = "Array: regular hashCode() used instead of Arrays.hashCode() for field";
	private static final String MULTIDIMENSIONAL_EQUALS = "Multidimensional array: ==, regular equals() or Arrays.equals() used instead of Arrays.deepEquals() for field";
	private static final String MULTIDIMENSIONAL_HASHCODE = "Multidimensional array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field";
	private static final String FIELD_NAME = "array";

	@Test
	public void fail_whenRegularEqualsIsUsedInsteadOfArraysEquals_givenAPrimitiveArray() {
		expectFailure(REGULAR_EQUALS, FIELD_NAME);
		EqualsVerifier.forClass(PrimitiveArrayRegularEquals.class)
				.verify();
	}
	
	@Test
	public void fail_whenRegularHashCodeIsUsedInsteadOfArraysHashCode_givenAPrimitiveArray() {
		expectFailure(REGULAR_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(PrimitiveArrayRegularHashCode.class)
				.verify();
	}
	
	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAPrimitiveArray() {
		EqualsVerifier.forClass(PrimitiveArrayCorrect.class)
				.verify();
	}
	
	@Test
	public void fail_whenArraysEqualsIsUsedInsteadOfDeepEquals_givenAMultidimensionalArray() {
		expectFailure(MULTIDIMENSIONAL_EQUALS, FIELD_NAME);
		EqualsVerifier.forClass(MultidimensionalArrayArraysEquals.class)
				.verify();
	}
	
	@Test
	public void fail_whenRegularHashCodeIsUsedInsteadOfDeepHashCode_givenAMultidimensionalArray() {
		expectFailure(MULTIDIMENSIONAL_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(MultidimensionalArrayRegularHashCode.class)
				.verify();
	}

	@Test
	public void fail_whenArraysHashCodeIsUsedInsteadOfDeepHashCode_givenAMultidimensionalArray() {
		expectFailure(MULTIDIMENSIONAL_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(MultidimensionalArrayArraysHashCode.class)
				.verify();
	}

	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAMultidimensionalArray() {
		EqualsVerifier.forClass(MultidimensionalArrayCorrect.class)
				.verify();
	}

	@Test
	public void failWithCorrectMessage_whenShallowHashCodeIsUsedOnSecondArray_givenTwoMultidimensionalArrays() {
		expectFailure("second", MULTIDIMENSIONAL_HASHCODE);
		EqualsVerifier.forClass(TwoMultidimensionalArraysShallowHashCodeForSecond.class)
				.verify();
	}

	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAThreedimensionalArray() {
		EqualsVerifier.forClass(ThreeDimensionalArrayCorrect.class)
				.verify();
	}
	@Test
	public void failWithRecursionError_whenClassContainsARecursionButAlsoAMutltiDimensionalArray() {
		thrown.expect(AssertionError.class);
		thrown.expectMessage(containsString("Recursive datastructure"));
		EqualsVerifier.forClass(MultiDimensionalArrayAndRecursion.Board.class)
				.verify();
	}

	@Test
	public void fail_whenRegularEqualsIsUsedInsteadOfArraysEquals_givenAnObjectArray() {
		expectFailure(REGULAR_EQUALS, FIELD_NAME);
		EqualsVerifier.forClass(ObjectArrayRegularEquals.class)
				.verify();
	}

	@Test
	public void fail_whenRegularHashCodeIsUsedInsteadOfArraysHashCode_givenAnObjectArray() {
		expectFailure(REGULAR_HASHCODE, FIELD_NAME);
		EqualsVerifier.forClass(ObjectArrayRegularHashCode.class)
				.verify();
	}

	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAnObjectArray() {
		EqualsVerifier.forClass(ObjectArrayCorrect.class)
				.verify();
	}
	
	@Test
	public void succeed_whenCorrectMethodsAreUsed_givenAnArrayAndAnUnusedField() {
		EqualsVerifier.forClass(ArrayAndSomethingUnused.class)
				.verify();
	}
	
	@Test
	public void succeed_whenArraysAreNotUsedInEquals_givenArrayFields() {
		EqualsVerifier.forClass(ArrayAndNoEquals.class)
				.verify();
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

	final static class TwoMultidimensionalArraysShallowHashCodeForSecond {
		private final Object[][] first;
		private final Object[][] second;

		public TwoMultidimensionalArraysShallowHashCodeForSecond(Object[][] first, Object[][] second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TwoMultidimensionalArraysShallowHashCodeForSecond)) {
				return false;
			}
			TwoMultidimensionalArraysShallowHashCodeForSecond other = (TwoMultidimensionalArraysShallowHashCodeForSecond) obj;
			return Arrays.deepEquals(first, other.first) && Arrays.deepEquals(second, other.second);
		}

		@Override
		public int hashCode() {
			int prime = 31;
			int result = 1;
			result = (result * prime) + Arrays.deepHashCode(first);
			result = (result * prime) + Arrays.hashCode(second);
			return result;
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

	@SuppressWarnings("unused")
	static final class MultiDimensionalArrayAndRecursion {
		static final class Board {
			private final Object[][] grid;
			private BoardManipulator manipulator = new BoardManipulator(this);
			public Board(Object[][] grid) { this.grid = grid; }
		}

		static final class BoardManipulator {
			private final Board board;
			public BoardManipulator(Board board) { this.board = board; }
		}
	}

	static final class ObjectArrayRegularEquals {
		private final Object[] array;
		
		public ObjectArrayRegularEquals(Object[] array) { this.array = array; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectArrayRegularEquals)) {
				return false;
			}
			ObjectArrayRegularEquals other = (ObjectArrayRegularEquals)obj;
			return nullSafeEquals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
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
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(array);
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
			return Arrays.equals(array, other.array);
		}
		
		@Override
		public int hashCode() {
			return (array == null) ? 0 : Arrays.hashCode(array);
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
