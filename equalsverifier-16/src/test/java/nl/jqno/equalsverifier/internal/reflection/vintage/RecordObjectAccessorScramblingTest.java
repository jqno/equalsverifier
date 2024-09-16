package nl.jqno.equalsverifier.internal.reflection.vintage;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecordObjectAccessorScramblingTest {

    private static final LinkedHashSet<TypeTag> EMPTY_TYPE_STACK = new LinkedHashSet<>();
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

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private ObjectAccessor<Object> doScramble(Object object) {
        return create(object).scramble(prefabValues, TypeTag.NULL, EMPTY_TYPE_STACK);
    }

    record Point(int x, int y) {}

    record TypeContainerRecord(int i, boolean b, String s, Object o) {}

    private static final String ORIGINAL_VALUE = "original";

    record StaticFieldContainer(int nonstatic) {
        public static final String STATIC_FINAL = ORIGINAL_VALUE;
        public static String staticNonfinal = ORIGINAL_VALUE;
    }
}
