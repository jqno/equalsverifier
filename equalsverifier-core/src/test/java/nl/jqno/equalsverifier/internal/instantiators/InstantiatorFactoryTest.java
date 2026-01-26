package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InstantiatorFactoryTest {
    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void returnsProvidedFactoryInstantiator_whenFactoryIsProvided() {
        InstanceFactory<SomeClass> factory = v -> new SomeClass();
        assertThat(sut(SomeClass.class, factory)).isInstanceOf(ProvidedFactoryInstantiator.class);
    }

    @Test
    void returnsConstructorInstantiator_forRecord() {
        assertThat(sut(SomeRecord.class)).isInstanceOf(ConstructorInstantiator.class);
    }

    @Test
    void returnConstructorInstantiator_forClassWhenConstructorMatchesFields() {
        assertThat(sut(ConstructorMatchesFields.class)).isInstanceOf(ConstructorInstantiator.class);
    }

    @Test
    void returnsReflectionInstantiator_ifAllElseFails() {
        assertThat(sut(SomeClass.class)).isInstanceOf(ReflectionInstantiator.class);
    }

    private <T> Instantiator<T> sut(Class<T> type) {
        return sut(type, null);
    }

    private <T> Instantiator<T> sut(Class<T> type, InstanceFactory<T> factory) {
        return InstantiatorFactory.of(ClassProbe.of(type), factory, objenesis);
    }

    record SomeRecord(int i) {}

    @SuppressWarnings("unused")
    static class SomeClass {
        private final int i = 10;
    }

    static final class ConstructorMatchesFields {
        private final int i;
        private final String s;

        public ConstructorMatchesFields(int i, String s) {
            this.i = i;
            this.s = s;
        }
    }
}
