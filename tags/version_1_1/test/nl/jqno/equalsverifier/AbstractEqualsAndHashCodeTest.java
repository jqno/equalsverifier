/*
 * Copyright 2011 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;

import org.junit.Test;

public class AbstractEqualsAndHashCodeTest {
	private static final String ABSTRACT_DELEGATION = "Abstract delegation";
	private static final String EQUALS_IS_ABSTRACT = "equals method is abstract";
	private static final String HASHCODE_IS_ABSTRACT = "hashCode method is abstract";
	private static final String EQUALS_IS_NOT = "but equals is not";
	private static final String HASHCODE_IS_NOT = "but hashCode is not";

	@Test
	public void bothEqualsAndHashCodeAbstractIsValid() {
		EqualsVerifier.forClass(SubclassOfAbstractBoth.class)
				.verify();
	}
	
	@Test
	public void onlyEqualsAbstractIsInvalid() {
		EqualsVerifier<SubclassOfAbstractEqualsButNotHashCode> ev = EqualsVerifier.forClass(SubclassOfAbstractEqualsButNotHashCode.class);
		assertFailure(ev, ABSTRACT_DELEGATION, EQUALS_IS_ABSTRACT, HASHCODE_IS_NOT);
	}
	
	@Test
	public void onlyHashCodeAbstractIsInvalid() {
		EqualsVerifier<SubclassOfAbstractHashCodeButNotEquals> ev = EqualsVerifier.forClass(SubclassOfAbstractHashCodeButNotEquals.class);
		assertFailure(ev, ABSTRACT_DELEGATION, HASHCODE_IS_ABSTRACT, EQUALS_IS_NOT);
	}
	
	@Test
	public void extraLevelInClassHierarchyIsValid() {
		EqualsVerifier.forClass(SubclassOfSubclassOfAbstractBoth.class)
				.verify();
	}
	
	abstract static class AbstractBoth {
		public abstract boolean equals(Object obj);
		public abstract int hashCode();
	}
	
	static final class SubclassOfAbstractBoth extends AbstractBoth {
		private final int foo;
		
		SubclassOfAbstractBoth(int foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubclassOfAbstractBoth)) {
				return false;
			}
			return foo == ((SubclassOfAbstractBoth)obj).foo;
		}
		
		@Override
		public int hashCode() {
			return foo;
		}
	}
	
	static abstract class AbstractEqualsButNotHashCode {
		public abstract boolean equals(Object obj);
	}
	
	static class SubclassOfAbstractEqualsButNotHashCode extends AbstractEqualsButNotHashCode {
		private final int foo;
		
		SubclassOfAbstractEqualsButNotHashCode(int foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubclassOfAbstractEqualsButNotHashCode)) {
				return false;
			}
			return foo == ((SubclassOfAbstractEqualsButNotHashCode)obj).foo;
		}
		
		@Override
		public int hashCode() {
			return foo;
		}
	}
	
	static abstract class AbstractHashCodeButNotEquals {
		public abstract int hashCode();
	}
	
	static class SubclassOfAbstractHashCodeButNotEquals extends AbstractHashCodeButNotEquals {
		private final int foo;
		
		SubclassOfAbstractHashCodeButNotEquals(int foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubclassOfAbstractHashCodeButNotEquals)) {
				return false;
			}
			return foo == ((SubclassOfAbstractHashCodeButNotEquals)obj).foo;
		}
		
		@Override
		public int hashCode() {
			return foo;
		}
	}

	static abstract class IntermediateSubclassOfAbstractBoth extends AbstractBoth {}
	
	static final class SubclassOfSubclassOfAbstractBoth extends IntermediateSubclassOfAbstractBoth {
		private final int foo;
		
		SubclassOfSubclassOfAbstractBoth(int foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SubclassOfSubclassOfAbstractBoth)) {
				return false;
			}
			return foo == ((SubclassOfSubclassOfAbstractBoth)obj).foo;
		}
		
		@Override
		public int hashCode() {
			return foo;
		}
	}
}
