package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.StaticFinalContainer;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InPlaceObjectAccessorCopyingTest {

    private Objenesis objenesis = new ObjenesisStd();

    @Test
    public void copyHappyPath() {
        Point original = new Point(2, 3);
        Point copy = copyOf(original);

        assertAllFieldsEqual(original, copy, Point.class);
    }

    @Test
    public void shallowCopy() {
        PointContainer original = new PointContainer(new Point(1, 2));
        PointContainer copy = copyOf(original);

        assertNotSame(original, copy);
        assertTrue(original.getPoint() == copy.getPoint());
    }

    @Test
    public void copyStaticFinal() {
        StaticFinalContainer foo = new StaticFinalContainer();
        copyOf(foo);
    }

    @Test
    public void inheritanceCopy() {
        Point3D original = new Point3D(2, 3, 4);
        Point3D copy = copyOf(original);

        assertAllFieldsEqual(original, copy, Point.class);
        assertAllFieldsEqual(original, copy, Point3D.class);
    }

    @Test
    public void copyFromSub() {
        Point3D original = new Point3D(2, 3, 4);
        Point copy = copyOf(original, Point.class);

        assertEquals(Point.class, copy.getClass());
        assertAllFieldsEqual(original, copy, Point.class);
    }

    @SuppressWarnings("unchecked")
    private <T> InPlaceObjectAccessor<T> create(T object) {
        return new InPlaceObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private <T> T copyOf(T from) {
        return create(from).copy(objenesis);
    }

    private <T> T copyOf(T from, Class<T> type) {
        return new InPlaceObjectAccessor<T>(from, type).copy(objenesis);
    }

    private static <T> void assertAllFieldsEqual(T original, T copy, Class<? extends T> type) {
        assertNotSame(original, copy);
        for (Field field : FieldIterable.of(type)) {
            try {
                assertEquals(field.get(original), field.get(copy));
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
