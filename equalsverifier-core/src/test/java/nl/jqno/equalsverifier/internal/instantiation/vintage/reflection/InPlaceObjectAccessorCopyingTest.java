package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.StaticFinalContainer;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class InPlaceObjectAccessorCopyingTest {

    private Objenesis objenesis = new ObjenesisStd();

    @Test
    void copyHappyPath() {
        Point original = new Point(2, 3);
        Point copy = copyOf(original);

        assertAllFieldsEqual(original, copy, Point.class);
    }

    @Test
    void shallowCopy() {
        PointContainer original = new PointContainer(new Point(1, 2));
        PointContainer copy = copyOf(original);

        assertThat(original).isNotSameAs(copy);
        assertThat(original.getPoint() == copy.getPoint()).isTrue();
    }

    @Test
    void copyStaticFinal() {
        StaticFinalContainer foo = new StaticFinalContainer();
        copyOf(foo);
    }

    @Test
    void inheritanceCopy() {
        Point3D original = new Point3D(2, 3, 4);
        Point3D copy = copyOf(original);

        assertAllFieldsEqual(original, copy, Point.class);
        assertAllFieldsEqual(original, copy, Point3D.class);
    }

    @Test
    void copyFromSub() {
        Point3D original = new Point3D(2, 3, 4);
        Point copy = copyOf(original, Point.class);

        assertThat(copy.getClass()).isEqualTo(Point.class);
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
        assertThat(original).isNotSameAs(copy);
        for (FieldProbe probe : FieldIterable.of(type)) {
            try {
                assertThat(probe.getValue(copy)).isEqualTo(probe.getValue(original));
            }
            catch (ReflectionException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
