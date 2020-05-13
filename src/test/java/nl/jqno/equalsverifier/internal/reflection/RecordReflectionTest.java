package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertTrue;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Before;
import org.junit.Test;

public class RecordReflectionTest extends StringCompilerTestBase {

    private boolean isRecordsAvailable = false;
    private PrefabValues prefabValues;

    @Before
    public void setup() {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
        isRecordsAvailable = determineIsRecordsAvailable();
    }

    @Test
    public void sanity() {
        if (!isRecordsAvailable) {
            return;
        }
        assertTrue(false);
    }

    @Test
    public void isRecord() {
        if (!isRecordsAvailable) {
            return;
        }
        Class<?> record = compile(RECORD_CLASS_NAME, RECORD_CLASS);
        ClassAccessor<?> accessor = ClassAccessor.of(record, prefabValues);
        assertTrue(accessor.isRecord());
    }

    public boolean determineIsRecordsAvailable() {
        if (!isTypeAvailable("java.lang.Record")) {
            return false;
        }
        try {
            compile(RECORD_CLASS_NAME, RECORD_CLASS);
            return true;
        } catch (AssertionError ignored) {
            // We're in Java 14 and preview features aren't enabled
            return false;
        }
    }

    private static final String RECORD_CLASS_NAME = "MyRecord";
    private static final String RECORD_CLASS = "record MyRecord(int i, String s) {}";
}
