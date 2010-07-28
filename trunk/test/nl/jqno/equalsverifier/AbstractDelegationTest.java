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

import static nl.jqno.equalsverifier.Helper.assertFailure;
import static nl.jqno.equalsverifier.Helper.nullSafeEquals;
import static nl.jqno.equalsverifier.Helper.nullSafeHashCode;

import org.junit.Test;

public class AbstractDelegationTest {
	private static final String ABSTRACT_DELEGATION = "Abstract delegation";
	private static final String EQUALS_DELEGATES = "equals method delegates to an abstract method";
	private static final String HASHCODE_DELEGATES = "hashCode method delegates to an abstract method";
	private static final String PREFAB = "Add prefab values for";

	@Test
	public void equalsDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractEqualsDelegator> ev = EqualsVerifier.forClass(AbstractEqualsDelegator.class);
		assertFailure(ev, ABSTRACT_DELEGATION, EQUALS_DELEGATES, AbstractEqualsDelegator.class.getSimpleName());
	}
	
	@Test
	public void hashCodeDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractHashCodeDelegator> ev = EqualsVerifier.forClass(AbstractHashCodeDelegator.class);
		assertFailure(ev, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, AbstractHashCodeDelegator.class.getSimpleName());
	}
	
	@Test
	public void equalsDelegatesToAbstractMethodInField() {
		EqualsVerifier<EqualsDelegatesToAbstractMethodInField> ev = EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class);
		assertFailure(ev, ABSTRACT_DELEGATION, EQUALS_DELEGATES, EqualsDelegatesToAbstractMethodInField.class.getSimpleName());
		
		EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class)
				.suppress(Warning.NULL_FIELDS)
				.withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(), new AbstractDelegatorImpl())
				.verify();
	}
	
	@Test
	public void hashCodeDelegatesToAbstractMethodInField() {
		EqualsVerifier<HashCodeDelegatesToAbstractMethodInField> ev = EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class);
		assertFailure(ev, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, HashCodeDelegatesToAbstractMethodInField.class.getSimpleName());
		
		EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class)
				.suppress(Warning.NULL_FIELDS)
				.withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(), new AbstractDelegatorImpl())
				.verify();
	}
	
	@Test
	public void equalsInFieldDelegatesToAbstractMethod() {
		EqualsVerifier<EqualsInFieldDelegatesToAbstractMethod> ev = EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class);
		assertFailure(ev, ABSTRACT_DELEGATION, EQUALS_DELEGATES, PREFAB, AbstractEqualsDelegator.class.getSimpleName());
		
		EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class)
				.withPrefabValues(AbstractEqualsDelegator.class, new AbstractEqualsDelegatorImpl(1), new AbstractEqualsDelegatorImpl(2))
				.verify();
	}
	
	@Test
	public void hashCodeInFieldDelegatesToAbstractMethod() {
		EqualsVerifier<HashCodeInFieldDelegatesToAbstractMethod> ev = EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class);
		assertFailure(ev, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, PREFAB, AbstractHashCodeDelegator.class.getSimpleName());
		
		EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class)
				.withPrefabValues(AbstractHashCodeDelegator.class, new AbstractHashCodeDelegatorImpl(1), new AbstractHashCodeDelegatorImpl(2))
				.verify();
	}
	
	@Test
	public void equalsInSuperclassDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractEqualsDelegatorImpl> ev = EqualsVerifier.forClass(AbstractEqualsDelegatorImpl.class);
		assertFailure(ev, ABSTRACT_DELEGATION, EQUALS_DELEGATES, AbstractEqualsDelegator.class.getSimpleName());
	}
	
	@Test
	public void hashCodeInSuperclassDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractHashCodeDelegatorImpl> ev = EqualsVerifier.forClass(AbstractHashCodeDelegatorImpl.class);
		assertFailure(ev, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, AbstractHashCodeDelegator.class.getSimpleName());
	}
	
	static abstract class AbstractEqualsDelegator {
		private final int i;
		
		AbstractEqualsDelegator(int i) {
			this.i = i;
		}
		
		abstract boolean theAnswer();
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AbstractEqualsDelegator)) {
				return false;
			}
			if (theAnswer()) {
				return true;
			}
			AbstractEqualsDelegator other = (AbstractEqualsDelegator)obj;
			return i == other.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static class AbstractEqualsDelegatorImpl extends AbstractEqualsDelegator {
		AbstractEqualsDelegatorImpl(int i) {
			super(i);
		}
		
		@Override
		public boolean theAnswer() {
			return false;
		}
	}
	
	static abstract class AbstractHashCodeDelegator {
		private final int i;
		
		AbstractHashCodeDelegator(int i) {
			this.i = i;
		}
		
		abstract int theAnswer();

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AbstractHashCodeDelegator)) {
				return false;
			}
			AbstractHashCodeDelegator other = (AbstractHashCodeDelegator)obj;
			return i == other.i;
		}

		@Override
		public int hashCode() {
			return i + theAnswer();
		}
	}
	
	static class AbstractHashCodeDelegatorImpl extends AbstractHashCodeDelegator {
		AbstractHashCodeDelegatorImpl(int i) {
			super(i);
		}
		
		@Override
		public int theAnswer() {
			return 0;
		}
	}
	
	static abstract class AbstractDelegator {
		abstract void abstractDelegation();
	}
	
	static final class AbstractDelegatorImpl extends AbstractDelegator {
		@Override
		public void abstractDelegation() {}
	}
	
	static final class EqualsDelegatesToAbstractMethodInField {
		final AbstractDelegator ad;
		final int i;
		
		EqualsDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) {
			this.ad = ad;
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsDelegatesToAbstractMethodInField)) {
				return false;
			}
			ad.abstractDelegation();
			EqualsDelegatesToAbstractMethodInField other = (EqualsDelegatesToAbstractMethodInField)obj;
			return i == other.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class HashCodeDelegatesToAbstractMethodInField {
		final AbstractDelegator ad;
		final int i;
		
		HashCodeDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) {
			this.ad = ad;
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashCodeDelegatesToAbstractMethodInField)) {
				return false;
			}
			HashCodeDelegatesToAbstractMethodInField other = (HashCodeDelegatesToAbstractMethodInField)obj;
			return i == other.i;
		}
		
		@Override
		public int hashCode() {
			ad.abstractDelegation();
			return i;
		}
	}
	
	static final class EqualsInFieldDelegatesToAbstractMethod {
		final AbstractEqualsDelegator aed;
		
		EqualsInFieldDelegatesToAbstractMethod(AbstractEqualsDelegator aed) {
			this.aed = aed;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsInFieldDelegatesToAbstractMethod)) {
				return false;
			}
			EqualsInFieldDelegatesToAbstractMethod other = (EqualsInFieldDelegatesToAbstractMethod)obj;
			return nullSafeEquals(aed, other.aed);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(aed);
		}
	}
	
	static final class HashCodeInFieldDelegatesToAbstractMethod {
		final AbstractHashCodeDelegator ahcd;
		
		HashCodeInFieldDelegatesToAbstractMethod(AbstractHashCodeDelegator ahcd) {
			this.ahcd = ahcd;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashCodeInFieldDelegatesToAbstractMethod)) {
				return false;
			}
			HashCodeInFieldDelegatesToAbstractMethod other = (HashCodeInFieldDelegatesToAbstractMethod)obj;
			return nullSafeEquals(ahcd, other.ahcd);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(ahcd);
		}
	}
}
