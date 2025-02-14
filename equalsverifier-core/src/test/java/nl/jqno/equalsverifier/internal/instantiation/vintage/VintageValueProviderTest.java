package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.values;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.ThrowingInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class VintageValueProviderTest {

    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag INT_TAG = new TypeTag(int.class);

    private final Objenesis objenesis = new ObjenesisStd();
    private final FactoryCache factoryCache = new FactoryCache();
    private VintageValueProvider vp;

    @BeforeEach
    void setUp() {
        factoryCache.put(String.class, new AppendingStringTestFactory());
        factoryCache.put(int.class, values(42, 1337, 42));
        vp = new VintageValueProvider(factoryCache, objenesis);
    }

    @Test
    void sanityTestFactoryIncreasesStringLength() {
        AppendingStringTestFactory f = new AppendingStringTestFactory();
        assertThat(f.createValues(null, null, null).red()).isEqualTo("r");
        assertThat(f.createValues(null, null, null).red()).isEqualTo("rr");
        assertThat(f.createValues(null, null, null).red()).isEqualTo("rrr");
    }

    @Test
    void provide() {
        Optional<Tuple<Point>> actual = vp.provide(POINT_TAG);
        assertThat(actual).contains(new Tuple<>(new Point(42, 42), new Point(1337, 1337), new Point(42, 42)));
    }

    @Test
    void giveRedFromFactory() {
        assertThat(vp.<String>giveRed(STRING_TAG)).isEqualTo("r");
    }

    @Test
    void giveRedFromCache() {
        vp.giveRed(STRING_TAG);
        assertThat(vp.<String>giveRed(STRING_TAG)).isEqualTo("r");
    }

    @Test
    void giveBlueFromFactory() {
        assertThat(vp.<String>giveBlue(STRING_TAG)).isEqualTo("b");
    }

    @Test
    void giveBlueFromCache() {
        vp.giveBlue(STRING_TAG);
        assertThat(vp.<String>giveBlue(STRING_TAG)).isEqualTo("b");
    }

    @Test
    void giveRedCopyFromFactory() {
        assertThat(vp.<String>giveRedCopy(STRING_TAG)).isEqualTo("r").isNotSameAs(vp.giveRed(STRING_TAG));
    }

    @Test
    void giveRedCopyFromCache() {
        vp.giveRedCopy(STRING_TAG);
        assertThat(vp.<String>giveRedCopy(STRING_TAG)).isEqualTo("r").isNotSameAs(vp.giveRed(STRING_TAG));
    }

    @Test
    void giveRedFromFallbackFactory() {
        Point actual = vp.giveRed(POINT_TAG);
        assertThat(actual).isEqualTo(new Point(42, 42));
    }

    @Test
    void giveBlueFromFallbackFactory() {
        Point actual = vp.giveBlue(POINT_TAG);
        assertThat(actual).isEqualTo(new Point(1337, 1337));
    }

    @Test
    void giveRedCopyFromFallbackFactory() {
        assertThat(vp.<Point>giveRedCopy(POINT_TAG)).isEqualTo(new Point(42, 42)).isNotSameAs(vp.giveRed(POINT_TAG));
    }

    @Test
    void fallbackDoesNotAffectStaticFields() {
        int expected = StaticContainer.staticInt;
        vp.giveRed(new TypeTag(StaticContainer.class));
        assertThat(StaticContainer.staticInt).isEqualTo(expected);
    }

    @Test
    void stringListIsSeparateFromIntegerList() {
        factoryCache.put(List.class, new ListTestFactory());
        vp = new VintageValueProvider(factoryCache, objenesis);

        List<String> strings = vp.giveRed(new TypeTag(List.class, STRING_TAG));
        List<Integer> ints = vp.giveRed(new TypeTag(List.class, INT_TAG));

        assertThat(strings.get(0)).isEqualTo("r");
        assertThat((int) ints.get(0)).isEqualTo(42);
    }

    @Test
    void addingNullDoesntBreakAnything() {
        factoryCache.put((Class<?>) null, new ListTestFactory());
    }

    @Test
    void addingATypeTwiceOverrulesTheExistingOne() {
        factoryCache.put(int.class, values(-1, -2, -1));
        vp = new VintageValueProvider(factoryCache, objenesis);
        assertThat((int) vp.giveRed(INT_TAG)).isEqualTo(-1);
        assertThat((int) vp.giveBlue(INT_TAG)).isEqualTo(-2);
    }

    @Test
    void addLazyFactoryWorks() {
        TypeTag lazyTag = new TypeTag(Lazy.class);
        factoryCache.put(Lazy.class.getName(), values(Lazy.X, Lazy.Y, Lazy.X));
        vp = new VintageValueProvider(factoryCache, objenesis);
        assertThat(vp.<Lazy>giveRed(lazyTag)).isEqualTo(Lazy.X);
        assertThat(vp.<Lazy>giveBlue(lazyTag)).isEqualTo(Lazy.Y);
        assertThat(vp.<Lazy>giveRedCopy(lazyTag)).isEqualTo(Lazy.X);
    }

    @Test
    void addLazyFactoryIsLazy() {
        TypeTag throwingInitializerTag = new TypeTag(ThrowingInitializer.class);

        // Shouldn't throw, because constructing PrefabValues doesn't instantiate objects:
        factoryCache
                .put(
                    ThrowingInitializer.class.getName(),
                    (t, p, ts) -> new Tuple<>(ThrowingInitializer.X, ThrowingInitializer.Y, ThrowingInitializer.X));
        vp = new VintageValueProvider(factoryCache, objenesis);

        // Should throw, because `giveRed` does instantiate objects:
        try {
            vp.giveRed(throwingInitializerTag);
            fail("Expected an exception");
        }
        catch (Error e) {
            // succeed
        }
    }

    public static class NpeThrowing {

        private final int i;

        public NpeThrowing(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                throw new NullPointerException();
            }
            if (!(obj instanceof NpeThrowing)) {
                return false;
            }
            return i == ((NpeThrowing) obj).i;
        }

        @Override
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
                LinkedHashSet<TypeTag> typeStack) {
            red += "r";
            blue += "b";
            return new Tuple<>(red, blue, new String(red));
        }
    }

    @SuppressWarnings("rawtypes")
    private static final class ListTestFactory implements PrefabValueFactory<List> {

        @Override
        public Tuple<List> createValues(
                TypeTag tag,
                VintageValueProvider valueProvider,
                LinkedHashSet<TypeTag> typeStack) {
            TypeTag subtag = tag.genericTypes().get(0);

            List red = List.of(valueProvider.<Object>giveRed(subtag));
            List blue = List.of(valueProvider.<Object>giveBlue(subtag));
            List redCopy = List.of(valueProvider.<Object>giveRed(subtag));

            return new Tuple<>(red, blue, redCopy);
        }
    }

    private static final class StaticContainer {

        static int staticInt = 2;

        @SuppressWarnings("unused")
        int regularInt = 3;
    }

    @SuppressWarnings("unused")
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
