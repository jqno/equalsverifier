package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.util.Arrays;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.jupiter.api.Test;

public class MutableStateTest extends ExpectedExceptionTestBase {
    private static final String MUTABILITY = "Mutability: equals depends on mutable field";
    private static final String FIELD_NAME = "field";

    @Test
    public void fail_whenClassHasAMutablePrimitiveField() {
        expectFailure(MUTABILITY, "second");
        EqualsVerifier.forClass(PrimitiveMutableField.class).verify();
    }

    @Test
    public void succeed_whenClassHasAMutablePrimitiveField_givenItDoesNotUseThatFieldInEquals() {
        EqualsVerifier.forClass(UnusedPrimitiveMutableField.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void succeed_whenClassHasAMutablePrimitiveField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(PrimitiveMutableField.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenClassHasAMutableObjectField() {
        expectFailure(MUTABILITY, FIELD_NAME);
        EqualsVerifier.forClass(ObjectMutableField.class).verify();
    }

    @Test
    public void succeed_whenClassHasAMutableObjectField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(ObjectMutableField.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenClassHasAMutableEnumField() {
        expectFailure(MUTABILITY, FIELD_NAME);
        EqualsVerifier.forClass(EnumMutableField.class).verify();
    }

    @Test
    public void succeed_whenClassHasAMutableEnumField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(EnumMutableField.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void fail_whenClassHasAMutableArrayField() {
        expectFailure(MUTABILITY, FIELD_NAME);
        EqualsVerifier.forClass(ArrayMutableField.class).verify();
    }

    @Test
    public void succeed_whenClassHasAMutableArrayField_givenWarningIsSuppressed() {
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

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class ObjectMutableField {
        private Object field;

        public ObjectMutableField(Object value) {
            field = value;
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

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class EnumMutableField {
        public enum Enum {
            RED,
            BLUE
        }

        private Enum field;

        public EnumMutableField(Enum value) {
            field = value;
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
            return (field == null) ? 0 : Arrays.hashCode(field);
        }
    }
}
