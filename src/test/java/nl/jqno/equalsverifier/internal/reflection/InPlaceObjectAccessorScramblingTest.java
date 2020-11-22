package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.StaticFinalContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InPlaceObjectAccessorScramblingTest {
    private PrefabValues prefabValues;

    @BeforeEach
    public void setup() {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        factoryCache.put(Point.class, values(new Point(1, 2), new Point(2, 3), new Point(1, 2)));
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void scrambleReturnsThis() {
        Point original = new Point(2, 3);
        Point copy = copy(original);

        ObjectAccessor<Object> actual = doScramble(copy);
        assertSame(copy, actual.get());
    }

    @Test
    public void scramble() {
        Point original = new Point(2, 3);
        Point copy = copy(original);

        assertTrue(original.equals(copy));
        doScramble(copy);
        assertFalse(original.equals(copy));
    }

    @Test
    public void deepScramble() {
        Point3D modified = new Point3D(2, 3, 4);
        Point3D reference = copy(modified);

        doScramble(modified);

        assertFalse(modified.equals(reference));
        modified.z = 4;
        assertFalse(modified.equals(reference));
    }

    @Test
    public void shallowScramble() {
        Point3D modified = new Point3D(2, 3, 4);
        Point3D reference = copy(modified);

        create(modified).shallowScramble(prefabValues, TypeTag.NULL);

        assertFalse(modified.equals(reference));
        modified.z = 4;
        assertTrue(modified.equals(reference));
    }

    @SuppressWarnings("static-access")
    @Test
    public void scrambleStaticFinal() {
        StaticFinalContainer foo = new StaticFinalContainer();
        int originalInt = StaticFinalContainer.CONST;
        Object originalObject = StaticFinalContainer.OBJECT;

        doScramble(foo);

        assertEquals(originalInt, foo.CONST);
        assertEquals(originalObject, foo.OBJECT);
    }

    @Test
    public void scrambleString() {
        StringContainer foo = new StringContainer();
        String before = foo.s;
        doScramble(foo);
        assertFalse(before.equals(foo.s));
    }

    @Test
    public void privateFinalStringCannotBeScrambled() {
        FinalAssignedStringContainer foo = new FinalAssignedStringContainer();
        String before = foo.s;

        doScramble(foo);

        assertEquals(before, foo.s);
    }

    @Test
    public void scramblePrivateFinalPoint() {
        FinalAssignedPointContainer foo = new FinalAssignedPointContainer();
        Point before = foo.p;

        assertTrue(before.equals(foo.p));
        doScramble(foo);
        assertFalse(before.equals(foo.p));
    }

    @Test
    public void scrambleNestedGenerics() {
        GenericContainerContainer foo = new GenericContainerContainer();

        assertTrue(foo.strings.ts.isEmpty());
        assertTrue(foo.points.ts.isEmpty());

        doScramble(foo);

        assertFalse(foo.strings.ts.isEmpty());
        assertEquals(String.class, foo.strings.ts.get(0).getClass());
        assertFalse(foo.points.ts.isEmpty());
        assertEquals(Point.class, foo.points.ts.get(0).getClass());
    }

    @SuppressWarnings("unchecked")
    private <T> InPlaceObjectAccessor<T> create(T object) {
        return new InPlaceObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private <T> T copy(T object) {
        return create(object).copy();
    }

    private ObjectAccessor<Object> doScramble(Object object) {
        return create(object).scramble(prefabValues, TypeTag.NULL);
    }

    static final class StringContainer {
        private String s = "x";
    }

    static final class FinalAssignedStringContainer {
        private final String s = "x";
    }

    static final class FinalAssignedPointContainer {
        private final Point p = new Point(2, 3);
    }

    static final class GenericContainerContainer {
        private final GenericContainer<String> strings =
                new GenericContainer<>(new ArrayList<String>());
        private final GenericContainer<Point> points =
                new GenericContainer<>(new ArrayList<Point>());
    }

    static final class GenericContainer<T> {
        private List<T> ts;

        public GenericContainer(List<T> ts) {
            this.ts = ts;
        }
    }
}
