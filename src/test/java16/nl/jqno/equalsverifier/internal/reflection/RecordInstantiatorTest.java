package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Test;

public class RecordInstantiatorTest extends StringCompilerTestBase {
    @Test
    public void instantiateRecord() {
        assumeTrue(isRecordsAvailable());
        Class<?> simpleRecordClass = compileSimpleRecord();
        Instantiator<?> instantiator = Instantiator.of(simpleRecordClass);
        Object simpleRecord = instantiator.instantiate();
        assertEquals(simpleRecordClass, simpleRecord.getClass());
    }
}
