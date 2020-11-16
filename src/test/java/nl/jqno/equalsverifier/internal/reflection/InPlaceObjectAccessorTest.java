package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;

public class InPlaceObjectAccessorTest {
    @Test
    public void of() {
        Point p = new Point(1, 2);
        ObjectAccessor<Point> actual = ObjectAccessor.of(p);
        assertTrue(actual instanceof InPlaceObjectAccessor);
    }

    @Test
    public void get() {
        Object foo = new Object();
        InPlaceObjectAccessor<Object> accessor = create(foo);
        assertSame(foo, accessor.get());
    }

    @Test
    public void getField() throws Exception {
        Point p = new Point(1, 2);
        Field f = Point.class.getDeclaredField("y");
        InPlaceObjectAccessor<Point> accessor = create(p);
        assertEquals(2, accessor.getField(f));
    }

    @SuppressWarnings("unchecked")
    private <T> InPlaceObjectAccessor<T> create(T object) {
        return new InPlaceObjectAccessor<T>(object, (Class<T>) object.getClass());
    }
}
