package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.internal.instantiation.ClassProbe;
import org.junit.jupiter.api.Test;

public class ClassProbeRecordTest {

    @Test
    public void isRecord() {
        var probe = new ClassProbe<>(SimpleRecord.class);
        assertTrue(probe.isRecord());
    }

    record SimpleRecord(int i) {}
}
