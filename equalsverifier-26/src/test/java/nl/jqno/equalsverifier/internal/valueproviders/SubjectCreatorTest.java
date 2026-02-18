package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.InstantiatorException;
import nl.jqno.equalsverifier.internal.instantiators.InstantiatorFactory;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.Configuration;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class SubjectCreatorTest {

    private static final boolean NO_NEED_TO_FORCE = false;
    private static final String METHOD_NAME = "foo";

    private final ValueProvider valueProvider = new SubjectCreatorTestValueProvider();
    private final Objenesis objenesis = new ObjenesisStd();
    private SubjectCreator<NonConstructable> sut =
            createSut(NonConstructable.class, values -> new NonConstructable("" + values.getInt("i")));

    @Test
    void constructSubjectCreator_withoutFactory() {
        assertThatThrownBy(() -> createSut(NonConstructable.class, null))
                .isInstanceOf(InstantiatorException.class)
                .hasMessageContaining("Cannot instantiate NonConstructable.");
    }

    @Test
    void copyIntoSubclass_withoutFactory() {
        var obj = new NonConstructable("42");
        assertThatThrownBy(() -> sut.copyIntoSubclass(obj, NonConstructableSub.class, null, METHOD_NAME))
                .isInstanceOf(InstantiatorException.class)
                .hasMessageContaining(
                    "Cannot instantiate a subclass of NonConstructable (attempted subclass: NonConstructableSub).");
    }

    @Test
    void copyIntoSubclass_withFactory() {
        var obj = new NonConstructable("42");
        var actual = sut
                .copyIntoSubclass(
                    obj,
                    NonConstructableSub.class,
                    values -> new NonConstructableSub("" + values.getInt("i")),
                    METHOD_NAME);
        assertThat(actual).isEqualTo(new NonConstructableSub("42"));
    }

    @Test
    void copyIntoSuperclass_withoutFactory() {
        var subSut = createSut(NonConstructableSub.class, values -> new NonConstructableSub("" + values.getInt("i")));

        var obj = new NonConstructableSub("42");
        assertThatThrownBy(() -> subSut.copyIntoSuperclass(obj, null))
                .isInstanceOf(InstantiatorException.class)
                .hasMessageContaining(
                    "Cannot instantiate the superclass of NonConstructableSub (attempted superclass: NonConstructable).");
    }

    @Test
    void copyIntoSuperclass_withFactory() {
        var obj = new NonConstructableSub("42");
        var actual = sut.copyIntoSuperclass(obj, values -> new NonConstructable("" + values.getInt("i")));
        assertThat(actual).isEqualTo(new NonConstructable("42")).isInstanceOf(NonConstructable.class);
    }

    static class NonConstructable {
        private final int i;

        public NonConstructable(String i) {
            this.i = Integer.valueOf(i);
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof NonConstructable other && i == other.i;
        }

        @Override
        public final int hashCode() {
            return i;
        }
    }

    static final class NonConstructableSub extends NonConstructable {
        public NonConstructableSub(String i) {
            super(i);
        }
    }

    static class SubjectCreatorTestValueProvider implements ValueProvider {

        @Override
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            return Optional.empty();
        }
    }

    private <T> SubjectCreator<T> createSut(Class<T> type, InstanceFactory<T> factory) {
        var config = buildConfiguration(type, factory);
        var actualType =
                SubtypeManager.findInstantiableSubclass(ClassProbe.of(type), valueProvider, Attributes.empty());
        var instantiator = InstantiatorFactory.of(ClassProbe.of(actualType), factory, objenesis, NO_NEED_TO_FORCE);
        return new SubjectCreator<>(actualType, config, valueProvider, objenesis, instantiator, NO_NEED_TO_FORCE);
    }

    private <T> Configuration<T> buildConfiguration(Class<T> type, InstanceFactory<T> factory) {
        return Configuration
                .build(
                    type,
                    factory,
                    null,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptySet(),
                    null,
                    false,
                    null,
                    null,
                    null,
                    false,
                    EnumSet.noneOf(Warning.class),
                    Collections.emptySet(),
                    null,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptyList(),
                    Collections.emptyList());
    }
}
