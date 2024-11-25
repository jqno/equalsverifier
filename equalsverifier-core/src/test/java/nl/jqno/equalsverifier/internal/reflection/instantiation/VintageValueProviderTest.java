package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.ThrowingInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class VintageValueProviderTest {

    private static final Attributes EMPTY_ATTRIBUTES = Attributes.unlabeled();
    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag INT_TAG = new TypeTag(int.class);

    private Objenesis objenesis = new ObjenesisStd();
    private CachedValueProvider cache = new CachedValueProvider();
    private FactoryCache factoryCache = new FactoryCache();
    private VintageValueProvider vp;

    @BeforeEach
    public void setUp() {
        factoryCache.put(String.class, values("r", "b", new String("r")));
        factoryCache.put(int.class, values(42, 1337, 42));
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
    }

    @Test
    public void provide() {
        Tuple<Point> actual = vp.provideOrThrow(POINT_TAG, Attributes.unlabeled());
        assertEquals(Tuple.of(new Point(42, 42), new Point(1337, 1337), new Point(42, 42)), actual);
    }

    @Test
    public void provideFromFactory() {
        assertEquals(Tuple.of("r", "b", "r"), vp.provideOrThrow(STRING_TAG, EMPTY_ATTRIBUTES));
    }

    @Test
    public void giveRedFromFallbackFactory() {
        Tuple<Point> actual = vp.provideOrThrow(POINT_TAG, EMPTY_ATTRIBUTES);
        assertEquals(Tuple.of(new Point(42, 42), new Point(1337, 1337), new Point(42, 42)), actual);
        assertNotSame(actual.getRed(), actual.getRedCopy());
    }

    @Test
    public void fallbackDoesNotAffectStaticFields() {
        int expected = StaticContainer.staticInt;
        vp.provideOrThrow(new TypeTag(StaticContainer.class), EMPTY_ATTRIBUTES);
        assertEquals(expected, StaticContainer.staticInt);
    }

    @Test
    public void stringListIsSeparateFromIntegerList() {
        factoryCache.put(List.class, new ListTestFactory());
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);

        List<String> strings = vp
            .<List<String>>provideOrThrow(new TypeTag(List.class, STRING_TAG), EMPTY_ATTRIBUTES)
            .getRed();
        List<Integer> ints = vp
            .<List<Integer>>provideOrThrow(new TypeTag(List.class, INT_TAG), EMPTY_ATTRIBUTES)
            .getRed();

        assertEquals("r", strings.get(0));
        assertEquals(42, (int) ints.get(0));
    }

    @Test
    public void addingNullDoesntBreakAnything() {
        factoryCache.put((Class<?>) null, new ListTestFactory());
    }

    @Test
    public void addingATypeTwiceOverrulesTheExistingOne() {
        factoryCache.put(int.class, values(-1, -2, -1));
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        assertEquals(-1, (int) vp.provideOrThrow(INT_TAG, EMPTY_ATTRIBUTES).getRed());
        assertEquals(-2, (int) vp.provideOrThrow(INT_TAG, EMPTY_ATTRIBUTES).getBlue());
    }

    @Test
    public void addLazyFactoryWorks() {
        TypeTag lazyTag = new TypeTag(Lazy.class);
        factoryCache.put(Lazy.class.getName(), values(Lazy.X, Lazy.Y, Lazy.X));
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        Tuple<?> tuple = vp.provideOrThrow(lazyTag, EMPTY_ATTRIBUTES);
        assertEquals(Lazy.X, tuple.getRed());
        assertEquals(Lazy.Y, tuple.getBlue());
        assertEquals(Lazy.X, tuple.getRedCopy());
    }

    @Test
    public void addLazyFactoryIsLazy() {
        TypeTag throwingInitializerTag = new TypeTag(ThrowingInitializer.class);

        // Shouldn't throw, because constructing PrefabValues doesn't instantiate objects:
        factoryCache.put(
            ThrowingInitializer.class.getName(),
            (t, p, ts) ->
                Tuple.of(ThrowingInitializer.X, ThrowingInitializer.Y, ThrowingInitializer.X)
        );
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);

        // Should throw, because `provideOrThrow` does instantiate objects:
        try {
            vp.provideOrThrow(throwingInitializerTag, EMPTY_ATTRIBUTES);
            fail("Expected an exception");
        } catch (Error e) {
            // succeed
        }
    }

    public static class NpeThrowing {

        private final int i;

        public NpeThrowing(int i) {
            this.i = i;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                throw new NullPointerException();
            }
            if (!(obj instanceof NpeThrowing)) {
                return false;
            }
            return i == ((NpeThrowing) obj).i;
        }

        public int hashCode() {
            return i;
        }
    }

    @SuppressWarnings("rawtypes")
    private static final class ListTestFactory implements PrefabValueFactory<List> {

        @Override
        @SuppressWarnings("unchecked")
        public Tuple<List> createValues(
            TypeTag tag,
            ValueProvider valueProvider,
            Attributes attributes
        ) {
            TypeTag subtag = tag.getGenericTypes().get(0);

            Tuple<List> tuple = valueProvider
                .provideOrThrow(subtag, Attributes.unlabeled())
                .map(val -> {
                    List list = new ArrayList<>();
                    list.add(val);
                    return list;
                });

            return new Tuple<>(tuple.getRed(), tuple.getBlue(), tuple.getRedCopy());
        }
    }

    private static final class StaticContainer {

        static int staticInt = 2;

        @SuppressWarnings("unused")
        int regularInt = 3;
    }

    public static class Lazy {

        public static final Lazy X = new Lazy(1);
        public static final Lazy Y = new Lazy(2);

        private final int i;

        public Lazy(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }

        @Override
        public String toString() {
            return "Lazy: " + i;
        }
    }
}
