package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Color;
import org.junit.jupiter.api.Test;

@SuppressWarnings("CheckReturnValue")
class NullFieldsTest {

    private static final String NON_NULLITY = "Non-nullity";
    private static final String EQUALS = "equals throws NullPointerException";
    private static final String HASHCODE = "hashCode throws NullPointerException";
    private static final String ON_THIS_FIELD = "on the 'this' object's";
    private static final String ON_THE_OTHER_FIELD = "on the parameter's";

    @Test
    void fail_whenEqualsThrowsNpeOnThissField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class).verify())
                .assertFailure()
                .assertCause(NullPointerException.class)
                .assertMessageContains(NON_NULLITY, EQUALS, ON_THIS_FIELD, "color");
    }

    @Test
    void fail_whenEqualsThrowsNpeOnOthersField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(EqualsThrowsNpeOnOther.class).verify())
                .assertFailure()
                .assertCause(NullPointerException.class)
                .assertMessageContains(NON_NULLITY, EQUALS, ON_THE_OTHER_FIELD, "color");
    }

    @Test
    void fail_whenEqualsThrowsNpeOnStaticField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(EqualsThrowsNpeOnStatic.class).verify())
                .assertFailure()
                .assertCause(NullPointerException.class)
                .assertMessageContains(NON_NULLITY, EQUALS, ON_THE_OTHER_FIELD, "color");
    }

    @Test
    void fail_whenHashCodeThrowsNpe() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(HashCodeThrowsNpe.class).verify())
                .assertFailure()
                .assertCause(NullPointerException.class)
                .assertMessageContains(NON_NULLITY, HASHCODE, "color");
    }

    @Test
    void succeed_whenEqualsThrowsNpeOnThissField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void succeed_whenEqualsTestFieldWhichThrowsNpe() {
        EqualsVerifier.forClass(CheckedDeepNullA.class).verify();
    }

    @Test
    void succeed_whenEqualsThrowsNpeOnFieldWhichAlsoThrowsNpe_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(DeepNullA.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void succeed_whenDoingASanityCheckOnTheFieldUsedInThePreviousTests_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(DeepNullB.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void succeed_whenConstantFieldIsNull() {
        EqualsVerifier.forClass(ConstantFieldIsNull.class).verify();
    }

    @Test
    void fail_whenClassHasNullChecksForOnlySomeFields() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(MixedNullFields.class).verify())
                .assertFailure()
                .assertCause(NullPointerException.class)
                .assertMessageContains(NON_NULLITY, EQUALS, ON_THIS_FIELD, "o");
    }

    @Test
    void succeed_whenClassHasNullChecksForOnlySomeFields_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(MixedNullFields.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void succeed_whenClassHasNullChecksForOnlySomeFields_givenTheOtherFieldIsFlagged() {
        EqualsVerifier.forClass(MixedNullFields.class).withNonnullFields("o").verify();
    }

    @Test
    void anExceptionIsThrown_whenANonExistingFieldIsGivenToWithNonnullFields() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(MixedNullFields.class).withNonnullFields("thisFieldDoesNotExist"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "class MixedNullFields does not contain field thisFieldDoesNotExist.");
    }

    @Test
    void anExceptionIsThrown_whenWithNonnullFieldsOverlapsWithSuppressWarnings() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(MixedNullFields.class)
                            .withNonnullFields("o")
                            .suppress(Warning.NULL_FIELDS))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can call either withNonnullFields or suppress Warning.NULL_FIELDS, but not both.");
    }

    @Test
    void anExceptionIsThrown_whenSuppressWarningsOverlapsWithWithNonnullFields() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(MixedNullFields.class)
                            .suppress(Warning.NULL_FIELDS)
                            .withNonnullFields("o"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can call either withNonnullFields or suppress Warning.NULL_FIELDS, but not both.");
    }

    static final class EqualsThrowsNpeOnThis {

        private final Color color;

        public EqualsThrowsNpeOnThis(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsThrowsNpeOnThis)) {
                return false;
            }
            EqualsThrowsNpeOnThis p = (EqualsThrowsNpeOnThis) obj;
            return color.equals(p.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(color);
        }
    }

    static final class EqualsThrowsNpeOnOther {

        private final Color color;

        public EqualsThrowsNpeOnOther(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsThrowsNpeOnOther)) {
                return false;
            }
            EqualsThrowsNpeOnOther p = (EqualsThrowsNpeOnOther) obj;
            return p.color.equals(color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(color);
        }
    }

    static final class EqualsThrowsNpeOnStatic {

        private static Color color = Color.INDIGO;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsThrowsNpeOnStatic)) {
                return false;
            }
            EqualsThrowsNpeOnStatic p = (EqualsThrowsNpeOnStatic) obj;
            return color.equals(p.color);
        }

        @Override
        public int hashCode() {
            return -1;
        }
    }

    static final class HashCodeThrowsNpe {

        private final Color color;

        public HashCodeThrowsNpe(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof HashCodeThrowsNpe other && Objects.equals(color, other.color);
        }

        @Override
        public int hashCode() {
            return color.hashCode();
        }

        @Override
        public String toString() {
            // Object.toString calls hashCode()
            return "";
        }
    }

    static final class CheckedDeepNullA {

        private final DeepNullB b;

        public CheckedDeepNullA(DeepNullB b) {
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CheckedDeepNullA other && Objects.equals(b, other.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(b);
        }
    }

    static final class DeepNullA {

        private final DeepNullB b;

        public DeepNullA(DeepNullB b) {
            if (b == null) {
                throw new NullPointerException("b");
            }

            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DeepNullA other && Objects.equals(b, other.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(b);
        }
    }

    static final class DeepNullB {

        private final Object o;

        public DeepNullB(Object o) {
            if (o == null) {
                throw new NullPointerException("o");
            }

            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DeepNullB other && Objects.equals(o, other.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }

    static final class ConstantFieldIsNull {

        @SuppressWarnings("unused")
        private static final String NULL_CONSTANT = null;
        private final Object o;

        public ConstantFieldIsNull(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstantFieldIsNull other && Objects.equals(o, other.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }

    static final class MixedNullFields {

        private final Object o;
        private final Object p;

        public MixedNullFields(Object o, Object p) {
            if (o == null) {
                throw new NullPointerException("o");
            }
            this.o = o;
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MixedNullFields)) {
                return false;
            }
            MixedNullFields other = (MixedNullFields) obj;
            return o.equals(other.o) && (p == null ? other.p == null : p.equals(other.p));
        }

        @Override
        public int hashCode() {
            return Objects.hash(o, p);
        }

        @Override
        public String toString() {
            return "Mixed[" + o + ", " + p + "]";
        }
    }
}
