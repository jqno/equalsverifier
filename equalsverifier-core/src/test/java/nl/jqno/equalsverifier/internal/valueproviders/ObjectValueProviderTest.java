package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.AbstractClass;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.StaticContainer;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

public class ObjectValueProviderTest {

    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");

    private ObjectValueProvider sut =
            new ObjectValueProvider(new BuiltinPrefabValueProvider(), new ObjenesisStd(), false);

    @Test
    void instantiateSimpleObject() {
        var actual = provide(StringContainer.class);
        assertThat(actual.red()).isEqualTo(new StringContainer("one"));
        assertThat(actual.blue()).isEqualTo(new StringContainer("two"));
    }

    @Test
    void doesNotInstantiateAbstractClass() {
        assertThatThrownBy(() -> provide(AbstractClass.class))
                .isInstanceOf(ReflectionException.class)
                .hasMessage("Cannot instantiate abstract class " + AbstractClass.class.getName());
    }

    @Test
    void redCopyFieldsAreSameAsRedFields() {
        var actual = provide(StringContainer.class);
        assertThat(actual.redCopy()).isEqualTo(actual.red());
        assertThat(actual.redCopy().s).isSameAs(actual.red().s);
    }

    @Test
    void ignoresStaticFields() {
        var expected = StaticContainer.field;
        provide(StaticContainer.class);
        assertThat(StaticContainer.field).isEqualTo(expected);
    }

    private <T> Tuple<T> provide(Class<T> type) {
        return sut.provideOrThrow(new TypeTag(type), SOME_ATTRIBUTES);
    }

    static class StringContainer {
        private final String s;

        public StringContainer(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StringContainer other && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
    }

    static sealed abstract class Sealed permits Child {
        int value;
    }

    static final class Child extends Sealed {}
}
