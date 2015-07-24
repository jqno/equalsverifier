/*
 * Copyright 2010, 2013-2015 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.Util;

import org.junit.Test;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class AbstractDelegationTest extends IntegrationTestBase {
    private static final String ABSTRACT_DELEGATION = "Abstract delegation";
    private static final String EQUALS_DELEGATES = "equals method delegates to an abstract method";
    private static final String HASHCODE_DELEGATES = "hashCode method delegates to an abstract method";
    private static final String PREFAB = "Add prefab values for";

    @Test
    public void succeed_whenClassHasAFieldOfAnAbstractClass() {
        EqualsVerifier.forClass(AbstractContainer.class)
                .verify();
    }

    @Test
    public void failGracefully_whenEqualsCallsAnAbstractMethod() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, AbstractEqualsDelegator.class.getSimpleName());
        EqualsVerifier.forClass(AbstractEqualsDelegator.class)
                .verify();
    }

    @Test
    public void failGracefully_whenHashCodeCallsAnAbstractMethod() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, AbstractHashCodeDelegator.class.getSimpleName());
        EqualsVerifier.forClass(AbstractHashCodeDelegator.class)
                .verify();
    }

    @Test
    public void succeed_whenToStringCallsAnAbstractMethod() {
        EqualsVerifier.forClass(AbstractToStringDelegator.class)
                .verify();
    }

    @Test
    public void failGracefully_whenEqualsCallsAnAbstractFieldsAbstractMethod() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, EqualsDelegatesToAbstractMethodInField.class.getSimpleName());
        EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsCallsAnAbstractFieldsAbstactMethod_givenAConcretePrefabImplementationOfSaidAbstractField() {
        EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class)
                .withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(1), new AbstractDelegatorImpl(2))
                .verify();
    }

    @Test
    public void failGracefully_whenHashCodeCallsAnAbstractFieldsAbstactMethod() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, HashCodeDelegatesToAbstractMethodInField.class.getSimpleName());
        EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class)
                .verify();
    }

    @Test
    public void succeed_whenHashCodeCallsAnAbstractFieldsAbstactMethod_givenAConcretePrefabImplementationOfSaidAbstractField() {
        EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class)
                .withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(1), new AbstractDelegatorImpl(2))
                .verify();
    }

    @Test
    public void succeed_whenToStringCallsAnAbstractFieldsAbstractMethod() {
        EqualsVerifier.forClass(ToStringDelegatesToAbstractMethodInField.class)
                .verify();
    }

    @Test
    public void failGracefully_whenAFieldsEqualsMethodCallsAnAbstractField() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, PREFAB, AbstractEqualsDelegator.class.getSimpleName());
        EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class)
                .verify();
    }

    @Test
    public void succeed_whenAFieldsEqualsMethodCallsAnAbstractField_givenAConcretePrefabImplementationOfSaidField() {
        EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class)
                .withPrefabValues(AbstractEqualsDelegator.class, new AbstractEqualsDelegatorImpl(1), new AbstractEqualsDelegatorImpl(2))
                .verify();
    }

    @Test
    public void failGracefully_whenAFieldsHashCodeMethodCallsAnAbstractField() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, PREFAB, AbstractHashCodeDelegator.class.getSimpleName());
        EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class)
                .verify();
    }

    @Test
    public void succeed_whenAFieldsHashCodeMethodCallsAnAbstractField_givenAConcretePrefabImplementationOfSaidField() {
        EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class)
                .withPrefabValues(AbstractHashCodeDelegator.class, new AbstractHashCodeDelegatorImpl(1), new AbstractHashCodeDelegatorImpl(2))
                .verify();
    }

    @Test
    public void succeed_whenAFieldsToStringMethodCallsAnAbstractField() {
        EqualsVerifier.forClass(ToStringInFieldDelegatesToAbstractMethod.class)
                .verify();
    }

    @Test
    public void failGracefully_whenEqualsInSuperclassCallsAnAbstractMethodEvenThoughItsImplementedHere() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, EQUALS_DELEGATES, AbstractEqualsDelegator.class.getSimpleName());
        EqualsVerifier.forClass(AbstractEqualsDelegatorImpl.class)
                .verify();
    }

    @Test
    public void failGracefully_whenHashCodeInSuperclassCallsAnAbstractMethodEvenThoughItsImplementedHere() {
        expectFailureWithCause(AbstractMethodError.class, ABSTRACT_DELEGATION, HASHCODE_DELEGATES, AbstractHashCodeDelegator.class.getSimpleName());
        EqualsVerifier.forClass(AbstractHashCodeDelegatorImpl.class)
                .verify();
    }

    @Test
    public void succeed_whenToStringInSuperclassCallsAnAbstractMethod() {
        EqualsVerifier.forClass(AbstractToStringDelegatorImpl.class)
                .verify();
    }

    @Test
    public void originalMessageIsIncludedInErrorMessage_whenEqualsVerifierSignalsAnAbstractDelegationIssue() {
        expectFailure("This is AbstractMethodError's original message");
        EqualsVerifier.forClass(ThrowsAbstractMethodErrorWithMessage.class)
                .verify();
    }

    private static abstract class AbstractClass {
        private int i;

        abstract void someMethod();

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractClass)) {
                return false;
            }
            return i == ((AbstractClass)obj).i;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class AbstractContainer {
        private final AbstractClass foo;

        public AbstractContainer(AbstractClass ac) { this.foo = ac; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractContainer)) {
                return false;
            }
            AbstractContainer other = (AbstractContainer)obj;
            return nullSafeEquals(foo, other.foo);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static abstract class AbstractEqualsDelegator {
        private final int i;

        public AbstractEqualsDelegator(int i) { this.i = i; }

        abstract boolean theAnswer();

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AbstractEqualsDelegator)) {
                return false;
            }
            if (theAnswer()) {
                return true;
            }
            AbstractEqualsDelegator other = (AbstractEqualsDelegator)obj;
            return i == other.i;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static class AbstractEqualsDelegatorImpl extends AbstractEqualsDelegator {
        public AbstractEqualsDelegatorImpl(int i) { super(i); }

        @Override
        public boolean theAnswer() {
            return false;
        }
    }

    static abstract class AbstractHashCodeDelegator {
        private final int i;

        public AbstractHashCodeDelegator(int i) { this.i = i; }

        abstract int theAnswer();

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public int hashCode() {
            return i + theAnswer();
        }
    }

    static class AbstractHashCodeDelegatorImpl extends AbstractHashCodeDelegator {
        public AbstractHashCodeDelegatorImpl(int i) { super(i); }

        @Override
        public int theAnswer() {
            return 0;
        }
    }

    static abstract class AbstractToStringDelegator {
        private final int i;

        public AbstractToStringDelegator(int i) { this.i = i; }

        abstract int theAnswer();

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof AbstractToStringDelegator)) {
                return false;
            }
            AbstractToStringDelegator other = (AbstractToStringDelegator)obj;
            return i == other.i;
        }

        @Override public final int hashCode() { return defaultHashCode(this); }

        @Override
        public String toString() {
            return "" + theAnswer();
        }
    }

    static class AbstractToStringDelegatorImpl extends AbstractToStringDelegator {
        public AbstractToStringDelegatorImpl(int i) { super(i); }

        @Override
        int theAnswer() {
            return 0;
        }
    }

    static abstract class AbstractDelegator {
        abstract void abstractDelegation();

        private int i;
        public AbstractDelegator(int i) { this.i = i; }
        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override
        public int hashCode() {
            return Util.defaultHashCode(this);
        }
    }

    static final class AbstractDelegatorImpl extends AbstractDelegator {
        public AbstractDelegatorImpl(int i) { super(i); }

        @Override
        public void abstractDelegation() {}
    }

    static final class EqualsDelegatesToAbstractMethodInField {
        final AbstractDelegator delegator;
        final int i;

        public EqualsDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) { this.delegator = ad; this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsDelegatesToAbstractMethodInField)) {
                return false;
            }
            if (delegator != null) {
                delegator.abstractDelegation();
            }
            EqualsDelegatesToAbstractMethodInField other = (EqualsDelegatesToAbstractMethodInField)obj;
            return i == other.i && nullSafeEquals(delegator, other.delegator);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class HashCodeDelegatesToAbstractMethodInField {
        final AbstractDelegator delegator;
        final int i;

        public HashCodeDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) { this.delegator = ad; this.i = i; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public int hashCode() {
            if (delegator != null) {
                delegator.abstractDelegation();
            }
            return defaultHashCode(this);
        }
    }

    static final class ToStringDelegatesToAbstractMethodInField {
        final AbstractDelegator delegator;
        final int i;

        public ToStringDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) { this.delegator = ad; this.i = i; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }

        @Override
        public String toString() {
            if (delegator != null) {
                delegator.abstractDelegation();
            }
            return "..." + i;
        }
    }

    static final class EqualsInFieldDelegatesToAbstractMethod {
        final AbstractEqualsDelegator delegator;

        public EqualsInFieldDelegatesToAbstractMethod(AbstractEqualsDelegator aed) { this.delegator = aed; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsInFieldDelegatesToAbstractMethod)) {
                return false;
            }
            EqualsInFieldDelegatesToAbstractMethod other = (EqualsInFieldDelegatesToAbstractMethod)obj;
            return nullSafeEquals(delegator, other.delegator);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class HashCodeInFieldDelegatesToAbstractMethod {
        final AbstractHashCodeDelegator delegator;

        public HashCodeInFieldDelegatesToAbstractMethod(AbstractHashCodeDelegator ahcd) { this.delegator = ahcd; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public int hashCode() {
            return nullSafeHashCode(delegator);
        }
    }

    static final class ToStringInFieldDelegatesToAbstractMethod {
        final AbstractToStringDelegator delegator;

        public ToStringInFieldDelegatesToAbstractMethod(AbstractToStringDelegator atsd) { this.delegator = atsd; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }

        @Override
        public String toString() {
            return "..." + (delegator == null ? "" : delegator.toString());
        }
    }

    static abstract class ThrowsAbstractMethodErrorWithMessage {
        private final int i;

        public ThrowsAbstractMethodErrorWithMessage(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            throw new AbstractMethodError("This is AbstractMethodError's original message");
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
