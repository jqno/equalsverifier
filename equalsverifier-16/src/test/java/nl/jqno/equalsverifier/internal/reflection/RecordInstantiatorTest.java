package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

public class RecordInstantiatorTest {

    @Test
    public void instantiateRecord() {
        Instantiator<?> instantiator = Instantiator.of(SimpleRecord.class, new ObjenesisStd());
        Object simpleRecord = instantiator.instantiate();
        assertEquals(SimpleRecord.class, simpleRecord.getClass());
    }

    record SimpleRecord(int i) {}
}
