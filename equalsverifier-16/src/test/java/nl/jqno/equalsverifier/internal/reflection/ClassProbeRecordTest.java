package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ClassProbeRecordTest {

    @Test
    void isRecord() {
        var probe = ClassProbe.of(SimpleRecord.class);
        assertThat(probe.isRecord()).isTrue();
    }

    record SimpleRecord(int i) {}
}
