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


public class MutableStateTest extends EqualsVerifierTestBase {
	@Test
	public void mutableFieldNotUsed() {
		EqualsVerifier.forClass(MutableFieldNotUsed.class).verify();
	}
	
	@Test
	public void secondMutableFieldUsed() {
		EqualsVerifier<SecondMutableFieldUsed> ev = EqualsVerifier.forClass(SecondMutableFieldUsed.class);
		verifyFailure("Mutability: equals depends on mutable field second", ev);

		EqualsVerifier.forClass(SecondMutableFieldUsed.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateObject() {
		EqualsVerifier<MutableObjectContainer> ev = EqualsVerifier.forClass(MutableObjectContainer.class);
		verifyFailure("Mutability: equals depends on mutable field _object", ev);
		
		EqualsVerifier.forClass(MutableObjectContainer.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateEnumValue() {
		EqualsVerifier<MutableEnumContainer> ev = EqualsVerifier.forClass(MutableEnumContainer.class);
		verifyFailure("Mutability: equals depends on mutable field _enum", ev);
		
		EqualsVerifier.forClass(MutableEnumContainer.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateEnumNull() {
		MutableEnumContainer first = new MutableEnumContainer(null);
		MutableEnumContainer second = new MutableEnumContainer(MutableEnumContainer.Enum.SECOND);
		EqualsVerifier<MutableEnumContainer> ev = EqualsVerifier.forExamples(first, second);
		verifyFailure("Mutability: equals depends on mutable field _enum", ev);
		
		EqualsVerifier.forExamples(first, second)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateArray() {
		EqualsVerifier<MutableArrayContainer> ev = EqualsVerifier.forClass(MutableArrayContainer.class);
		verifyFailure("Mutability: equals depends on mutable field _array", ev);
		
		EqualsVerifier.forClass(MutableArrayContainer.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
	
	@Test
	public void detectMutableStateInt() {
		EqualsVerifier<MutableIntContainer> ev = EqualsVerifier.forClass(MutableIntContainer.class);
		verifyFailure("Mutability: equals depends on mutable field _int", ev);
		
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
		private Object _object;
		
		public MutableObjectContainer(Object value) {
			_object = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableObjectContainer)) {
				return false;
			}
			MutableObjectContainer other = (MutableObjectContainer)obj;
			if (_object == null) {
				return other._object == null;
			}
			return _object.equals(other._object);
		}
		
		@Override
		public int hashCode() {
			return _object == null ? 0 : _object.hashCode();
		}
	}
	
	static final class MutableEnumContainer {
		public enum Enum { FIRST, SECOND };
		
		private Enum _enum;
		
		public MutableEnumContainer(Enum value) {
			_enum = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableEnumContainer)) {
				return false;
			}
			return _enum == ((MutableEnumContainer)obj)._enum;
		}
		
		@Override
		public int hashCode() {
			return _enum == null ? 0 : _enum.hashCode();
		}
	}
	
	static final class MutableArrayContainer {
		private int[] _array;
		
		MutableArrayContainer(int[] value) {
			_array = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableArrayContainer)) {
				return false;
			}
			return Arrays.equals(_array, ((MutableArrayContainer)obj)._array);
		}
		
		@Override
		public int hashCode() {
			return (_array == null) ? 0 : Arrays.hashCode(_array);
		}
	}
	
	static final class MutableIntContainer {
		private int _int;
		
		MutableIntContainer(int value) {
			_int = value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MutableIntContainer)) {
				return false;
			}
			return _int == ((MutableIntContainer)obj)._int;
		}
		
		@Override
		public int hashCode() {
			return _int;
		}
	}
}
