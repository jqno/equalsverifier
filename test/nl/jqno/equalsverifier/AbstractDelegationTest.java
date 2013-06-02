/*
 * Copyright 2010, 2013 Jan Ouwens
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
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

import org.junit.Test;

public class AbstractDelegationTest {
	private static final String ABSTRACT_DELEGATION = "Abstract delegation";
	private static final String EQUALS_DELEGATES = "equals method delegates to an abstract method";
	private static final String HASHCODE_DELEGATES = "hashCode method delegates to an abstract method";
	private static final String PREFAB = "Add prefab values for";

	@Test
	public void equalsDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractEqualsDelegator> ev = EqualsVerifier.forClass(AbstractEqualsDelegator.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, AbstractEqualsDelegator.class.getSimpleName());
	}
	
	@Test
	public void hashCodeDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractHashCodeDelegator> ev = EqualsVerifier.forClass(AbstractHashCodeDelegator.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, AbstractHashCodeDelegator.class.getSimpleName());
	}
	
	@Test
	public void toStringDelegatesToAbstractMethod() {
		EqualsVerifier.forClass(AbstractToStringDelegator.class).verify();
	}
	
	@Test
	public void equalsDelegatesToAbstractMethodInField() {
		EqualsVerifier<EqualsDelegatesToAbstractMethodInField> ev = EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, EqualsDelegatesToAbstractMethodInField.class.getSimpleName());
		
		EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class)
				.suppress(Warning.NULL_FIELDS)
				.withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(), new AbstractDelegatorImpl())
				.verify();
	}
	
	@Test
	public void hashCodeDelegatesToAbstractMethodInField() {
		EqualsVerifier<HashCodeDelegatesToAbstractMethodInField> ev = EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, HashCodeDelegatesToAbstractMethodInField.class.getSimpleName());
		
		EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class)
				.suppress(Warning.NULL_FIELDS)
				.withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(), new AbstractDelegatorImpl())
				.verify();
	}
	
	@Test
	public void toStringDelegatesToAbstractMethodInField() {
		EqualsVerifier.forClass(ToStringDelegatesToAbstractMethodInField.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void equalsInFieldDelegatesToAbstractMethod() {
		EqualsVerifier<EqualsInFieldDelegatesToAbstractMethod> ev = EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, PREFAB, AbstractEqualsDelegator.class.getSimpleName());
		
		EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class)
				.withPrefabValues(AbstractEqualsDelegator.class, new AbstractEqualsDelegatorImpl(1), new AbstractEqualsDelegatorImpl(2))
				.verify();
	}
	
	@Test
	public void hashCodeInFieldDelegatesToAbstractMethod() {
		EqualsVerifier<HashCodeInFieldDelegatesToAbstractMethod> ev = EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, PREFAB, AbstractHashCodeDelegator.class.getSimpleName());
		
		EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class)
				.withPrefabValues(AbstractHashCodeDelegator.class, new AbstractHashCodeDelegatorImpl(1), new AbstractHashCodeDelegatorImpl(2))
				.verify();
	}
	
	@Test
	public void toStringInFieldDelegatesToAbstractMethod() {
		EqualsVerifier.forClass(ToStringInFieldDelegatesToAbstractMethod.class)
				.verify();
	}
	
	@Test
	public void equalsInSuperclassDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractEqualsDelegatorImpl> ev = EqualsVerifier.forClass(AbstractEqualsDelegatorImpl.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, AbstractEqualsDelegator.class.getSimpleName());
	}
	
	@Test
	public void hashCodeInSuperclassDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractHashCodeDelegatorImpl> ev = EqualsVerifier.forClass(AbstractHashCodeDelegatorImpl.class);
		assertFailure(ev, AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, AbstractHashCodeDelegator.class.getSimpleName());
	}
	
	@Test
	public void toStringInSuperclassDelegatesToAbstractMethod() {
		EqualsVerifier.forClass(AbstractToStringDelegatorImpl.class).verify();
	}
	
	@Test
	public void originalMessageIsIncludedInErrorMessage() {
		EqualsVerifier<ThrowsAbstractMethodErrorWithMessage> ev = EqualsVerifier.forClass(ThrowsAbstractMethodErrorWithMessage.class);
		assertFailure(ev, "This is AbstractMethodError's original message");
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
	
	static abstract class AbstractToStringDelegator {
		private final int i;
		
		AbstractToStringDelegator(int i) {
			this.i = i;
		}
		
		abstract int theAnswer();

		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof AbstractToStringDelegator)) {
				return false;
			}
			AbstractToStringDelegator other = (AbstractToStringDelegator)obj;
			return i == other.i;
		}

		@Override
		public final int hashCode() {
			return i;
		}
		
		@Override
		public String toString() {
			return "" + theAnswer();
		}
	}
	
	static class AbstractToStringDelegatorImpl extends AbstractToStringDelegator {
		public AbstractToStringDelegatorImpl(int i) {
			super(i);
		}
		
		@Override
		int theAnswer() {
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
		final AbstractDelegator delegator;
		final int i;
		
		EqualsDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) {
			this.delegator = ad;
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsDelegatesToAbstractMethodInField)) {
				return false;
			}
			delegator.abstractDelegation();
			EqualsDelegatesToAbstractMethodInField other = (EqualsDelegatesToAbstractMethodInField)obj;
			return i == other.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class HashCodeDelegatesToAbstractMethodInField {
		final AbstractDelegator delegator;
		final int i;
		
		HashCodeDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) {
			this.delegator = ad;
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
			delegator.abstractDelegation();
			return i;
		}
	}
	
	static final class ToStringDelegatesToAbstractMethodInField {
		final AbstractDelegator delegator;
		final int i;
		
		public ToStringDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) {
			this.delegator = ad;
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ToStringDelegatesToAbstractMethodInField)) {
				return false;
			}
			ToStringDelegatesToAbstractMethodInField other = (ToStringDelegatesToAbstractMethodInField)obj;
			return i == other.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
		
		@Override
		public String toString() {
			delegator.abstractDelegation();
			return "..." + i;
		}
	}
	
	static final class EqualsInFieldDelegatesToAbstractMethod {
		final AbstractEqualsDelegator delegator;
		
		EqualsInFieldDelegatesToAbstractMethod(AbstractEqualsDelegator aed) {
			this.delegator = aed;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsInFieldDelegatesToAbstractMethod)) {
				return false;
			}
			EqualsInFieldDelegatesToAbstractMethod other = (EqualsInFieldDelegatesToAbstractMethod)obj;
			return nullSafeEquals(delegator, other.delegator);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(delegator);
		}
	}
	
	static final class HashCodeInFieldDelegatesToAbstractMethod {
		final AbstractHashCodeDelegator delegator;
		
		HashCodeInFieldDelegatesToAbstractMethod(AbstractHashCodeDelegator ahcd) {
			this.delegator = ahcd;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashCodeInFieldDelegatesToAbstractMethod)) {
				return false;
			}
			HashCodeInFieldDelegatesToAbstractMethod other = (HashCodeInFieldDelegatesToAbstractMethod)obj;
			return nullSafeEquals(delegator, other.delegator);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(delegator);
		}
	}
	
	static final class ToStringInFieldDelegatesToAbstractMethod {
		final AbstractToStringDelegator delegator;
		
		public ToStringInFieldDelegatesToAbstractMethod(AbstractToStringDelegator atsd) {
			this.delegator = atsd;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ToStringInFieldDelegatesToAbstractMethod)) {
				return false;
			}
			ToStringInFieldDelegatesToAbstractMethod other = (ToStringInFieldDelegatesToAbstractMethod)obj;
			return nullSafeEquals(delegator, other.delegator);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(delegator);
		}
		
		@Override
		public String toString() {
			return "..." + (delegator == null ? "" : delegator.toString());
		}
	}
	
	static abstract class ThrowsAbstractMethodErrorWithMessage {
		private final int i;
		
		ThrowsAbstractMethodErrorWithMessage(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			throw new AbstractMethodError("This is AbstractMethodError's original message");
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
}
