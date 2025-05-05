package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

class RecordInstanceCreatorTest {

    @Test
    void instanceCreator() throws NoSuchFieldException {
        ClassProbe<SomeRecord> probe = ClassProbe.of(SomeRecord.class);
        var sut = new InstanceCreator<RecordInstanceCreatorTest.SomeRecord>(probe, new ObjenesisStd());

        Field x = SomeRecord.class.getDeclaredField("x");
        Field z = SomeRecord.class.getDeclaredField("z");
        var values = Map.<Field, Object>of(x, 42, z, "42");

        SomeRecord actual = sut.instantiate(values);

        assertThat(actual.x).isEqualTo(42);
        assertThat(actual.y).isEqualTo(0);
        assertThat(actual.z).isEqualTo("42");
    }

    record SomeRecord(int x, int y, String z) {}
}
