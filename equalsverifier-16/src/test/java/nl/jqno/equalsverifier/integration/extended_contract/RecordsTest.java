package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.RecordsHelper;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class RecordsTest {

    @Test
    public void sanityCheckForRecordsHelper() {
        assertTrue(RecordsHelper.isRecord(SimpleRecord.class));
        assertFalse(RecordsHelper.isRecord(String.class));
    }

    @Test
    public void succeed_whenClassIsARecord() {
        EqualsVerifier.forClass(SimpleRecord.class).verify();
    }

    @Test
    public void succeed_whenClassIsAPrivateRecord() {
        EqualsVerifier.forClass(PrivateSimpleRecord.class).verify();
    }

    @Test
    public void succeed_whenConstructorChecksNull() {
        EqualsVerifier.forClass(NullCheckingRecord.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    public void fail_whenConstructorChecksValue() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(ValueCheckingRecord.class).verify())
            .assertFailure()
            .assertMessageContains(
                "Record:",
                "failed to invoke constructor",
                "Warning.ZERO_FIELDS"
            );
    }

    @Test
    public void succeed_whenConstructorChecksValue_givenPrefabValues() {
        EqualsVerifier
            .forClass(ValueCheckingRecord.class)
            .withPrefabValues(int.class, 10, 11)
            .verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenIntFieldIsModifiedInConstructor() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(BrokenInvariantIntFieldRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Record invariant", "intField");
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenStringFieldIsModifiedInConstructor() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(BrokenInvariantStringFieldRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Record invariant", "stringField");
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenBothFieldsAreModifiedInConstructor() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(BrokenInvariantBothRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Record invariant", "intField", "stringField");
    }

    @Test
    public void succeed_whenRecordImplementsItsOwnEquals() {
        EqualsVerifier.forClass(EqualsRecord.class).verify();
    }

    @Test
    public void fail_whenRecordImplementsItsOwnEquals_givenNotAllFieldsAreUsed() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(NotAllFieldsRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Significant fields");
    }

    @Test
    public void fail_whenRecordConstructorThrows() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(ThrowingConstructorRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Record", "failed to invoke constructor");
    }

    @Test
    public void fail_whenRecordConstructorThrowsNpe() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(NullFieldRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Record", "failed to invoke constructor");
    }

    @Test
    public void fail_whenRecordAccessorThrows() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(ThrowingAccessorRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Record", "failed to invoke accessor method");
    }

    @Test
    public void fail_whenRecordAccessorThrowsNpe() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(NullAccessorRecord.class).verify())
            .assertFailure()
            .assertMessageContains("Record", "failed to invoke accessor method", "s()");
    }

    @Test
    public void succeed_whenRecordContainsStaticField() {
        EqualsVerifier.forClass(StaticFieldRecord.class).verify();
    }

    @Test
    public void succeed_whenRecordValidatesInput_givenValidPrefabValues() {
        EqualsVerifier
            .forClass(ValidatingConstructorRecord.class)
            .withPrefabValues(String.class, "valid-1", "valid-2")
            .verify();
    }

    record SimpleRecord(int i, String s) {}

    private record PrivateSimpleRecord(int i, String s) {}

    record NullCheckingRecord(String s) {
        public NullCheckingRecord {
            Objects.requireNonNull(s);
        }
    }

    record ValueCheckingRecord(int i) {
        public ValueCheckingRecord {
            if (i <= 3) {
                throw new IllegalStateException("Error: expected " + i + " > 3");
            }
        }
    }

    record BrokenInvariantIntFieldRecord(int intField, String stringField) {
        public BrokenInvariantIntFieldRecord(int intField, String stringField) {
            this.intField = intField + 1;
            this.stringField = stringField;
        }
    }

    record BrokenInvariantStringFieldRecord(int intField, String stringField) {
        public BrokenInvariantStringFieldRecord(int intField, String stringField) {
            this.intField = intField;
            this.stringField = stringField + "x";
        }
    }

    record BrokenInvariantBothRecord(int intField, String stringField) {
        public BrokenInvariantBothRecord(int intField, String stringField) {
            this.intField = intField + 1;
            this.stringField = stringField + "x";
        }
    }

    record EqualsRecord(int i, String s) {
        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }
    }

    record NotAllFieldsRecord(int i, String s) {
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NotAllFieldsRecord)) {
                return false;
            }
            return i == ((NotAllFieldsRecord) obj).i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    record ThrowingConstructorRecord(int i, String s) {
        public ThrowingConstructorRecord {
            throw new IllegalStateException();
        }
    }

    record NullFieldRecord(int i, String s) {
        public NullFieldRecord {
            s.length();
        }
    }

    record ThrowingAccessorRecord(int i, String s) {
        public ThrowingAccessorRecord(int i, String s) {
            this.i = i;
            this.s = s + "x";
        }

        public int i() {
            throw new IllegalStateException();
        }
    }

    record NullAccessorRecord(String s, String t) {
        public NullAccessorRecord(String s, String t) {
            this.s = s;
            this.t = t + "x";
        }

        public String s() {
            throw new NullPointerException();
        }
    }

    record StaticFieldRecord(int i, String s) {
        private static final int X = 0;
    }

    record ValidatingConstructorRecord(String s) {
        public ValidatingConstructorRecord {
            if (s != null && !s.startsWith("valid-")) {
                throw new IllegalStateException("rejected");
            }
        }
    }
}
