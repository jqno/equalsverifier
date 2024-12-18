package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ClassProbeRecordTest {

    @Test
    public void isRecord() {
        var probe = ClassProbe.of(SimpleRecord.class);
        assertTrue(probe.isRecord());
    }

    record SimpleRecord(int i) {}
}
