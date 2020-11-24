package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.RecordObjectAccessorScramblingTest.GenericContainer;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecordObjectAccessorScramblingTest {

    private FactoryCache factoryCache;
    private PrefabValues prefabValues;

    @BeforeEach
    public void setup() throws Exception {
        factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void scrambleLeavesOriginalUnaffected() throws Exception {
        Constructor<?> c = Point.class.getDeclaredConstructor(int.class, int.class);
        Object original = c.newInstance(2, 3);
        Object copy = doScramble(original).get();
        assertNotSame(original, copy);
    }

    @Test
    public void scramble() throws Exception {
        Constructor<?> constructor = Point.class.getDeclaredConstructor(int.class, int.class);
        factoryCache.put(
            Point.class,
            values(
                constructor.newInstance(1, 2),
                constructor.newInstance(2, 3),
                constructor.newInstance(1, 2)
            )
        );
        Object original = constructor.newInstance(1, 2);

        Object scrambled = doScramble(original);
        assertFalse(original.equals(scrambled));
    }

    @Test
    public void scrambleAllFields() throws Exception {
        Constructor<?> constructor =
            TypeContainerRecord.class.getDeclaredConstructor(
                    int.class,
                    boolean.class,
                    String.class,
                    Object.class
                );
        Object someObject = new Object();
        Object original = constructor.newInstance(42, true, "hello", someObject);

        ObjectAccessor<?> scrambled = doScramble(original);

        assertNotEquals(42, fieldValue(scrambled, "i"));
        assertNotEquals(true, fieldValue(scrambled, "b"));
        assertNotEquals("hello", fieldValue(scrambled, "s"));
        assertNotEquals(someObject, fieldValue(scrambled, "o"));
    }

    @Test
    public void shallowScramble() throws Exception {
        Constructor<?> constructor = Point.class.getDeclaredConstructor(int.class, int.class);
        Object original = constructor.newInstance(1, 2);

        ExpectedException
            .when(() -> create(original).shallowScramble(prefabValues, TypeTag.NULL))
            .assertThrows(EqualsVerifierInternalBugException.class)
            .assertMessageContains("Record:", "can't shallow-scramble a record.");
    }

    @Test
    public void dontScrambleStaticFinal() throws NoSuchFieldException {
        Object instance = Instantiator.of(StaticFieldContainer.class).instantiate();

        ObjectAccessor<?> scrambled = doScramble(instance);

        Object scrambledStaticFinal = fieldValue(scrambled, "STATIC_FINAL");
        Object scrambledStaticNonfinal = fieldValue(scrambled, "staticNonfinal");
        assertEquals(ORIGINAL_VALUE, scrambledStaticFinal);
        assertEquals(ORIGINAL_VALUE, scrambledStaticNonfinal);
    }

    @Test
    public void scrambleNestedGenerics() throws Exception {
        Constructor<?> constructor =
            GenericContainerContainer.class.getDeclaredConstructor(
                    GenericContainer.class,
                    GenericContainer.class
                );
        Object instance = constructor.newInstance(
            new GenericContainer<String>(new ArrayList<String>()),
            new GenericContainer<Point3D>(new ArrayList<Point3D>())
        );
        ObjectAccessor<?> accessor = create(instance);

        assertTrue(GenericContainer.<String>cast(fieldValue(accessor, "strings")).ts.isEmpty());
        assertTrue(GenericContainer.<Point3D>cast(fieldValue(accessor, "points")).ts.isEmpty());

        ObjectAccessor<?> scrambled = doScramble(instance);

        List<String> strings = GenericContainer.<String>cast(fieldValue(scrambled, "strings")).ts;
        assertFalse(strings.isEmpty());
        assertEquals(String.class, strings.get(0).getClass());
        List<Point3D> points = GenericContainer.<Point3D>cast(fieldValue(scrambled, "points")).ts;
        assertFalse(points.isEmpty());
        assertEquals(Point3D.class, points.get(0).getClass());
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private Object fieldValue(ObjectAccessor<?> accessor, String fieldName)
        throws NoSuchFieldException {
        Field field = accessor.get().getClass().getDeclaredField(fieldName);
        return accessor.getField(field);
    }

    private ObjectAccessor<Object> doScramble(Object object) {
        return create(object).scramble(prefabValues, TypeTag.NULL);
    }

    record Point(int x, int y) {}

    record TypeContainerRecord(int i, boolean b, String s, Object o) {}

    private static final String ORIGINAL_VALUE = "original";

    record StaticFieldContainer(int nonstatic) {

        public static final String STATIC_FINAL = ORIGINAL_VALUE;
        public static String staticNonfinal = ORIGINAL_VALUE;
    }

    public static final class GenericContainer<T> {

        private List<T> ts;

        public GenericContainer(List<T> ts) {
            this.ts = ts;
        }

        @SuppressWarnings("unchecked")
        public static <T> GenericContainer<T> cast(Object object) {
            return (GenericContainer<T>) object;
        }
    }

    record GenericContainerContainer(
        GenericContainer<String> strings,
        GenericContainer<Point3D> points
    ) {}
}
