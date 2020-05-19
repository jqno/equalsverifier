package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Before;
import org.junit.Test;

public class RecordsTest extends StringCompilerTestBase {

    private boolean isRecordsAvailable = false;

    @Before
    public void setup() {
        isRecordsAvailable = determineIsRecordsAvailable();
    }

    @Test
    public void succeed_whenClassIsARecord() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> type = compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenIntFieldIsModifiedInConstructor() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> type =
                compile(
                        BROKEN_INVARIANT_INT_FIELD_RECORD_CLASS_NAME,
                        BROKEN_INVARIANT_INT_FIELD_RECORD_CLASS);

        expectFailure("Record invariant", "intField");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenStringFieldIsModifiedInConstructor() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> type =
                compile(
                        BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS_NAME,
                        BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS);

        expectFailure("Record invariant", "stringField");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenBothFieldsAreModifiedInConstructor() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> type =
                compile(
                        BROKEN_INVARIANT_BOTH_RECORD_CLASS_NAME,
                        BROKEN_INVARIANT_BOTH_RECORD_CLASS);

        expectFailure("Record invariant", "intField", "stringField");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void succeed_whenRecordImplementsItsOwnEquals() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> type = compile(EQUALS_RECORD_CLASS_NAME, EQUALS_RECORD_CLASS);
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordImplementsItsOwnEquals_givenNotAllFieldsAreUsed() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> type = compile(NOT_ALL_FIELDS_RECORD_CLASS_NAME, NOT_ALL_FIELDS_RECORD_CLASS);

        expectFailure("Significant fields");
        EqualsVerifier.forClass(type).verify();
    }

    public boolean determineIsRecordsAvailable() {
        if (!isTypeAvailable("java.lang.Record")) {
            return false;
        }
        try {
            compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
            return true;
        } catch (AssertionError ignored) {
            // We're in Java 14 and preview features aren't enabled
            return false;
        }
    }

    private static final String SIMPLE_RECORD_CLASS_NAME = "SimpleRecord";
    private static final String SIMPLE_RECORD_CLASS = "record SimpleRecord(int i, String s) {}";

    private static final String BROKEN_INVARIANT_INT_FIELD_RECORD_CLASS_NAME =
            "BrokenInvariantIntFieldRecord";
    private static final String BROKEN_INVARIANT_INT_FIELD_RECORD_CLASS =
            "\nrecord BrokenInvariantIntFieldRecord(int intField, String stringField) {"
                    + "\n    public BrokenInvariantIntFieldRecord(int intField, String stringField) {"
                    + "\n        this.intField = intField + 1;"
                    + "\n        this.stringField = stringField;"
                    + "\n    }"
                    + "\n}";

    private static final String BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS_NAME =
            "BrokenInvariantStringFieldRecord";
    private static final String BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS =
            "\nrecord BrokenInvariantStringFieldRecord(int intField, String stringField) {"
                    + "\n    public BrokenInvariantStringFieldRecord(int intField, String stringField) {"
                    + "\n        this.intField = intField;"
                    + "\n        this.stringField = stringField + \"x\";"
                    + "\n    }"
                    + "\n}";

    private static final String BROKEN_INVARIANT_BOTH_RECORD_CLASS_NAME =
            "BrokenInvariantBothRecord";
    private static final String BROKEN_INVARIANT_BOTH_RECORD_CLASS =
            "\nrecord BrokenInvariantBothRecord(int intField, String stringField) {"
                    + "\n    public BrokenInvariantBothRecord(int intField, String stringField) {"
                    + "\n        this.intField = intField + 1;"
                    + "\n        this.stringField = stringField + \"x\";"
                    + "\n    }"
                    + "\n}";

    private static final String EQUALS_RECORD_CLASS_NAME = "EqualsRecord";
    private static final String EQUALS_RECORD_CLASS =
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;"
                    + "\n"
                    + "\nrecord EqualsRecord(int i, String s) {"
                    + "\n    @Override"
                    + "\n    public boolean equals(Object obj) {"
                    + "\n        return defaultEquals(this, obj);"
                    + "\n    }"
                    + "\n}";

    private static final String NOT_ALL_FIELDS_RECORD_CLASS_NAME = "NotAllFieldsRecord";
    private static final String NOT_ALL_FIELDS_RECORD_CLASS =
            "\nrecord NotAllFieldsRecord(int i, String s) {"
                    + "\n    @Override"
                    + "\n    public boolean equals(Object obj) {"
                    + "\n        if (!(obj instanceof NotAllFieldsRecord)) {"
                    + "\n            return false;"
                    + "\n        }"
                    + "\n        return i == ((NotAllFieldsRecord)obj).i;"
                    + "\n    }"
                    + "\n"
                    + "\n    @Override"
                    + "\n    public int hashCode() {"
                    + "\n        return i;"
                    + "\n    }"
                    + "\n}";
}
