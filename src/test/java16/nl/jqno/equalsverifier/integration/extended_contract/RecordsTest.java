package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;

import org.junit.Test;

public class RecordsTest extends ExpectedExceptionTestBase {

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
        expectFailure("Record invariant", "intField");
        EqualsVerifier.forClass(BrokenInvariantIntFieldRecord.class).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenStringFieldIsModifiedInConstructor() {
        expectFailure("Record invariant", "stringField");
        EqualsVerifier.forClass(BrokenInvariantStringFieldRecord.class).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenBothFieldsAreModifiedInConstructor() {
        expectFailure("Record invariant", "intField", "stringField");
        EqualsVerifier.forClass(BrokenInvariantBothRecord.class).verify();
    }

    @Test
    public void succeed_whenRecordImplementsItsOwnEquals() {
        EqualsVerifier.forClass(EqualsRecord.class).verify();
    }

    @Test
    public void fail_whenRecordImplementsItsOwnEquals_givenNotAllFieldsAreUsed() {
        expectFailure("Significant fields");
        EqualsVerifier.forClass(NotAllFieldsRecord.class).verify();
    }

    @Test
    public void fail_whenRecordConstructorThrows() {
        expectFailure("Record", "failed to invoke constructor");
        EqualsVerifier.forClass(ThrowingConstructorRecord.class).verify();
    }

    @Test
    public void fail_whenRecordConstructorThrowsNpe() {
        expectFailure("Record", "failed to invoke constructor");
        EqualsVerifier.forClass(NullFieldRecord.class).verify();
    }

    @Test
    public void fail_whenRecordAccessorThrows() {
        expectFailure("Record", "failed to invoke accessor method");
        EqualsVerifier.forClass(ThrowingAccessorRecord.class).verify();
    }

    @Test
    public void fail_whenRecordAccessorThrowsNpe() {
        expectFailure("Record", "failed to invoke accessor method", "s()");
        EqualsVerifier.forClass(NullAccessorRecord.class).verify();
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
