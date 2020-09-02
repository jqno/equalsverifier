package nl.jqno.equalsverifier.integration.extended_contract;

import static org.junit.Assume.*;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Before;
import org.junit.Test;

public class RecordsTest extends StringCompilerTestBase {

    @Before
    public void setup() {
        assumeTrue(isRecordsAvailable());
    }

    @Test
    public void succeed_whenClassIsARecord() {
        Class<?> type = compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenIntFieldIsModifiedInConstructor() {
        Class<?> type =
                compile(
                        BROKEN_INVARIANT_INT_FIELD_RECORD_CLASS_NAME,
                        BROKEN_INVARIANT_INT_FIELD_RECORD_CLASS);

        expectFailure("Record invariant", "intField");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenStringFieldIsModifiedInConstructor() {
        Class<?> type =
                compile(
                        BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS_NAME,
                        BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS);

        expectFailure("Record invariant", "stringField");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordInvariantIsViolated_givenBothFieldsAreModifiedInConstructor() {
        Class<?> type =
                compile(
                        BROKEN_INVARIANT_BOTH_RECORD_CLASS_NAME,
                        BROKEN_INVARIANT_BOTH_RECORD_CLASS);

        expectFailure("Record invariant", "intField", "stringField");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void succeed_whenRecordImplementsItsOwnEquals() {
        Class<?> type = compile(EQUALS_RECORD_CLASS_NAME, EQUALS_RECORD_CLASS);
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordImplementsItsOwnEquals_givenNotAllFieldsAreUsed() {
        Class<?> type = compile(NOT_ALL_FIELDS_RECORD_CLASS_NAME, NOT_ALL_FIELDS_RECORD_CLASS);

        expectFailure("Significant fields");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordConstructorThrows() {
        Class<?> type =
                compile(THROWING_CONSTRUCTOR_RECORD_CLASS_NAME, THROWING_CONSTRUCTOR_RECORD_CLASS);

        expectFailure("Record", "failed to invoke constructor");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordConstructorThrowsNpe() {
        Class<?> type = compile(NULL_CONSTRUCTOR_RECORD_CLASS_NAME, NULL_CONSTRUCTOR_RECORD_CLASS);

        expectFailure("Record", "failed to invoke constructor");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordAccessorThrows() {
        Class<?> type =
                compile(THROWING_ACCESSOR_RECORD_CLASS_NAME, THROWING_ACCESSOR_RECORD_CLASS);

        expectFailure("Record", "failed to invoke accessor method");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void fail_whenRecordAccessorThrowsNpe() {
        Class<?> type = compile(NULL_ACCESSOR_RECORD_CLASS_NAME, NULL_ACCESSOR_RECORD_CLASS);

        expectFailure("Record", "failed to invoke accessor method");
        EqualsVerifier.forClass(type).verify();
    }

    @Test
    public void succeed_whenRecordContainsStaticField() {
        Class<?> type = compile(STATIC_FIELD_RECORD_CLASS_NAME, STATIC_FIELD_RECORD_CLASS);
        EqualsVerifier.forClass(type).verify();
    }

    public boolean isRecordsAvailable() {
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
                    + "\n    public BrokenInvariantIntFieldRecord {"
                    + "\n        this.intField = intField + 1;"
                    + "\n    }"
                    + "\n}";

    private static final String BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS_NAME =
            "BrokenInvariantStringFieldRecord";
    private static final String BROKEN_INVARIANT_STRING_FIELD_RECORD_CLASS =
            "\nrecord BrokenInvariantStringFieldRecord(int intField, String stringField) {"
                    + "\n    public BrokenInvariantStringFieldRecord {"
                    + "\n        this.stringField = stringField + \"x\";"
                    + "\n    }"
                    + "\n}";

    private static final String BROKEN_INVARIANT_BOTH_RECORD_CLASS_NAME =
            "BrokenInvariantBothRecord";
    private static final String BROKEN_INVARIANT_BOTH_RECORD_CLASS =
            "\nrecord BrokenInvariantBothRecord(int intField, String stringField) {"
                    + "\n    public BrokenInvariantBothRecord {"
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

    private static final String THROWING_CONSTRUCTOR_RECORD_CLASS_NAME =
            "ThrowingConstructorRecord";
    private static final String THROWING_CONSTRUCTOR_RECORD_CLASS =
            "\nrecord ThrowingConstructorRecord(int i, String s) {"
                    + "\n    public ThrowingConstructorRecord {"
                    + "\n        throw new IllegalStateException();"
                    + "\n    }"
                    + "\n}";

    private static final String NULL_CONSTRUCTOR_RECORD_CLASS_NAME = "NullFieldRecord";
    private static final String NULL_CONSTRUCTOR_RECORD_CLASS =
            "\nrecord NullFieldRecord(int i, String s) {"
                    + "\n    public NullFieldRecord {"
                    + "\n        s.length();"
                    + "\n    }"
                    + "\n}";

    private static final String THROWING_ACCESSOR_RECORD_CLASS_NAME = "ThrowingAccessorRecord";
    private static final String THROWING_ACCESSOR_RECORD_CLASS =
            "\nrecord ThrowingAccessorRecord(int i, String s) {"
                    + "\n    public ThrowingAccessorRecord {"
                    + "\n        this.s = s + \"x\";"
                    + "\n    }"
                    + "\n"
                    + "\n    public int i() {"
                    + "\n        throw new IllegalStateException();"
                    + "\n    }"
                    + "\n}";

    private static final String NULL_ACCESSOR_RECORD_CLASS_NAME = "NullAccessorRecord";
    private static final String NULL_ACCESSOR_RECORD_CLASS =
            "\nrecord NullAccessorRecord(String s, String t) {"
                    + "\n    public NullAccessorRecord {"
                    + "\n        this.t = \"\" + t;"
                    + "\n    }"
                    + "\n"
                    + "\n    public String s() {"
                    + "\n        s.length();"
                    + "\n        return s;"
                    + "\n    }"
                    + "\n}";
    private static final String STATIC_FIELD_RECORD_CLASS_NAME = "StaticFieldRecord";
    private static final String STATIC_FIELD_RECORD_CLASS =
            "record StaticFieldRecord(int i, String s) {\n"
                    + "    private static final int X = 0;\n"
                    + "}";
}
