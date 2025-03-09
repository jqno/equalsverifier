package nl.jqno.equalsverifier.integration.extended_contract;

import java.io.Serializable;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class RecordsTest {

    @Test
    void succeed_whenClassIsARecord() {
        EqualsVerifier.forClass(SimpleRecord.class).verify();
    }

    @Test
    void succeed_whenClassIsAPrivateRecord() {
        EqualsVerifier.forClass(PrivateSimpleRecord.class).verify();
    }

    @Test
    void fail_whenConstructorChecksNull() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NullCheckingRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record:", "failed to run constructor", "Warning.NULL_FIELDS");
    }

    @Test
    void succeed_whenConstructorChecksNull_givenSuppressedWarning() {
        EqualsVerifier.forClass(NullCheckingRecord.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void fail_whenConstructorChecksValue() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ValueCheckingRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record:", "failed to run constructor", "prefab values");
    }

    @Test
    void fail_whenRecordInvariantIsViolated_givenIntFieldIsModifiedInConstructor() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(BrokenInvariantIntFieldRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record invariant", "intField");
    }

    @Test
    void fail_whenRecordInvariantIsViolated_givenStringFieldIsModifiedInConstructor() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(BrokenInvariantStringFieldRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record invariant", "stringField");
    }

    @Test
    void fail_whenRecordInvariantIsViolated_givenBothFieldsAreModifiedInConstructor() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(BrokenInvariantBothRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record invariant", "intField", "stringField");
    }

    @Test
    void succeed_whenRecordImplementsItsOwnEquals() {
        EqualsVerifier.forClass(EqualsRecord.class).verify();
    }

    @Test
    void fail_whenRecordImplementsItsOwnEquals_givenNotAllFieldsAreUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NotAllFieldsRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields");
    }

    @Test
    void fail_whenRecordConstructorThrows() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ThrowingConstructorRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record", "failed to run constructor");
    }

    @Test
    void fail_whenRecordConstructorThrowsNpe() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NullFieldRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record", "failed to run constructor");
    }

    @Test
    void fail_whenRecordAccessorThrows() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ThrowingAccessorRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record", "failed to run accessor method");
    }

    @Test
    void fail_whenRecordAccessorThrowsNpe() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NullAccessorRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record", "failed to run accessor method", "s()");
    }

    @Test
    void succeed_whenRecordContainsStaticField() {
        EqualsVerifier.forClass(StaticFieldRecord.class).verify();
    }

    @Test
    void succeed_whenRecordValidatesInput_givenValidPrefabValues() {
        EqualsVerifier
                .forClass(ValidatingConstructorRecord.class)
                .withPrefabValues(String.class, "valid-1", "valid-2")
                .verify();
    }

    @Test
    void succeed_whenRecordHasBoundedWildcardGeneric() {
        EqualsVerifier.forClass(WildcardGenericRecordContainer.class).verify();
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
            return obj instanceof EqualsRecord other && Objects.equals(i, other.i) && Objects.equals(s, other.s);
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

    record BoundedGenericRecord<T extends Serializable>(T t) {}

    record WildcardGenericRecordContainer(BoundedGenericRecord<?> bgr) {}
}
