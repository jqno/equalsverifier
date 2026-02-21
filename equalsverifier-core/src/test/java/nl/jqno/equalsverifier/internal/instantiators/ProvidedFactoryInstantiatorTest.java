package nl.jqno.equalsverifier.internal.instantiators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

public class ProvidedFactoryInstantiatorTest {
    @Test
    void happyPath() throws Exception {
        var x = Point.class.getDeclaredField("x");
        var y = Point.class.getDeclaredField("y");
        var values = Map.<Field, Object>of(x, 42, y, 1337);
        InstanceFactory<Point> factory = v -> new Point(v.getInt("x"), v.getInt("y"));

        var sut = new ProvidedFactoryInstantiator<>(factory, false);
        var actual = sut.instantiate(values);

        assertThat(actual.x).isEqualTo(42);
        assertThat(actual.y).isEqualTo(1337);
    }

    @Test
    void returnNullForUnknownField() {
        InstanceFactory<Point> factory = v -> new Point(v.getInt("x"), v.getInt("y"));

        var sut = new ProvidedFactoryInstantiator<>(factory, false);
        var actual = sut.instantiate(Map.of());

        assertThat(actual.x).isEqualTo(0);
        assertThat(actual.y).isEqualTo(0);
    }

    @Test
    void throwExceptionForUnknownField() {
        InstanceFactory<Point> factory = v -> new Point(v.getInt("x"), v.getInt("y"));

        var sut = new ProvidedFactoryInstantiator<>(factory, true);

        assertThatThrownBy(() -> sut.instantiate(Map.of()))
                .isInstanceOf(ReflectionException.class)
                .hasMessage("Attempted to get non-existing field x.");
    }
}
