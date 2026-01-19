package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class AbstractValueProviderTest {

    private final Attributes attributes = Attributes.empty();
    private final ValueProvider vp = new AbstractValueProviderTestValueProvider();
    private AbstractValueProvider sut = new AbstractValueProvider(vp);

    @Test
    void provideConcreteClass() {
        var actual = sut.provide(new TypeTag(Concrete.class), attributes);
        assertThat(actual).isEmpty();
    }

    @Test
    void provideAbstractClass() {
        var actual = sut.provide(new TypeTag(Abstract.class), attributes);
        assertThat(actual.get().red()).isInstanceOf(Abstract.class);
    }

    @Test
    void noInstantiableSubtypeExists() {
        assertThatThrownBy(() -> sut.provide(new TypeTag(SealedInterface.class), attributes))
                .isInstanceOf(NoValueException.class);
    }

    @Test
    void noValueExists() {
        var actual = sut.provide(new TypeTag(AbstractNoValue.class), attributes);
        assertThat(actual).isEmpty();
    }

    static class Concrete {}

    static abstract class Abstract {}

    static abstract class AbstractNoValue {}

    sealed interface SealedInterface {}

    record SealedSub(SealedInterface i) implements SealedInterface {}

    private static final class AbstractValueProviderTestValueProvider implements ValueProvider {
        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
            if (Concrete.class.isAssignableFrom(tag.getType())) {
                return Optional.of((Tuple<T>) new Tuple<>(new Concrete() {}, new Concrete() {}, new Concrete() {}));
            }
            if (Abstract.class.isAssignableFrom(tag.getType())) {
                return Optional.of((Tuple<T>) new Tuple<>(new Abstract() {}, new Abstract() {}, new Abstract() {}));
            }
            if (SealedSub.class.equals(tag.getType())) {
                throw new NoValueException("no value!");
            }
            return Optional.empty();
        }
    }
}
