package nl.jqno.equalsverifier.internal.reflection.vintage.mutation;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.lang.reflect.Constructor;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.CachedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

public class RecordObjectAccessorScramblingTest {

    private static final Attributes EMPTY_ATTRIBUTES = Attributes.unlabeled();
    private CachedValueProvider cache;
    private FactoryCache factoryCache;
    private VintageValueProvider valueProvider;

    @BeforeEach
    public void setup() throws Exception {
        cache = new CachedValueProvider();
        factoryCache = JavaApiPrefabValues.build();
        valueProvider =
            new VintageValueProvider(
                TestValueProviders.empty(),
                cache,
                factoryCache,
                new ObjenesisStd()
            );
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
        return create(object).scramble(valueProvider, TypeTag.NULL, EMPTY_ATTRIBUTES);
    }

    record Point(int x, int y) {}

    record TypeContainerRecord(int i, boolean b, String s, Object o) {}

    private static final String ORIGINAL_VALUE = "original";

    record StaticFieldContainer(int nonstatic) {
        public static final String STATIC_FINAL = ORIGINAL_VALUE;
        public static String staticNonfinal = ORIGINAL_VALUE;
    }
}
