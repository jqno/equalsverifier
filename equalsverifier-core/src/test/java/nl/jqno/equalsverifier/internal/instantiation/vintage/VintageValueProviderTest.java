package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueCaches;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
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
    private final Objenesis objenesis = new ObjenesisStd();
    private VintageValueProvider vp;

    @BeforeEach
    void setUp() {
        prefabs.register(int.class, 42, 1337, 42);
        vp = new VintageValueProvider(prefabValueProvider, objenesis);
    }

    @Test
    void addingATypeTwiceOverrulesTheExistingOne() {
        prefabs.register(int.class, -1, -2, -1);
        vp = new VintageValueProvider(prefabValueProvider, objenesis);
        assertThat(vp.provideOrThrow(INT_TAG, Attributes.empty())).isEqualTo(new Tuple<>(-1, -2, -1));
    }

    @Test
    void addLazyFactoryWorks() {
        TypeTag lazyTag = new TypeTag(Lazy.class);
        prefabs.register(Lazy.class, Lazy.X, Lazy.Y, Lazy.X);
        vp = new VintageValueProvider(prefabValueProvider, objenesis);
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
