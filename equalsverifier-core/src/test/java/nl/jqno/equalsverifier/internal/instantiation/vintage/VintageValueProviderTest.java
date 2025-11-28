package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueCaches;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class VintageValueProviderTest {

    private static final TypeTag INT_TAG = new TypeTag(int.class);

    private final UserPrefabValueCaches prefabs = new UserPrefabValueCaches();
    private final UserPrefabValueProvider prefabValueProvider = new UserPrefabValueProvider(prefabs);
    private final FactoryCache factoryCache = new FactoryCache();
    private final Objenesis objenesis = new ObjenesisStd();
    private VintageValueProvider vp;

    @BeforeEach
    void setUp() {
        factoryCache.put(String.class, new AppendingStringTestFactory());
        prefabs.register(int.class, 42, 1337, 42);
        vp = new VintageValueProvider(prefabValueProvider, factoryCache, objenesis);
    }

    @Test
    void sanityTestFactoryIncreasesStringLength() {
        AppendingStringTestFactory f = new AppendingStringTestFactory();
        assertThat(f.createValues(null, null, null).red()).isEqualTo("r");
        assertThat(f.createValues(null, null, null).red()).isEqualTo("rr");
        assertThat(f.createValues(null, null, null).red()).isEqualTo("rrr");
    }

    @Test
    void addingNullDoesntBreakAnything() {
        factoryCache.put((Class<?>) null, new ListTestFactory());
    }

    @Test
    void addingATypeTwiceOverrulesTheExistingOne() {
        prefabs.register(int.class, -1, -2, -1);
        vp = new VintageValueProvider(prefabValueProvider, factoryCache, objenesis);
        assertThat(vp.provideOrThrow(INT_TAG, Attributes.empty())).isEqualTo(new Tuple<>(-1, -2, -1));
    }

    @Test
    void addLazyFactoryWorks() {
        TypeTag lazyTag = new TypeTag(Lazy.class);
        prefabs.register(Lazy.class, Lazy.X, Lazy.Y, Lazy.X);
        vp = new VintageValueProvider(prefabValueProvider, factoryCache, objenesis);
        assertThat(vp.<Lazy>provideOrThrow(lazyTag, Attributes.empty())).isEqualTo(new Tuple<>(Lazy.X, Lazy.Y, Lazy.X));
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

            return valueProvider.<Object>provideOrThrow(subtag, Attributes.empty()).map(v -> List.of(v));
        }
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
