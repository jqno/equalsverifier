package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import nl.jqno.equalsverifier_testhelpers.types.ThrowingInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class VintageValueProviderTest {

    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag INT_TAG = new TypeTag(int.class);

    private final UserPrefabValueProvider prefabs = new UserPrefabValueProvider();
    private final FactoryCache factoryCache = new FactoryCache();
    private final Objenesis objenesis = new ObjenesisStd();
    private VintageValueProvider vp;

    @BeforeEach
    void setUp() {
        factoryCache.put(String.class, new AppendingStringTestFactory());
        prefabs.register(int.class, 42, 1337, 42);
        vp = new VintageValueProvider(prefabs, factoryCache, objenesis);
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
        Optional<Tuple<Point>> actual = vp.provide(POINT_TAG, null);
        assertThat(actual).contains(new Tuple<>(new Point(42, 42), new Point(1337, 1337), new Point(42, 42)));
    }

    @Test
    void giveTupleFromFactory() {
        assertThat(vp.<String>giveTuple(STRING_TAG)).isEqualTo(new Tuple<>("r", "b", "r"));
    }

    @Test
    void giveTupleFromCache() {
        vp.giveTuple(STRING_TAG);
        assertThat(vp.<String>giveTuple(STRING_TAG)).isEqualTo(new Tuple<>("r", "b", "r"));
    }

    @Test
    void giveTupleFromFallbackFactory() {
        Tuple<Point> actual = vp.giveTuple(POINT_TAG);
        assertThat(actual).isEqualTo(new Tuple<>(new Point(42, 42), new Point(1337, 1337), new Point(42, 42)));
    }

    @Test
    void fallbackDoesNotAffectStaticFields() {
        int expected = StaticContainer.staticInt;
        vp.giveTuple(new TypeTag(StaticContainer.class));
        assertThat(StaticContainer.staticInt).isEqualTo(expected);
    }

    @Test
    void stringListIsSeparateFromIntegerList() {
        factoryCache.put(List.class, new ListTestFactory());
        vp = new VintageValueProvider(prefabs, factoryCache, objenesis);

        List<String> strings = vp.<List<String>>giveTuple(new TypeTag(List.class, STRING_TAG)).red();
        List<Integer> ints = vp.<List<Integer>>giveTuple(new TypeTag(List.class, INT_TAG)).red();

        assertThat(strings.get(0)).isEqualTo("r");
        assertThat((int) ints.get(0)).isEqualTo(42);
    }

    @Test
    void addingNullDoesntBreakAnything() {
        factoryCache.put((Class<?>) null, new ListTestFactory());
    }

    @Test
    void addingATypeTwiceOverrulesTheExistingOne() {
        prefabs.register(int.class, -1, -2, -1);
        vp = new VintageValueProvider(prefabs, factoryCache, objenesis);
        assertThat(vp.giveTuple(INT_TAG)).isEqualTo(new Tuple<>(-1, -2, -1));
    }

    @Test
    void addLazyFactoryWorks() {
        TypeTag lazyTag = new TypeTag(Lazy.class);
        prefabs.register(Lazy.class, Lazy.X, Lazy.Y, Lazy.X);
        vp = new VintageValueProvider(prefabs, factoryCache, objenesis);
        assertThat(vp.<Lazy>giveTuple(lazyTag)).isEqualTo(new Tuple<>(Lazy.X, Lazy.Y, Lazy.X));
    }

    @Test
    void addLazyFactoryIsLazy() {
        TypeTag throwingInitializerTag = new TypeTag(ThrowingInitializer.class);

        // Shouldn't throw, because declaring Factories doesn't instantiate objects:
        factoryCache
                .put(
                    ThrowingInitializer.class.getName(),
                    (t, p, ts) -> new Tuple<>(ThrowingInitializer.X, ThrowingInitializer.Y, ThrowingInitializer.X));
        vp = new VintageValueProvider(prefabs, factoryCache, objenesis);

        // Should throw, because `giveTuple` does instantiate objects:
        try {
            vp.giveTuple(throwingInitializerTag);
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

    private static final class AppendingStringTestFactory implements PrefabValueFactory<String> {

        private String red;
        private String blue;

        private AppendingStringTestFactory() {
            red = "";
            blue = "";
        }

        @Override
        @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
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
        @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
        public Tuple<List> createValues(
                TypeTag tag,
                VintageValueProvider valueProvider,
                LinkedHashSet<TypeTag> typeStack) {
            TypeTag subtag = tag.genericTypes().get(0);

            return valueProvider.<Object>giveTuple(subtag).map(v -> List.of(v));
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
            return obj instanceof Lazy other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }

        @Override
        public String toString() {
            return "Lazy: " + i;
        }
    }
}
