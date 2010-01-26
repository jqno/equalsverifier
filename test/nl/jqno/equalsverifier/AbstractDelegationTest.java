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

import org.junit.Test;

public class AbstractDelegationTest extends EqualsVerifierTestBase {
	@Test
	public void equalsDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractEqualsDelegator> ev = EqualsVerifier.forClass(AbstractEqualsDelegator.class);
		ev.with(Feature.VERBOSE);
		verifyFailure("Abstract delegation: AbstractEqualsDelegator's equals method delegates to an abstract method. Add prefab values for nl.jqno.equalsverifier.AbstractDelegationTest$AbstractEqualsDelegator.", ev);
	}
	
	@Test
	public void hashCodeDelegatesToAbstractMethod() {
		EqualsVerifier<AbstractHashCodeDelegator> ev = EqualsVerifier.forClass(AbstractHashCodeDelegator.class);
		verifyFailure("Abstract delegation: AbstractHashCodeDelegator's hashCode method delegates to an abstract method. Add prefab values for nl.jqno.equalsverifier.AbstractDelegationTest$AbstractHashCodeDelegator.", ev);
	}
	
	@Test
	public void equalsDelegatesToAbstractMethodInField() {
		EqualsVerifier<EqualsDelegatesToAbstractMethodInField> ev = EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class);
		verifyFailure("Abstract delegation: EqualsDelegatesToAbstractMethodInField's equals method delegates to an abstract method. Add prefab values for nl.jqno.equalsverifier.AbstractDelegationTest$EqualsDelegatesToAbstractMethodInField.", ev);
	}
	
	@Test
	public void hashCodeDelegatesToAbstractMethodInField() {
		EqualsVerifier<HashCodeDelegatesToAbstractMethodInField> ev = EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class);
		verifyFailure("Abstract delegation: HashCodeDelegatesToAbstractMethodInField's hashCode method delegates to an abstract method. Add prefab values for nl.jqno.equalsverifier.AbstractDelegationTest$HashCodeDelegatesToAbstractMethodInField.", ev);
	}
	
	@Test
	public void equalsInFieldDelegatesToAbstractMethod() {
		EqualsVerifier<EqualsInFieldDelegatesToAbstractMethod> ev = EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class);
		verifyFailure("Abstract delegation: AbstractEqualsDelegator's equals method delegates to an abstract method. Add prefab values for nl.jqno.equalsverifier.AbstractDelegationTest$AbstractEqualsDelegator.", ev);
	}
	
	@Test
	public void hashCodeInFieldDelegatesToAbstractMethod() {
		EqualsVerifier<HashCodeInFieldDelegatesToAbstractMethod> ev = EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class);
		verifyFailure("Abstract delegation: AbstractHashCodeDelegator's hashCode method delegates to an abstract method. Add prefab values for nl.jqno.equalsverifier.AbstractDelegationTest$AbstractHashCodeDelegator.", ev);
	}
	
	static abstract class AbstractEqualsDelegator {
		abstract boolean theAnswer();
		
		@Override
		public boolean equals(Object obj) {
			return theAnswer();
		}
	}
	
	static abstract class AbstractHashCodeDelegator {
		abstract int theAnswer();
		
		@Override
		public int hashCode() {
			return theAnswer();
		}
	}
	
	static abstract class AbstractDelegator {
		abstract void abstractDelegation();
	}
	
	static final class EqualsDelegatesToAbstractMethodInField {
		final AbstractDelegator ad;
		
		EqualsDelegatesToAbstractMethodInField(AbstractDelegator ad) {
			this.ad = ad;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsDelegatesToAbstractMethodInField)) {
				return false;
			}
			if (ad == null) {
				return ((EqualsDelegatesToAbstractMethodInField)obj).ad == null;
			}
			ad.abstractDelegation();
			throw new IllegalStateException("Should have failed earlier.");
		}
		
		@Override
		public int hashCode() {
			return 0;
		}
	}
	
	static final class HashCodeDelegatesToAbstractMethodInField {
		final AbstractDelegator ad;
		
		HashCodeDelegatesToAbstractMethodInField(AbstractDelegator ad) {
			this.ad = ad;
		}
		
		@Override
		public boolean equals(Object obj) {
			return true;
		}
		
		@Override
		public int hashCode() {
			ad.abstractDelegation();
			throw new IllegalStateException("Should have failed earlier.");
		}
	}
	
	static final class EqualsInFieldDelegatesToAbstractMethod {
		AbstractEqualsDelegator aed;
		
		EqualsInFieldDelegatesToAbstractMethod(AbstractEqualsDelegator aed) {
			this.aed = aed;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsInFieldDelegatesToAbstractMethod)) {
				return false;
			}
			EqualsInFieldDelegatesToAbstractMethod other = (EqualsInFieldDelegatesToAbstractMethod)obj;
			return aed == null ? other.aed == null : aed.equals(other.aed);
		}
		
		@Override
		public int hashCode() {
			return aed == null ? 0 : aed.hashCode();
		}
	}
	
	static final class HashCodeInFieldDelegatesToAbstractMethod {
		AbstractHashCodeDelegator ahcd;
		
		HashCodeInFieldDelegatesToAbstractMethod(AbstractHashCodeDelegator ahcd) {
			this.ahcd = ahcd;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashCodeInFieldDelegatesToAbstractMethod)) {
				return false;
			}
			HashCodeInFieldDelegatesToAbstractMethod other = (HashCodeInFieldDelegatesToAbstractMethod)obj;
			return ahcd == null ? other.ahcd == null : ahcd.equals(other.ahcd);
		}
		
		@Override
		public int hashCode() {
			return ahcd == null ? 0 : ahcd.hashCode();
		}
	}
}
