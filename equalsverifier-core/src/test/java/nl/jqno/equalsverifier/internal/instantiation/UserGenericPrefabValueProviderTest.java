package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class UserGenericPrefabValueProviderTest {

    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");

    private UserPrefabValueCaches prefabs = new UserPrefabValueCaches();
    private UserGenericPrefabValueProvider sut =
            new UserGenericPrefabValueProvider(prefabs, new BuiltinPrefabValueProvider());

    @Test
    void returnsEmpty_givenUnregsteredGeneric() {
        var actual = sut.provide(new TypeTag(Generic1.class), SOME_ATTRIBUTES);
        assertThat(actual).isEmpty();
    }

    @Test
    void returnsIntInstance_givenRegisteredGenericWithArity1() {
        prefabs.registerGeneric(Generic1.class, Generic1::new);
        var actual = sut
                .<Generic1<Integer>>provide(new TypeTag(Generic1.class, new TypeTag(Integer.class)), SOME_ATTRIBUTES)
                .get()
                .red();
        assertThat(actual.t).isEqualTo(1000);
    }

    @Test
    void returnsStringInstance_givenRegisteredGenericWithArity1() {
        prefabs.registerGeneric(Generic1.class, Generic1::new);
        var strings = sut
                .<Generic1<String>>provide(new TypeTag(Generic1.class, new TypeTag(String.class)), SOME_ATTRIBUTES)
                .get()
                .red();
        assertThat(strings.t).isEqualTo("one");
    }

    @Test
    void returnsIntStringInstance_givenRegisteredGenericWithArity2() {
        prefabs.registerGeneric(Generic2.class, Generic2::new);
        var actual = sut
                .<Generic2<Integer, String>>provide(
                    new TypeTag(Generic2.class, new TypeTag(Integer.class), new TypeTag(String.class)),
                    SOME_ATTRIBUTES)
                .get()
                .red();
        assertThat(actual.t).isEqualTo(1000);
        assertThat(actual.u).isEqualTo("one");
    }

    @Test
    void returnsCharDoubleInstance_givenRegisteredGenericWithArity2() {
        prefabs.registerGeneric(Generic2.class, Generic2::new);
        var strings = sut
                .<Generic2<Character, Double>>provide(
                    new TypeTag(Generic2.class, new TypeTag(Character.class), new TypeTag(Double.class)),
                    SOME_ATTRIBUTES)
                .get()
                .red();
        assertThat(strings.t).isEqualTo('α');
        assertThat(strings.u).isEqualTo(0.5);
    }

    static class Generic1<T> {
        private final T t;

        Generic1(T t) {
            this.t = t;
        }
    }

    static class Generic2<T, U> {
        private final T t;
        private final U u;

        Generic2(T t, U u) {
            this.t = t;
            this.u = u;
        }
    }
}
