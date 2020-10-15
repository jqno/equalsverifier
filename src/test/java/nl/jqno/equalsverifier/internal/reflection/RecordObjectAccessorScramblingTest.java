package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import org.junit.Before;
import org.junit.Test;

public class RecordObjectAccessorScramblingTest extends StringCompilerTestBase {
    private FactoryCache factoryCache;
    private PrefabValues prefabValues;

    @Before
    public void setup() throws Exception {
        assumeTrue(isRecordsAvailable());
        factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void scramble() throws Exception {
        Class<?> type = compile(POINT_RECORD_CLASS_NAME, POINT_RECORD_CLASS);
        Constructor<?> constructor = type.getDeclaredConstructor(int.class, int.class);
        factoryCache.put(
                type,
                values(
                        constructor.newInstance(1, 2),
                        constructor.newInstance(2, 3),
                        constructor.newInstance(1, 2)));
        Object original = constructor.newInstance(1, 2);

        Object scrambled = doScramble(original);
        assertFalse(original.equals(scrambled));
    }

    @Test
    public void scrambleAllFields() throws Exception {
        Class<?> type = compile(TYPE_CONTAINER_RECORD_CLASS_NAME, TYPE_CONTAINER_RECORD_CLASS);
        Constructor<?> constructor =
                type.getDeclaredConstructor(int.class, boolean.class, String.class, Object.class);
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
        Class<?> type = compile(POINT_RECORD_CLASS_NAME, POINT_RECORD_CLASS);
        Constructor<?> constructor = type.getDeclaredConstructor(int.class, int.class);
        Object original = constructor.newInstance(1, 2);

        expectException(
                EqualsVerifierInternalBugException.class,
                "Record:",
                "can't shallow-scramble a record.");
        create(original).shallowScramble(prefabValues, TypeTag.NULL);
    }

    @Test
    public void dontScrambleStaticFinal() throws NoSuchFieldException {
        Class<?> type = compile(STATIC_FIELD_RECORD_CLASS_NAME, STATIC_FIELD_RECORD_CLASS);
        Object instance = Instantiator.of(type).instantiate();

        ObjectAccessor<?> scrambled = doScramble(instance);

        Object scrambledStaticFinal = fieldValue(scrambled, "STATIC_FINAL");
        Object scrambledStaticNonfinal = fieldValue(scrambled, "staticNonfinal");
        assertEquals(ORIGINAL_VALUE, scrambledStaticFinal);
        assertEquals(ORIGINAL_VALUE, scrambledStaticNonfinal);
    }

    @Test
    public void scrambleNestedGenerics() throws Exception {
        Class<?> type =
                compile(
                        NESTED_GENERIC_CONTAINER_RECORD_CLASS_NAME,
                        NESTED_GENERIC_CONTAINER_RECORD_CLASS);
        Constructor<?> constructor =
                type.getDeclaredConstructor(GenericContainer.class, GenericContainer.class);
        Object instance =
                constructor.newInstance(
                        new GenericContainer<String>(new ArrayList<String>()),
                        new GenericContainer<Point3D>(new ArrayList<Point3D>()));
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

    private static final String POINT_RECORD_CLASS_NAME = "Point";
    private static final String POINT_RECORD_CLASS = "public record Point(int x, int y) {}";

    private static final String TYPE_CONTAINER_RECORD_CLASS_NAME = "TypeContainerRecord";
    private static final String TYPE_CONTAINER_RECORD_CLASS =
            "public record TypeContainerRecord(int i, boolean b, String s, Object o) {}";

    private static final String ORIGINAL_VALUE = "original";
    private static final String STATIC_FIELD_RECORD_CLASS_NAME = "StaticFieldContainer";
    private static final String STATIC_FIELD_RECORD_CLASS =
            "public record StaticFieldContainer(int nonstatic) {"
                    + ("\n    public static final String STATIC_FINAL = \""
                            + ORIGINAL_VALUE
                            + "\";")
                    + ("\n    public static String staticNonfinal = \"" + ORIGINAL_VALUE + "\";")
                    + "\n}";

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

    private static final String NESTED_GENERIC_CONTAINER_RECORD_CLASS_NAME =
            "GenericContainerContainer";
    private static final String NESTED_GENERIC_CONTAINER_RECORD_CLASS =
            "import nl.jqno.equalsverifier.internal.reflection.RecordObjectAccessorScramblingTest.GenericContainer;"
                    + "\nimport nl.jqno.equalsverifier.testhelpers.types.Point3D;"
                    + "\npublic record GenericContainerContainer(GenericContainer<String> strings, GenericContainer<Point3D> points) {}";
}
