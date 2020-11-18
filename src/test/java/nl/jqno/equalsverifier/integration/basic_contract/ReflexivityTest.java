package nl.jqno.equalsverifier.integration.basic_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;

public class ReflexivityTest {
    @Test
    public void fail_whenReferencesAreNotEqual() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(ReflexivityIntentionallyBroken.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains(
                        "Reflexivity",
                        "object does not equal itself",
                        ReflexivityIntentionallyBroken.class.getSimpleName());
    }

    @Test
    public void fail_whenTheWrongFieldsAreComparedInEquals() {
        ExpectedException.when(() -> EqualsVerifier.forClass(FieldsMixedUpInEquals.class).verify())
                .assertFailure()
                .assertMessageContains(
                        "Reflexivity",
                        "object does not equal an identical copy of itself",
                        FieldsMixedUpInEquals.class.getSimpleName());
    }

    @Test
    public void fail_whenReferencesAreNotEqual_givenFieldsThatAreNull() {
        ExpectedException.when(
                        () -> EqualsVerifier.forClass(ReflexivityBrokenOnNullFields.class).verify())
                .assertFailure()
                .assertMessageContains(
                        "Reflexivity", ReflexivityBrokenOnNullFields.class.getSimpleName());
    }

    @Test
    public void succeed_whenReferencesAreNotEqual_givenFieldsThatAreNullAndWarningIsSuppressed() {
        EqualsVerifier.forClass(ReflexivityBrokenOnNullFields.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenObjectIsInstanceofCheckedWithWrongClass() {
        ExpectedException.when(() -> EqualsVerifier.forClass(WrongInstanceofCheck.class).verify())
                .assertFailure()
                .assertMessageContains(
                        "Reflexivity",
                        "object does not equal an identical copy of itself",
                        WrongInstanceofCheck.class.getSimpleName());
    }

    @Test
    public void fail_whenEqualsReturnsFalse_givenObjectsThatAreIdentical() {
        ExpectedException.when(
                        () -> EqualsVerifier.forClass(SuperCallerWithUnusedField.class).verify())
                .assertFailure()
                .assertMessageContains("Reflexivity", "identical copy");
    }

    @Test
    public void
            succeed_whenEqualsReturnsFalse_givenObjectsThatAreIdenticalAndWarningIsSuppressed() {
        EqualsVerifier.forClass(SuperCallerWithUnusedField.class)
                .suppress(Warning.IDENTICAL_COPY, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenIdenticalCopyWarningIsSuppressedUnnecessarily() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(FinalPoint.class)
                                        .suppress(Warning.IDENTICAL_COPY)
                                        .verify())
                .assertFailure()
                .assertMessageContains("Unnecessary suppression", "IDENTICAL_COPY");
    }

    static final class ReflexivityIntentionallyBroken extends Point {
        // Instantiator.scramble will flip this boolean.
        private boolean broken = false;

        public ReflexivityIntentionallyBroken(int x, int y) {
            super(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (broken && this == obj) {
                return false;
            }
            return super.equals(obj);
        }
    }

    static final class FieldsMixedUpInEquals {
        private final String one;
        private final String two;

        @SuppressWarnings("unused")
        private final String unused;

        public FieldsMixedUpInEquals(String one, String two, String unused) {
            this.one = one;
            this.two = two;
            this.unused = unused;
        }

        @Override
        public boolean equals(Object obj) {
            // EV must also find the error when equals short-circuits.
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof FieldsMixedUpInEquals)) {
                return false;
            }
            FieldsMixedUpInEquals other = (FieldsMixedUpInEquals) obj;
            return Objects.equals(two, other.one) && Objects.equals(two, other.two);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class ReflexivityBrokenOnNullFields {
        private final Object a;

        public ReflexivityBrokenOnNullFields(Object a) {
            this.a = a;
        }

        // This equals method was generated by Eclipse, except for the indicated line.
        // CHECKSTYLE OFF: NeedBraces
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            ReflexivityBrokenOnNullFields other = (ReflexivityBrokenOnNullFields) obj;
            if (a == null) {
                if (other.a != null) return false;
                // The following line was added to cause equals to be broken on reflexivity.
                return false;
            } else if (!a.equals(other.a)) return false;
            return true;
        }
        // CHECKSTYLE ON: NeedBraces

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class WrongInstanceofCheck {
        private final int foo;

        public WrongInstanceofCheck(int foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SomethingCompletelyDifferent)) {
                return false;
            }
            WrongInstanceofCheck other = (WrongInstanceofCheck) obj;
            return foo == other.foo;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class SomethingCompletelyDifferent {}

    static final class SuperCallerWithUnusedField {
        @SuppressWarnings("unused")
        private final int unused;

        public SuperCallerWithUnusedField(int unused) {
            this.unused = unused;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
