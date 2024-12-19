package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

class RecordInstantiatorTest {

    @Test
    void instantiateRecord() {
        Instantiator<?> instantiator = Instantiator.of(SimpleRecord.class, new ObjenesisStd());
        Object simpleRecord = instantiator.instantiate();
        assertThat(simpleRecord.getClass()).isEqualTo(SimpleRecord.class);
    }

    record SimpleRecord(int i) {}
}
