package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Test;

public class RecordObjectAccessorTest extends StringCompilerTestBase {

    @Test
    public void of() {
        assumeTrue(isRecordsAvailable());
        Class<?> type = compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        Object instance = Instantiator.of(type).instantiate();
        ObjectAccessor<?> actual = ObjectAccessor.of(instance);
        assertTrue((actual instanceof RecordObjectAccessor));
    }

    private static final String SIMPLE_RECORD_CLASS_NAME = "SimpleRecord";
    private static final String SIMPLE_RECORD_CLASS =
            "public record SimpleRecord(int i, String s) {}";
}
