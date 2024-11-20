package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
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

    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag INT_TAG = new TypeTag(int.class);

    private Objenesis objenesis = new ObjenesisStd();
    private CachedValueProvider cache = new CachedValueProvider();
    private FactoryCache factoryCache = new FactoryCache();
    private VintageValueProvider vp;

    @BeforeEach
    public void setUp() {
        factoryCache.put(String.class, new AppendingStringTestFactory());
        factoryCache.put(int.class, values(42, 1337, 42));
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
    }

    @Test
    public void sanityTestFactoryIncreasesStringLength() {
        AppendingStringTestFactory f = new AppendingStringTestFactory();
        assertEquals("r", f.createValues(null, null, null).getRed());
        assertEquals("rr", f.createValues(null, null, null).getRed());
        assertEquals("rrr", f.createValues(null, null, null).getRed());
    }

    @Test
    public void provide() {
        Tuple<Point> actual = vp.provideOrThrow(POINT_TAG, null);
        assertEquals(Tuple.of(new Point(42, 42), new Point(1337, 1337), new Point(42, 42)), actual);
    }

    @Test
    public void giveRedFromFactory() {
        assertEquals("r", vp.giveRed(STRING_TAG));
    }

    @Test
    public void giveRedFromCache() {
        vp.giveRed(STRING_TAG);
        assertEquals("r", vp.giveRed(STRING_TAG));
    }

    @Test
    public void giveBlueFromFactory() {
        assertEquals("b", vp.giveBlue(STRING_TAG));
    }

    @Test
    public void giveBlueFromCache() {
        vp.giveBlue(STRING_TAG);
        assertEquals("b", vp.giveBlue(STRING_TAG));
    }

    @Test
    public void giveRedCopyFromFactory() {
        assertEquals("r", vp.giveRedCopy(STRING_TAG));
        assertNotSame(vp.giveRed(STRING_TAG), vp.giveRedCopy(STRING_TAG));
    }

    @Test
    public void giveRedCopyFromCache() {
        vp.giveRedCopy(STRING_TAG);
        assertEquals("r", vp.giveRedCopy(STRING_TAG));
        assertNotSame(vp.giveRed(STRING_TAG), vp.giveRedCopy(STRING_TAG));
    }

    @Test
    public void giveRedFromFallbackFactory() {
        Point actual = vp.giveRed(POINT_TAG);
        assertEquals(new Point(42, 42), actual);
    }

    @Test
    public void giveBlueFromFallbackFactory() {
        Point actual = vp.giveBlue(POINT_TAG);
        assertEquals(new Point(1337, 1337), actual);
    }

    @Test
    public void giveRedCopyFromFallbackFactory() {
        Point actual = vp.giveRedCopy(POINT_TAG);
        assertEquals(new Point(42, 42), actual);
        assertNotSame(vp.giveRed(POINT_TAG), actual);
    }

    @Test
    public void fallbackDoesNotAffectStaticFields() {
        int expected = StaticContainer.staticInt;
        vp.giveRed(new TypeTag(StaticContainer.class));
        assertEquals(expected, StaticContainer.staticInt);
    }

    @Test
    public void stringListIsSeparateFromIntegerList() {
        factoryCache.put(List.class, new ListTestFactory());
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);

        List<String> strings = vp.giveRed(new TypeTag(List.class, STRING_TAG));
        List<Integer> ints = vp.giveRed(new TypeTag(List.class, INT_TAG));

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
        assertEquals(-1, (int) vp.giveRed(INT_TAG));
        assertEquals(-2, (int) vp.giveBlue(INT_TAG));
    }

    @Test
    public void addLazyFactoryWorks() {
        TypeTag lazyTag = new TypeTag(Lazy.class);
        factoryCache.put(Lazy.class.getName(), values(Lazy.X, Lazy.Y, Lazy.X));
        vp = new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        assertEquals(Lazy.X, vp.giveRed(lazyTag));
        assertEquals(Lazy.Y, vp.giveBlue(lazyTag));
        assertEquals(Lazy.X, vp.giveRedCopy(lazyTag));
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

        // Should throw, because `giveRed` does instantiate objects:
        try {
            vp.giveRed(throwingInitializerTag);
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

    private static class AppendingStringTestFactory implements PrefabValueFactory<String> {

        private String red;
        private String blue;

        public AppendingStringTestFactory() {
            red = "";
            blue = "";
        }

        @Override
        public Tuple<String> createValues(
            TypeTag tag,
            VintageValueProvider valueProvider,
            LinkedHashSet<TypeTag> typeStack
        ) {
            red += "r";
            blue += "b";
            return new Tuple<>(red, blue, new String(red));
        }
    }

    @SuppressWarnings("rawtypes")
    private static final class ListTestFactory implements PrefabValueFactory<List> {

        @Override
        @SuppressWarnings("unchecked")
        public Tuple<List> createValues(
            TypeTag tag,
            VintageValueProvider valueProvider,
            LinkedHashSet<TypeTag> typeStack
        ) {
            TypeTag subtag = tag.getGenericTypes().get(0);

            List red = new ArrayList<>();
            red.add(valueProvider.giveRed(subtag));

            List blue = new ArrayList<>();
            blue.add(valueProvider.giveBlue(subtag));

            List redCopy = new ArrayList<>();
            redCopy.add(valueProvider.giveRed(subtag));

            return new Tuple<>(red, blue, redCopy);
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
