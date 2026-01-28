package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

class RecordConstructorInstantiatorTest {

    @Test
    void instanceCreator() throws NoSuchFieldException {
        var probe = ClassProbe.of(SomeRecord.class);
        var sut = InstantiatorFactory.of(probe, new ObjenesisStd(), false);

        var x = SomeRecord.class.getDeclaredField("x");
        var z = SomeRecord.class.getDeclaredField("z");
        var values = Map.<Field, Object>of(x, 42, z, "42");

        var actual = sut.instantiate(values);

        assertThat(actual.x).isEqualTo(42);
        assertThat(actual.y).isEqualTo(0);
        assertThat(actual.z).isEqualTo("42");
    }

    record SomeRecord(int x, int y, String z) {}
}
