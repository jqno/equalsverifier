package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.*;

import org.junit.Test;

public class RecordInstantiatorTest {
    @Test
    public void instantiateRecord() {
        Instantiator<?> instantiator = Instantiator.of(SimpleRecord.class);
        Object simpleRecord = instantiator.instantiate();
        assertEquals(SimpleRecord.class, simpleRecord.getClass());
    }

    record SimpleRecord(int i) {}
}
