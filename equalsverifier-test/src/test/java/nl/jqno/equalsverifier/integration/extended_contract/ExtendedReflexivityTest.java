package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class ExtendedReflexivityTest {

    @Test
    void succeed_whenEqualsUsesEqualsMethodForObjects() {
        EqualsVerifier.forClass(UsesEqualsMethod.class).verify();
    }

    @Test
    void fail_whenEqualsUsesDoubleEqualSignForObjects() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UsesDoubleEqualSign.class).verify())
                .assertFailure()
                .assertMessageContains("Reflexivity", "== used instead of .equals()", "stringField");
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForObject_givenDoubleEqualWarningIsSuppressed() {
        EqualsVerifier.forClass(UsesDoubleEqualSign.class).suppress(Warning.REFERENCE_EQUALITY).verify();
    }

    @Test
    void fail_whenEqualsUsesDoubleEqualSignForBoxedCharacterPrimitives() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UsesDoubleEqualSignOnBoxedCharacterPrimitive.class).verify())
                .assertFailure()
                .assertMessageContains("Reflexivity", "== used instead of .equals()", "characterField");
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForBoxedCharacterPrimitives_givenDoubleEqualWarningIsSuppressed() {
        EqualsVerifier
                .forClass(UsesDoubleEqualSignOnBoxedCharacterPrimitive.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    void fail_whenEqualsUsesDoubleEqualSignForBoxedIntegerPrimitives() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UsesDoubleEqualSignOnBoxedIntegerPrimitive.class).verify())
                .assertFailure()
                .assertMessageContains("Reflexivity", "== used instead of .equals()", "integerField");
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForBoxedIntegerPrimitives_givenDoubleEqualWarningIsSuppressed() {
        EqualsVerifier
                .forClass(UsesDoubleEqualSignOnBoxedIntegerPrimitive.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    void fail_whenEqualsUsesDoubleEqualSignForBoxedLongPrimitives() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UsesDoubleEqualSignOnBoxedLongPrimitive.class).verify())
                .assertFailure()
                .assertMessageContains("Reflexivity", "== used instead of .equals()", "longField");
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForBoxedLongPrimitives_givenDoubleEqualWarningIsSuppressed() {
        EqualsVerifier
                .forClass(UsesDoubleEqualSignOnBoxedLongPrimitive.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    void fail_whenEqualsUsesDoubleEqualSignForBoxedShortPrimitives() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UsesDoubleEqualSignOnBoxedShortPrimitive.class).verify())
                .assertFailure()
                .assertMessageContains("Reflexivity", "== used instead of .equals()", "shortField");
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForBoxedShortPrimitives_givenDoubleEqualWarningIsSuppressed() {
        EqualsVerifier
                .forClass(UsesDoubleEqualSignOnBoxedShortPrimitive.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForObject_givenObjectDoesntDeclareEquals() {
        EqualsVerifier.forClass(FieldHasNoEquals.class).verify();
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForObject_givenObjectIsAnInterface() {
        EqualsVerifier.forClass(FieldIsInterface.class).verify();
    }

    @Test
    void succeed_whenEqualsUsesDoubleEqualSignForObject_givenObjectIsAnInterfaceWithEquals() {
        EqualsVerifier.forClass(FieldIsInterfaceWithEquals.class).verify();
    }

    static final class UsesEqualsMethod {

        private final String s;

        public UsesEqualsMethod(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesEqualsMethod)) {
                return false;
            }
            UsesEqualsMethod other = (UsesEqualsMethod) obj;
            return Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
    }

    static final class UsesDoubleEqualSign {

        private final String stringField;

        public UsesDoubleEqualSign(String s) {
            this.stringField = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesDoubleEqualSign)) {
                return false;
            }
            UsesDoubleEqualSign other = (UsesDoubleEqualSign) obj;
            return stringField == other.stringField;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stringField);
        }
    }

    static final class UsesDoubleEqualSignOnBoxedCharacterPrimitive {

        private final Character characterField;

        public UsesDoubleEqualSignOnBoxedCharacterPrimitive(Character c) {
            this.characterField = c;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesDoubleEqualSignOnBoxedCharacterPrimitive)) {
                return false;
            }
            UsesDoubleEqualSignOnBoxedCharacterPrimitive other = (UsesDoubleEqualSignOnBoxedCharacterPrimitive) obj;
            return characterField == other.characterField;
        }

        @Override
        public int hashCode() {
            return Objects.hash(characterField);
        }
    }

    static final class UsesDoubleEqualSignOnBoxedIntegerPrimitive {

        private final Integer integerField;

        public UsesDoubleEqualSignOnBoxedIntegerPrimitive(Integer i) {
            this.integerField = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesDoubleEqualSignOnBoxedIntegerPrimitive)) {
                return false;
            }
            UsesDoubleEqualSignOnBoxedIntegerPrimitive other = (UsesDoubleEqualSignOnBoxedIntegerPrimitive) obj;
            return integerField == other.integerField;
        }

        @Override
        public int hashCode() {
            return Objects.hash(integerField);
        }
    }

    static final class UsesDoubleEqualSignOnBoxedLongPrimitive {

        private final Long longField;

        public UsesDoubleEqualSignOnBoxedLongPrimitive(Long l) {
            this.longField = l;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesDoubleEqualSignOnBoxedLongPrimitive)) {
                return false;
            }
            UsesDoubleEqualSignOnBoxedLongPrimitive other = (UsesDoubleEqualSignOnBoxedLongPrimitive) obj;
            return longField == other.longField;
        }

        @Override
        public int hashCode() {
            return Objects.hash(longField);
        }
    }

    static final class UsesDoubleEqualSignOnBoxedShortPrimitive {

        private final Short shortField;

        public UsesDoubleEqualSignOnBoxedShortPrimitive(Short s) {
            this.shortField = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesDoubleEqualSignOnBoxedShortPrimitive)) {
                return false;
            }
            UsesDoubleEqualSignOnBoxedShortPrimitive other = (UsesDoubleEqualSignOnBoxedShortPrimitive) obj;
            return shortField == other.shortField;
        }

        @Override
        public int hashCode() {
            return Objects.hash(shortField);
        }
    }

    static final class FieldHasNoEquals {

        private final NoEquals field;

        public FieldHasNoEquals(NoEquals field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FieldHasNoEquals other && Objects.equals(field, other.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }

        static final class NoEquals {}
    }

    static final class FieldIsInterface {

        private final Interface field;

        public FieldIsInterface(Interface field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FieldIsInterface other && Objects.equals(field, other.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }

        interface Interface {}
    }

    static final class FieldIsInterfaceWithEquals {

        private final InterfaceWithEquals field;

        public FieldIsInterfaceWithEquals(InterfaceWithEquals field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FieldIsInterfaceWithEquals other && Objects.equals(field, other.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }

        interface InterfaceWithEquals {
            @Override
            boolean equals(Object obj);
        }
    }
}
