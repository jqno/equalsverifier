package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class RecordsTest {

    @Test
    public void succeed_whenClassIsARecord() {
        EqualsVerifier.forClass(SimpleRecord.class).verify();
    }

    @Test
    public void succeed_whenClassIsAPrivateRecord() {
        EqualsVerifier.forClass(PrivateSimpleRecord.class).verify();
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

    record SimpleRecord(int i, String s) {}

    private record PrivateSimpleRecord(int i, String s) {}

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
}
