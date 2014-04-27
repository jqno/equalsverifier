/*
 * Copyright 2009-2010, 2014 Jan Ouwens
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
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class MutableStateTest {
	private static final String MUTABILITY = "Mutability: equals depends on mutable field";
	private static final String FIELD_NAME = "field";
	
	@Test
	public void succeed_whenClassHasAMutablePrimitiveField_givenItDoesNotUseThatFieldInEquals() {
		EqualsVerifier.forClass(UnusedPrimitiveMutableField.class).verify();
	}
	
	@Test
	public void fail_whenClassHasAMutablePrimitiveField() {
		EqualsVerifier<PrimitiveMutableField> ev = EqualsVerifier.forClass(PrimitiveMutableField.class);
		assertFailure(ev, MUTABILITY, "second");
	}
		
	@Test
	public void succeed_whenClassHasAMutablePrimitiveField_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(PrimitiveMutableField.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassHasAMutableObjectField() {
		EqualsVerifier<ObjectMutableField> ev = EqualsVerifier.forClass(ObjectMutableField.class);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
	}
		
	@Test
	public void succeed_whenClassHasAMutableObjectField_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(ObjectMutableField.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassHasAMutableEnumField() {
		EqualsVerifier<EnumMutableField> ev = EqualsVerifier.forClass(EnumMutableField.class);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
	}
	
	@Test
	public void succeed_whenClassHasAMutableEnumField_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(EnumMutableField.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassHasAMutableEnumField_givenNullAsAnExample() {
		EnumMutableField red = new EnumMutableField(null);
		EnumMutableField black = new EnumMutableField(EnumMutableField.Enum.BLACK);
		EqualsVerifier<EnumMutableField> ev = EqualsVerifier.forExamples(red, black);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
	}
	
	@Test
	public void succeed_whenClassHasAMutableEnumField_givenNullAsAnExampleAndWarningIsSuppressed() {
		EnumMutableField red = new EnumMutableField(null);
		EnumMutableField black = new EnumMutableField(EnumMutableField.Enum.BLACK);
		EqualsVerifier.forExamples(red, black)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void fail_whenClassHasAMutableArrayField() {
		EqualsVerifier<ArrayMutableField> ev = EqualsVerifier.forClass(ArrayMutableField.class);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
	}
	
	@Test
	public void succeed_whenClassHasAMutableArrayField_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(ArrayMutableField.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	static final class UnusedPrimitiveMutableField {
		private final int immutable;
		@SuppressWarnings("unused")
		private int mutable = 0;
		
		public UnusedPrimitiveMutableField(int value) {
			immutable = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof UnusedPrimitiveMutableField)) {
				return false;
			}
			return immutable == ((UnusedPrimitiveMutableField)obj).immutable;
		}
		
		@Override
		public int hashCode() {
			return immutable;
		}
	}
	
	static final class PrimitiveMutableField {
		@SuppressWarnings("unused")
		private int first;
		private int second;
		
		PrimitiveMutableField(int first, int second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimitiveMutableField)) {
				return false;
			}
			return second == ((PrimitiveMutableField)obj).second;
		}
		
		@Override
		public int hashCode() {
			return second;
		}
	}
	
	static final class ObjectMutableField {
		private Object field;
		
		public ObjectMutableField(Object value) {
			field = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectMutableField)) {
				return false;
			}
			ObjectMutableField other = (ObjectMutableField)obj;
			if (field == null) {
				return other.field == null;
			}
			return field.equals(other.field);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(field);
		}
	}
	
	static final class EnumMutableField {
		public enum Enum { RED, BLACK };
		
		private Enum field;
		
		public EnumMutableField(Enum value) {
			field = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EnumMutableField)) {
				return false;
			}
			return field == ((EnumMutableField)obj).field;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(field);
		}
	}
	
	static final class ArrayMutableField {
		private int[] field;
		
		ArrayMutableField(int[] value) {
			field = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ArrayMutableField)) {
				return false;
			}
			return Arrays.equals(field, ((ArrayMutableField)obj).field);
		}
		
		@Override
		public int hashCode() {
			return (field == null) ? 0 : Arrays.hashCode(field);
		}
	}
}
