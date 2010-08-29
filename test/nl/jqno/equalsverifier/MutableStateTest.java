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
import static nl.jqno.equalsverifier.Helper.nullSafeHashCode;

import java.util.Arrays;

import org.junit.Test;

public class MutableStateTest {
	private static final String MUTABILITY = "Mutability: equals depends on mutable field";
	private static final String FIELD_NAME = "field";

	@Test
	public void mutableFieldNotUsed() {
		EqualsVerifier.forClass(MutableFieldNotUsed.class).verify();
	}
	
	@Test
	public void secondMutableFieldUsed() {
		EqualsVerifier<SecondMutableFieldUsed> ev = EqualsVerifier.forClass(SecondMutableFieldUsed.class);
		assertFailure(ev, MUTABILITY, "second");

		EqualsVerifier.forClass(SecondMutableFieldUsed.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateObject() {
		EqualsVerifier<MutableObjectContainer> ev = EqualsVerifier.forClass(MutableObjectContainer.class);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
		
		EqualsVerifier.forClass(MutableObjectContainer.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateEnumValue() {
		EqualsVerifier<MutableEnumContainer> ev = EqualsVerifier.forClass(MutableEnumContainer.class);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
		
		EqualsVerifier.forClass(MutableEnumContainer.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateEnumNull() {
		MutableEnumContainer first = new MutableEnumContainer(null);
		MutableEnumContainer second = new MutableEnumContainer(MutableEnumContainer.Enum.SECOND);
		EqualsVerifier<MutableEnumContainer> ev = EqualsVerifier.forExamples(first, second);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
		
		EqualsVerifier.forExamples(first, second)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateArray() {
		EqualsVerifier<MutableArrayContainer> ev = EqualsVerifier.forClass(MutableArrayContainer.class);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
		
		EqualsVerifier.forClass(MutableArrayContainer.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateInt() {
		EqualsVerifier<MutableIntContainer> ev = EqualsVerifier.forClass(MutableIntContainer.class);
		assertFailure(ev, MUTABILITY, FIELD_NAME);
		
		EqualsVerifier.forClass(MutableIntContainer.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	static final class MutableFieldNotUsed {
		private final int immutable;
		@SuppressWarnings("unused")
		private int mutable = 0;
		
		public MutableFieldNotUsed(int value) {
			immutable = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableFieldNotUsed)) {
				return false;
			}
			return immutable == ((MutableFieldNotUsed)obj).immutable;
		}
		
		@Override
		public int hashCode() {
			return immutable;
		}
	}
	
	static final class SecondMutableFieldUsed {
		@SuppressWarnings("unused")
		private int first = 0;
		private int second;
		
		SecondMutableFieldUsed(int value) {
			second = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SecondMutableFieldUsed)) {
				return false;
			}
			return second == ((SecondMutableFieldUsed)obj).second;
		}
		
		@Override
		public int hashCode() {
			return second;
		}
	}
	
	static final class MutableObjectContainer {
		private Object field;
		
		public MutableObjectContainer(Object value) {
			field = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableObjectContainer)) {
				return false;
			}
			MutableObjectContainer other = (MutableObjectContainer)obj;
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
	
	static final class MutableEnumContainer {
		public enum Enum { FIRST, SECOND };
		
		private Enum field;
		
		public MutableEnumContainer(Enum value) {
			field = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableEnumContainer)) {
				return false;
			}
			return field == ((MutableEnumContainer)obj).field;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(field);
		}
	}
	
	static final class MutableArrayContainer {
		private int[] field;
		
		MutableArrayContainer(int[] value) {
			field = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableArrayContainer)) {
				return false;
			}
			return Arrays.equals(field, ((MutableArrayContainer)obj).field);
		}
		
		@Override
		public int hashCode() {
			return (field == null) ? 0 : Arrays.hashCode(field);
		}
	}
	
	static final class MutableIntContainer {
		private int field;
		
		MutableIntContainer(int value) {
			field = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableIntContainer)) {
				return false;
			}
			return field == ((MutableIntContainer)obj).field;
		}
		
		@Override
		public int hashCode() {
			return field;
		}
	}
}
