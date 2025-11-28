package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.*;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

class RecordObjectAccessorScramblingTest {

    private static final LinkedHashSet<TypeTag> EMPTY_TYPE_STACK = new LinkedHashSet<>();
    private UserPrefabValueCaches prefabs;
    private VintageValueProvider valueProvider;

    @BeforeEach
    void setup() throws Exception {
        prefabs = new UserPrefabValueCaches();
        var chain = new ChainedValueProvider(new UserPrefabValueProvider(prefabs), new BuiltinPrefabValueProvider());
        valueProvider = new VintageValueProvider(chain, new ObjenesisStd());
    }

    @Test
    void scrambleLeavesOriginalUnaffected() throws Exception {
        Constructor<?> c = Point.class.getDeclaredConstructor(int.class, int.class);
        Object original = c.newInstance(2, 3);
        Object copy = doScramble(original).get();
        assertThat(original).isNotSameAs(copy);
    }

    @Test
    void scramble() throws Exception {
        Constructor<?> constructor = Point.class.getDeclaredConstructor(int.class, int.class);
        prefabs
                .register(
                    Point.class,
                    (Point) constructor.newInstance(1, 2),
                    (Point) constructor.newInstance(2, 3),
                    (Point) constructor.newInstance(1, 2));
        Object original = constructor.newInstance(1, 2);

        Object scrambled = doScramble(original);
        assertThat(scrambled).isNotEqualTo(original);
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private ObjectAccessor<Object> doScramble(Object object) {
        return create(object).scramble(valueProvider, TypeTag.NULL, EMPTY_TYPE_STACK);
    }

    record Point(int x, int y) {}

    record TypeContainerRecord(int i, boolean b, String s, Object o) {}

    private static final String ORIGINAL_VALUE = "original";

    record StaticFieldContainer(int nonstatic) {
        public static final String STATIC_FINAL = ORIGINAL_VALUE;
        public static String staticNonfinal = ORIGINAL_VALUE;
    }
}
