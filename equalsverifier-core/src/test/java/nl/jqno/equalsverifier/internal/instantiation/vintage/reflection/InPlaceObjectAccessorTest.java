package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class InPlaceObjectAccessorTest {

    @Test
    void of() {
        Point p = new Point(1, 2);
        ObjectAccessor<Point> actual = ObjectAccessor.of(p);
        assertThat(actual instanceof InPlaceObjectAccessor).isTrue();
    }

    @Test
    void get() {
        Object foo = new Object();
        InPlaceObjectAccessor<Object> accessor = create(foo);
        assertThat(accessor.get()).isSameAs(foo);
    }

    @SuppressWarnings("unchecked")
    private <T> InPlaceObjectAccessor<T> create(T object) {
        return new InPlaceObjectAccessor<T>(object, (Class<T>) object.getClass());
    }
}
