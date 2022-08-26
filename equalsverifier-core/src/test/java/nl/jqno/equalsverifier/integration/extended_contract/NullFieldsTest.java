package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class NullFieldsTest {

    private static final String NON_NULLITY = "Non-nullity";
    private static final String EQUALS = "equals throws NullPointerException";
    private static final String HASHCODE = "hashCode throws NullPointerException";
    private static final String ON_FIELD = "on field";

    @Test
    public void fail_whenEqualsThrowsNpeOnThissField() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class).verify())
            .assertFailure()
            .assertCause(NullPointerException.class)
            .assertMessageContains(NON_NULLITY, EQUALS, ON_FIELD, "color");
    }

    @Test
    public void fail_whenEqualsThrowsNpeOnOthersField() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(EqualsThrowsNpeOnOther.class).verify())
            .assertFailure()
            .assertCause(NullPointerException.class)
            .assertMessageContains(NON_NULLITY, EQUALS, ON_FIELD, "color");
    }

    @Test
    public void fail_whenEqualsThrowsNpeOnStaticField() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(EqualsThrowsNpeOnStatic.class).verify())
            .assertFailure()
            .assertCause(NullPointerException.class)
            .assertMessageContains(NON_NULLITY, EQUALS, ON_FIELD, "color");
    }

    @Test
    public void fail_whenHashCodeThrowsNpe() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(HashCodeThrowsNpe.class).verify())
            .assertFailure()
            .assertCause(NullPointerException.class)
            .assertMessageContains(NON_NULLITY, HASHCODE, ON_FIELD, "color");
    }

    @Test
    public void succeed_whenEqualsThrowsNpeOnThissField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void succeed_whenEqualsTestFieldWhichThrowsNpe() {
        EqualsVerifier.forClass(CheckedDeepNullA.class).verify();
    }

    @Test
    public void succeed_whenEqualsThrowsNpeOnFieldWhichAlsoThrowsNpe_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(DeepNullA.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void succeed_whenDoingASanityCheckOnTheFieldUsedInThePreviousTests_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(DeepNullB.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void succeed_whenConstantFieldIsNull() {
        EqualsVerifier.forClass(ConstantFieldIsNull.class).verify();
    }

    @Test
    public void fail_whenClassHasNullChecksForOnlySomeFields() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(MixedNullFields.class).verify())
            .assertFailure()
            .assertCause(NullPointerException.class)
            .assertMessageContains(NON_NULLITY, EQUALS, ON_FIELD, "o");
    }

    @Test
    public void succeed_whenClassHasNullChecksForOnlySomeFields_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(MixedNullFields.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void succeed_whenClassHasNullChecksForOnlySomeFields_givenTheOtherFieldIsFlagged() {
        EqualsVerifier.forClass(MixedNullFields.class).withNonnullFields("o").verify();
    }

    @Test
    public void anExceptionIsThrown_whenANonExistingFieldIsGivenToWithNonnullFields() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(MixedNullFields.class)
                    .withNonnullFields("thisFieldDoesNotExist")
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "class MixedNullFields does not contain field thisFieldDoesNotExist."
            );
    }

    @Test
    public void anExceptionIsThrown_whenWithNonnullFieldsOverlapsWithSuppressWarnings() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(MixedNullFields.class)
                    .withNonnullFields("o")
                    .suppress(Warning.NULL_FIELDS)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "you can call either withNonnullFields or suppress Warning.NULL_FIELDS, but not both."
            );
    }

    @Test
    public void anExceptionIsThrown_whenSuppressWarningsOverlapsWithWithNonnullFields() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(MixedNullFields.class)
                    .suppress(Warning.NULL_FIELDS)
                    .withNonnullFields("o")
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "you can call either withNonnullFields or suppress Warning.NULL_FIELDS, but not both."
            );
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
            return defaultHashCode(this);
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
            return defaultHashCode(this);
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
            return defaultHashCode(this);
        }
    }

    static final class HashCodeThrowsNpe {

        private final Color color;

        public HashCodeThrowsNpe(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class ConstantFieldIsNull {

        private static final String NULL_CONSTANT = null;
        private final Object o;

        public ConstantFieldIsNull(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
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
            return defaultHashCode(this);
        }

        @Override
        public String toString() {
            return "Mixed[" + o + ", " + p + "]";
        }
    }
}
