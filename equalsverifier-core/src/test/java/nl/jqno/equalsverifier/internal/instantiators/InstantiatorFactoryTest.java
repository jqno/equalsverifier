package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.exceptions.InstantiatorException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InstantiatorFactoryTest {
    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void returnsProvidedFactoryInstantiator_whenFactoryIsProvided() {
        InstanceFactory<SomeClassWithFinalField> factory = v -> new SomeClassWithFinalField();
        assertThat(sut(SomeClassWithFinalField.class, factory, false)).isInstanceOf(ProvidedFactoryInstantiator.class);
    }

    @Test
    void returnsRecordConstructorInstantiator_forRecord() {
        assertThat(sut(SomeRecord.class)).isInstanceOf(RecordConstructorInstantiator.class);
    }

    @Test
    void returnReflectionConstructorInstantiator_forClassWhenFinalDoesNotMeanFinal() {
        assertThat(sut(SomeClassWithFinalField.class)).isInstanceOf(ReflectionInstantiator.class);
    }

    @Test
    void returnReflectionConstructorInstantiator_forClassWhenItHasNoFinalFields() {
        assertThat(sut(SomeClassWithoutFinalField.class)).isInstanceOf(ReflectionInstantiator.class);
    }

    @Test
    void returnReflectionConstructorInstantiator_forClassWhenItHasNoFinalFields_evenWhenFinalMeansFinal() {
        assertThat(sut(SomeClassWithoutFinalField.class, null, true)).isInstanceOf(ReflectionInstantiator.class);
    }

    @Test
    void returnClassConstructorInstantiator_whenFinalMeansFinal() {
        assertThat(sut(ConstructorMatchesFields.class, null, true)).isInstanceOf(ClassConstructorInstantiator.class);
    }

    @Test
    void returnsReflectionInstantiator_ifAllElseFails() {
        assertThatThrownBy(() -> sut(SomeClassWithFinalField.class, null, true))
                .isInstanceOf(InstantiatorException.class)
                .hasMessageContaining("Use #withFactory");
    }

    private <T> Instantiator<T> sut(Class<T> type) {
        return sut(type, null, false);
    }

    private <T> Instantiator<T> sut(Class<T> type, InstanceFactory<T> factory, boolean finalMeansFinal) {
        return InstantiatorFactory.of(ClassProbe.of(type), factory, objenesis, finalMeansFinal);
    }

    record SomeRecord(int i) {}

    @SuppressWarnings("unused")
    static class SomeClassWithFinalField {
        private final int i = 10;
    }

    @SuppressWarnings("unused")
    static class SomeClassWithoutFinalField {
        private int i = 10;
    }

    @SuppressWarnings("unused")
    static final class ConstructorMatchesFields {
        private final int i;
        private final String s;

        public ConstructorMatchesFields(int i, String s) {
            this.i = i;
            this.s = s;
        }
    }
}
