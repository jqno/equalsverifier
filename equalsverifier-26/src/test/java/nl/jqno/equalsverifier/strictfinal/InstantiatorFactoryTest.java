package nl.jqno.equalsverifier.strictfinal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.instantiators.*;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InstantiatorFactoryTest {
    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void returnsProvidedFactoryInstantiator_whenFactoryIsProvided() {
        InstanceFactory<SomeClassWithFinalFieldWithNonmatchingConstructor> factory =
                v -> new SomeClassWithFinalFieldWithNonmatchingConstructor(Integer.valueOf(v.getString("s")));
        assertThat(sut(SomeClassWithFinalFieldWithNonmatchingConstructor.class, factory))
                .isInstanceOf(ProvidedFactoryInstantiator.class);
    }

    @Test
    void returnReflectionConstructorInstantiator_whenClassHasNoFinalFields() {
        assertThat(sut(SomeClassWithoutFinalField.class)).isInstanceOf(ReflectionInstantiator.class);
    }

    @Test
    void returnClassConstructorInstantiator_whenConstructorMatchesFields() {
        assertThat(sut(SomeClassWithFinalFieldWithMatchingConstructor.class))
                .isInstanceOf(ClassConstructorInstantiator.class);
    }

    @Test
    void returnReflectionConstructorInstantiator_whenAllElseFails() {
        assertThatThrownBy(() -> sut(SomeClassWithFinalFieldWithNonmatchingConstructor.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Use #withFactory()");
    }

    private <T> Instantiator<T> sut(Class<T> type) {
        return sut(type, null);
    }

    private <T> Instantiator<T> sut(Class<T> type, InstanceFactory<T> factory) {
        return InstantiatorFactory.of(ClassProbe.of(type), factory, objenesis, true);
    }

    @SuppressWarnings("unused")
    static class SomeClassWithoutFinalField {
        private int i = 10;
    }

    @SuppressWarnings("unused")
    static class SomeClassWithFinalFieldWithNonmatchingConstructor {
        private final String s;

        public SomeClassWithFinalFieldWithNonmatchingConstructor(int i) {
            this.s = "" + i;
        }
    }

    @SuppressWarnings("unused")
    static class SomeClassWithFinalFieldWithMatchingConstructor {
        private final int i;

        public SomeClassWithFinalFieldWithMatchingConstructor(int i) {
            this.i = i;
        }
    }
}
