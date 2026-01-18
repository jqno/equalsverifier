package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InstantiatorFactoryTest {
    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void returnsConstructorInstantiator_forRecord() {
        assertThat(sut(SomeRecord.class)).isInstanceOf(ConstructorInstantiator.class);
    }

    @Test
    void returnsReflectionInstantiator_ifAllElseFails() {
        assertThat(sut(SomeClass.class)).isInstanceOf(ReflectionInstantiator.class);
    }

    private <T> Instantiator<T> sut(Class<T> type) {
        return InstantiatorFactory.of(ClassProbe.of(type), objenesis);
    }

    record SomeRecord(int i) {}

    @SuppressWarnings("unused")
    static class SomeClass {
        private final int i = 10;
    }
}
