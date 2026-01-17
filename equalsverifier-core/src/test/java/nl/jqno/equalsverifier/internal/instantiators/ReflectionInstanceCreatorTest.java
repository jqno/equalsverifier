package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.ArrayContainer;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class ReflectionInstanceCreatorTest {

    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void instantiateClass() throws NoSuchFieldException {
        Field x = SomeClass.class.getDeclaredField("x");
        Field z = SomeClass.class.getDeclaredField("z");
        var values = Map.<Field, Object>of(x, 42, z, "42");

        SomeClass actual = instantiate(SomeClass.class, values);

        assertThat(actual.x).isEqualTo(42);
        assertThat(actual.y).isEqualTo(0);
        assertThat(actual.z).isEqualTo("42");
    }

    @Test
    void fieldsOfInstantiatedObjectHaveDefaultValues() {
        SomeClass actual = instantiate(SomeClass.class, Map.of());

        assertThat(actual.x).isEqualTo(0);
        assertThat(actual.z).isNull();
    }

    @Test
    void instantiateFinalClass() {
        FinalPoint actual = instantiate(FinalPoint.class, Map.of());

        assertThat(actual).isNotNull();
    }

    @Test
    void instantiateRecord() {
        SimpleRecord actual = instantiate(SimpleRecord.class, Map.of());

        assertThat(actual.getClass()).isEqualTo(SimpleRecord.class);
    }

    @Test
    void instantiateArrayContainer() {
        ArrayContainer actual = instantiate(ArrayContainer.class, Map.of());

        assertThat(actual).isNotNull();
    }

    @Test
    void instantiateANonToplevelClass() {
        class Something {}

        Something s = instantiate(Something.class, Map.of());

        assertThat(s.getClass()).isEqualTo(Something.class);
    }

    @Test
    void failsGracefully_abstract() {
        var probe = ClassProbe.of(SomeAbstractClass.class);
        var sut = InstanceCreator.of(probe, objenesis);

        assertThatThrownBy(() -> sut.instantiate(Map.of()))
                .isInstanceOf(ReflectionException.class)
                .hasMessage("Cannot instantiate abstract class " + SomeAbstractClass.class.getName());
    }

    private <T> T instantiate(Class<T> type, Map<Field, Object> values) {
        var probe = ClassProbe.of(type);
        var sut = InstanceCreator.of(probe, objenesis);
        return sut.instantiate(values);
    }

    @SuppressWarnings("unused")
    private static final class SomeClass {

        final int x;
        final int y;
        final String z;

        private SomeClass(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static abstract class SomeAbstractClass {}

    record SimpleRecord(int i) {}
}
