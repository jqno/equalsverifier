package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class AbstractDelegationTest {

    private static final String ABSTRACT_DELEGATION = "Abstract delegation";
    private static final String EQUALS_DELEGATES = "equals method delegates to an abstract method";
    private static final String HASHCODE_DELEGATES = "hashCode method delegates to an abstract method";
    private static final String PREFAB = "Add prefab values for";

    @Test
    void succeed_whenClassHasAFieldOfAnAbstractClass() {
        EqualsVerifier.forClass(AbstractContainer.class).verify();
    }

    @Test
    void failGracefully_whenEqualsCallsAnAbstractMethod() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(AbstractEqualsDelegator.class).verify())
                .assertFailure()
                .assertCause(AbstractMethodError.class)
                .assertMessageContains(
                    ABSTRACT_DELEGATION,
                    EQUALS_DELEGATES,
                    AbstractEqualsDelegator.class.getSimpleName());
    }

    @Test
    void failGracefully_whenHashCodeCallsAnAbstractMethod() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(AbstractHashCodeDelegator.class).verify())
                .assertFailure()
                .assertCause(AbstractMethodError.class)
                .assertMessageContains(
                    ABSTRACT_DELEGATION,
                    HASHCODE_DELEGATES,
                    AbstractHashCodeDelegator.class.getSimpleName());
    }

    @Test
    void succeed_whenToStringCallsAnAbstractMethod() {
        EqualsVerifier.forClass(AbstractToStringDelegator.class).verify();
    }

    @Test
    void failGracefully_whenEqualsCallsAnAbstractFieldsAbstractMethod() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(EqualsDelegatesToAbstractMethodInField.class).verify())
                .assertFailure()
                .assertCause(AbstractMethodError.class)
                .assertMessageContains(
                    ABSTRACT_DELEGATION,
                    EQUALS_DELEGATES,
                    EqualsDelegatesToAbstractMethodInField.class.getSimpleName());
    }

    @Test
    void succeed_whenEqualsCallsAnAbstractFieldsAbstactMethod_givenAConcretePrefabImplementationOfSaidAbstractField() {
        EqualsVerifier
                .forClass(EqualsDelegatesToAbstractMethodInField.class)
                .withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(1), new AbstractDelegatorImpl(2))
                .verify();
    }

    @Test
    void failGracefully_whenHashCodeCallsAnAbstractFieldsAbstactMethod() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(HashCodeDelegatesToAbstractMethodInField.class).verify())
                .assertFailure()
                .assertCause(AbstractMethodError.class)
                .assertMessageContains(
                    ABSTRACT_DELEGATION,
                    HASHCODE_DELEGATES,
                    HashCodeDelegatesToAbstractMethodInField.class.getSimpleName());
    }

    @Test
    void succeed_whenHashCodeCallsAnAbstractFieldsAbstactMethod_givenAConcretePrefabImplementationOfSaidAbstractField() {
        EqualsVerifier
                .forClass(HashCodeDelegatesToAbstractMethodInField.class)
                .withPrefabValues(AbstractDelegator.class, new AbstractDelegatorImpl(1), new AbstractDelegatorImpl(2))
                .verify();
    }

    @Test
    void succeed_whenToStringCallsAnAbstractFieldsAbstractMethod() {
        EqualsVerifier.forClass(ToStringDelegatesToAbstractMethodInField.class).verify();
    }

    @Test
    void failGracefully_whenAFieldsEqualsMethodCallsAnAbstractField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(EqualsInFieldDelegatesToAbstractMethod.class).verify())
                .assertFailure()
                .assertCause(AbstractMethodError.class)
                .assertMessageContains(
                    ABSTRACT_DELEGATION,
                    EQUALS_DELEGATES,
                    PREFAB,
                    AbstractEqualsDelegator.class.getSimpleName());
    }

    @Test
    void succeed_whenAFieldsEqualsMethodCallsAnAbstractField_givenAConcretePrefabImplementationOfSaidField() {
        EqualsVerifier
                .forClass(EqualsInFieldDelegatesToAbstractMethod.class)
                .withPrefabValues(
                    AbstractEqualsDelegator.class,
                    new AbstractEqualsDelegatorImpl(1),
                    new AbstractEqualsDelegatorImpl(2))
                .verify();
    }

    @Test
    void failGracefully_whenAFieldsHashCodeMethodCallsAnAbstractField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(HashCodeInFieldDelegatesToAbstractMethod.class).verify())
                .assertFailure()
                .assertCause(AbstractMethodError.class)
                .assertMessageContains(
                    ABSTRACT_DELEGATION,
                    HASHCODE_DELEGATES,
                    PREFAB,
                    AbstractHashCodeDelegator.class.getSimpleName());
    }

    @Test
    void succeed_whenAFieldsHashCodeMethodCallsAnAbstractField_givenAConcretePrefabImplementationOfSaidField() {
        EqualsVerifier
                .forClass(HashCodeInFieldDelegatesToAbstractMethod.class)
                .withPrefabValues(
                    AbstractHashCodeDelegator.class,
                    new AbstractHashCodeDelegatorImpl(1),
                    new AbstractHashCodeDelegatorImpl(2))
                .verify();
    }

    @Test
    void succeed_whenAFieldsToStringMethodCallsAnAbstractField() {
        EqualsVerifier.forClass(ToStringInFieldDelegatesToAbstractMethod.class).verify();
    }

    @Test
    void succeed_evenThoughEqualsInSuperclassCallsAnAbstractMethod() {
        EqualsVerifier.forClass(AbstractEqualsDelegatorImpl.class).verify();
    }

    @Test
    void succeed_evenThoughHashCodeInSuperclassCallsAnAbstractMethod() {
        EqualsVerifier.forClass(AbstractHashCodeDelegatorImpl.class).verify();
    }

    @Test
    void succeed_evenThoughToStringInSuperclassCallsAnAbstractMethod() {
        EqualsVerifier.forClass(AbstractToStringDelegatorImpl.class).verify();
    }

    @Test
    void succeed_evenThoughEqualsInSuperclassCallsAnAbstractMethod_givenUsingGetClass() {
        EqualsVerifier.forClass(AbstractEqualsUsingGetClassDelegatorImpl.class).usingGetClass().verify();
    }

    @Test
    void originalMessageIsIncludedInErrorMessage_whenEqualsVerifierSignalsAnAbstractDelegationIssue() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ThrowsAbstractMethodErrorWithMessage.class).verify())
                .assertFailure()
                .assertMessageContains("This is AbstractMethodError's original message");
    }

    @Test
    void failGracefully_whenAFieldsEqualsMethodDoesntDoAnIdentityCheckButCallsAnAbstractField() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(EqualsInFieldWithoutIdentityCheckDelegatesToAbstractMethod.class)
                            .verify())
                .assertFailure()
                .assertCause(AbstractMethodError.class)
                .assertMessageContains(
                    ABSTRACT_DELEGATION,
                    EQUALS_DELEGATES,
                    PREFAB,
                    AbstractEqualsWithoutIdentityCheckDelegator.class.getSimpleName());
    }

    @Test
    void succeed_whenAFieldsEqualsMethodDoesntDoAnIdentityCheckButCallsAnAbstractField_givenAConcretePrefabImplementationOfSaidField() {
        EqualsVerifier
                .forClass(EqualsInFieldWithoutIdentityCheckDelegatesToAbstractMethod.class)
                .withPrefabValues(
                    AbstractEqualsWithoutIdentityCheckDelegator.class,
                    new AbstractEqualsWithoutIdentityCheckDelegatorImpl(1),
                    new AbstractEqualsWithoutIdentityCheckDelegatorImpl(2))
                .verify();
    }

    private abstract static class AbstractClass {

        private int i;

        @SuppressWarnings("unused")
        abstract void someMethod();

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractClass)) {
                return false;
            }
            return i == ((AbstractClass) obj).i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class AbstractContainer {

        private final AbstractClass foo;

        public AbstractContainer(AbstractClass ac) {
            this.foo = ac;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractContainer)) {
                return false;
            }
            AbstractContainer other = (AbstractContainer) obj;
            return Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }

    abstract static class AbstractEqualsDelegator {

        private final int i;

        public AbstractEqualsDelegator(int i) {
            this.i = i;
        }

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
            AbstractEqualsDelegator other = (AbstractEqualsDelegator) obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class AbstractEqualsDelegatorImpl extends AbstractEqualsDelegator {

        public AbstractEqualsDelegatorImpl(int i) {
            super(i);
        }

        @Override
        public boolean theAnswer() {
            return false;
        }
    }

    abstract static class AbstractEqualsWithoutIdentityCheckDelegator {

        private final int i;

        public AbstractEqualsWithoutIdentityCheckDelegator(int i) {
            this.i = i;
        }

        abstract boolean theAnswer();

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractEqualsWithoutIdentityCheckDelegator)) {
                return false;
            }
            if (theAnswer()) {
                return true;
            }
            AbstractEqualsWithoutIdentityCheckDelegator other = (AbstractEqualsWithoutIdentityCheckDelegator) obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class AbstractEqualsWithoutIdentityCheckDelegatorImpl
            extends AbstractEqualsWithoutIdentityCheckDelegator {

        public AbstractEqualsWithoutIdentityCheckDelegatorImpl(int i) {
            super(i);
        }

        @Override
        public boolean theAnswer() {
            return false;
        }
    }

    abstract static class AbstractHashCodeDelegator {

        private final int i;

        public AbstractHashCodeDelegator(int i) {
            this.i = i;
        }

        abstract int theAnswer();

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractHashCodeDelegator)) {
                return false;
            }
            AbstractHashCodeDelegator other = (AbstractHashCodeDelegator) obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return i + theAnswer();
        }
    }

    static final class AbstractHashCodeDelegatorImpl extends AbstractHashCodeDelegator {

        public AbstractHashCodeDelegatorImpl(int i) {
            super(i);
        }

        @Override
        public int theAnswer() {
            return 0;
        }
    }

    abstract static class AbstractToStringDelegator {

        private final int i;

        public AbstractToStringDelegator(int i) {
            this.i = i;
        }

        abstract int theAnswer();

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof AbstractToStringDelegator)) {
                return false;
            }
            AbstractToStringDelegator other = (AbstractToStringDelegator) obj;
            return i == other.i;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i);
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

    abstract static class AbstractEqualsUsingGetClassDelegator {

        private final int i;

        public AbstractEqualsUsingGetClassDelegator(int i) {
            this.i = i;
        }

        abstract boolean theAnswer();

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (theAnswer()) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            AbstractEqualsUsingGetClassDelegator other = (AbstractEqualsUsingGetClassDelegator) obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class AbstractEqualsUsingGetClassDelegatorImpl extends AbstractEqualsUsingGetClassDelegator {

        public AbstractEqualsUsingGetClassDelegatorImpl(int i) {
            super(i);
        }

        @Override
        public boolean theAnswer() {
            return false;
        }
    }

    abstract static class AbstractDelegator {

        private int i;

        public AbstractDelegator(int i) {
            this.i = i;
        }

        abstract void abstractDelegation();

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AbstractDelegator other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class AbstractDelegatorImpl extends AbstractDelegator {

        public AbstractDelegatorImpl(int i) {
            super(i);
        }

        @Override
        public void abstractDelegation() {}
    }

    static final class EqualsDelegatesToAbstractMethodInField {

        final AbstractDelegator delegator;
        final int i;

        public EqualsDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) {
            this.delegator = ad;
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsDelegatesToAbstractMethodInField)) {
                return false;
            }
            if (delegator != null) {
                delegator.abstractDelegation();
            }
            EqualsDelegatesToAbstractMethodInField other = (EqualsDelegatesToAbstractMethodInField) obj;
            return i == other.i && Objects.equals(delegator, other.delegator);
        }

        @Override
        public int hashCode() {
            return Objects.hash(delegator, i);
        }
    }

    static final class HashCodeDelegatesToAbstractMethodInField {

        final AbstractDelegator delegator;
        final int i;

        public HashCodeDelegatesToAbstractMethodInField(AbstractDelegator ad, int i) {
            this.delegator = ad;
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof HashCodeDelegatesToAbstractMethodInField other
                    && Objects.equals(delegator, other.delegator)
                    && i == other.i;
        }

        @Override
        public int hashCode() {
            if (delegator != null) {
                delegator.abstractDelegation();
            }
            return Objects.hash(delegator, i);
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
            return obj instanceof ToStringDelegatesToAbstractMethodInField other
                    && Objects.equals(delegator, other.delegator)
                    && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(delegator, i);
        }

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

        public EqualsInFieldDelegatesToAbstractMethod(AbstractEqualsDelegator aed) {
            this.delegator = aed;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsInFieldDelegatesToAbstractMethod)) {
                return false;
            }
            EqualsInFieldDelegatesToAbstractMethod other = (EqualsInFieldDelegatesToAbstractMethod) obj;
            return Objects.equals(delegator, other.delegator);
        }

        @Override
        public int hashCode() {
            return Objects.hash(delegator);
        }
    }

    static final class HashCodeInFieldDelegatesToAbstractMethod {

        final AbstractHashCodeDelegator delegator;

        public HashCodeInFieldDelegatesToAbstractMethod(AbstractHashCodeDelegator ahcd) {
            this.delegator = ahcd;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof HashCodeInFieldDelegatesToAbstractMethod other
                    && Objects.equals(delegator, other.delegator);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(delegator);
        }
    }

    static final class ToStringInFieldDelegatesToAbstractMethod {

        final AbstractToStringDelegator delegator;

        public ToStringInFieldDelegatesToAbstractMethod(AbstractToStringDelegator atsd) {
            this.delegator = atsd;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ToStringInFieldDelegatesToAbstractMethod other
                    && Objects.equals(delegator, other.delegator);
        }

        @Override
        public int hashCode() {
            return Objects.hash(delegator);
        }

        @Override
        public String toString() {
            return "..." + (delegator == null ? "" : delegator.toString());
        }
    }

    abstract static class ThrowsAbstractMethodErrorWithMessage {

        private final int i;

        public ThrowsAbstractMethodErrorWithMessage(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            throw new AbstractMethodError("This is AbstractMethodError's original message");
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static class EqualsInFieldWithoutIdentityCheckDelegatesToAbstractMethod {

        private final AbstractEqualsWithoutIdentityCheckDelegator id;

        protected EqualsInFieldWithoutIdentityCheckDelegatesToAbstractMethod(
                AbstractEqualsWithoutIdentityCheckDelegator id) {
            this.id = id;
        }

        @Override
        public final boolean equals(Object other) {
            if (!(other instanceof EqualsInFieldWithoutIdentityCheckDelegatesToAbstractMethod)) {
                return false;
            }
            EqualsInFieldWithoutIdentityCheckDelegatesToAbstractMethod that =
                    (EqualsInFieldWithoutIdentityCheckDelegatesToAbstractMethod) other;
            return Objects.equals(id, that.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }
}
