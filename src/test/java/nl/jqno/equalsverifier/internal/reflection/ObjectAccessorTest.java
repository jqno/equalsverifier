package nl.jqno.equalsverifier.internal.reflection;

import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ObjectAccessorTest {
    @Test
    public void get() {
        Object foo = new Object();
        ObjectAccessor<Object> accessor = ObjectAccessor.of(foo);
        assertSame(foo, accessor.get());
    }

    @Test
    public void fieldAccessorFor() throws NoSuchFieldException {
        PointContainer foo = new PointContainer(new Point(1, 2));
        Field field = PointContainer.class.getDeclaredField("point");

        ObjectAccessor<PointContainer> accessor = ObjectAccessor.of(foo);
        FieldAccessor fieldAccessor = accessor.fieldAccessorFor(field);

        fieldAccessor.defaultField();
        assertNull(foo.getPoint());
    }
}
