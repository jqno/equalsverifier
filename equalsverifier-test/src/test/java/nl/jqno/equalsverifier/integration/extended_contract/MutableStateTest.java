package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Arrays;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class MutableStateTest {

    private static final String MUTABILITY = "Mutability: equals depends on mutable field";
    private static final String FIELD_NAME = "field";

    @Test
    void fail_whenClassHasAMutablePrimitiveField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(PrimitiveMutableField.class).verify())
                .assertFailure()
                .assertMessageContains(MUTABILITY, "second");
    }

    @Test
    void succeed_whenClassHasAMutablePrimitiveField_givenItDoesNotUseThatFieldInEquals() {
        EqualsVerifier.forClass(UnusedPrimitiveMutableField.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    void succeed_whenClassHasAMutablePrimitiveField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(PrimitiveMutableField.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    void fail_whenClassHasAMutableObjectField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ObjectMutableField.class).verify())
                .assertFailure()
                .assertMessageContains(MUTABILITY, FIELD_NAME);
    }

    @Test
    void succeed_whenClassHasAMutableObjectField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(ObjectMutableField.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    void fail_whenClassHasAMutableEnumField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(EnumMutableField.class).verify())
                .assertFailure()
                .assertMessageContains(MUTABILITY, FIELD_NAME);
    }

    @Test
    void succeed_whenClassHasAMutableEnumField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(EnumMutableField.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    void fail_whenClassHasAMutableArrayField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ArrayMutableField.class).verify())
                .assertFailure()
                .assertMessageContains(MUTABILITY, FIELD_NAME);
    }

    @Test
    void succeed_whenClassHasAMutableArrayField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(ArrayMutableField.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    static final class PrimitiveMutableField {

        private int second;

        PrimitiveMutableField(int second) {
            this.second = second;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PrimitiveMutableField)) {
                return false;
            }
            return second == ((PrimitiveMutableField) obj).second;
        }

        @Override
        public int hashCode() {
            return second;
        }
    }

    static final class UnusedPrimitiveMutableField {

        private final int immutable;

        @SuppressWarnings("unused")
        private int mutable = 0;

        public UnusedPrimitiveMutableField(int value) {
            immutable = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UnusedPrimitiveMutableField)) {
                return false;
            }
            return immutable == ((UnusedPrimitiveMutableField) obj).immutable;
        }

        @Override
        public int hashCode() {
            return immutable;
        }
    }

    static final class ObjectMutableField {

        private Object field;

        public ObjectMutableField(Object value) {
            field = value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ObjectMutableField other && Objects.equals(field, other.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }
    }

    static final class EnumMutableField {

        public enum Color {
            RED, BLUE
        }

        private Color field;

        public EnumMutableField(Color value) {
            field = value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EnumMutableField other && Objects.equals(field, other.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }
    }

    static final class ArrayMutableField {

        private int[] field;

        ArrayMutableField(int[] value) {
            field = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ArrayMutableField)) {
                return false;
            }
            return Arrays.equals(field, ((ArrayMutableField) obj).field);
        }

        @Override
        public int hashCode() {
            return field == null ? 0 : Arrays.hashCode(field);
        }
    }
}
