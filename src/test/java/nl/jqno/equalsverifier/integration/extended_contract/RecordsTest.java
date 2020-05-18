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
    public void succeed_whenRecordImplementsItsOwnEquals() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> type = compile(EQUALS_RECORD_CLASS_NAME, EQUALS_RECORD_CLASS);
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
}
