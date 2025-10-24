package nl.jqno.equalsverifier.internal.instantiation.prefab;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericValueSupplierTest {

    private static final TypeTag STRING = new TypeTag(String.class);
    private static final TypeTag WILDCARD = new TypeTag(Object.class);
    private static final TypeTag SINGLETON = new TypeTag(OneElementEnum.class);
    private static final Attributes SOME_ATTRIBUTES = Attributes.empty();

    private static final String RED_STRING = "red";
    private static final String BLUE_STRING = "blue";
    private static final Object RED_OBJECT = new Object();
    private static final Object BLUE_OBJECT = new Object();

    private UserPrefabValueProvider underlying = new UserPrefabValueProvider();

    @SuppressWarnings("rawtypes")
    private GenericValueSupplier<List> sut = new Sut<>(List.class, underlying);
    @SuppressWarnings("rawtypes")
    private GenericValueSupplier<Map> mapSut = new Sut<>(Map.class, underlying);

    @BeforeEach
    public void setUp() {
        underlying.register(String.class, RED_STRING, BLUE_STRING, RED_STRING);
        underlying.register(Object.class, RED_OBJECT, BLUE_OBJECT, RED_OBJECT);
        underlying.register(OneElementEnum.class, OneElementEnum.ONE, OneElementEnum.ONE, OneElementEnum.ONE);
    }

    @Test
    void is() {
        assertThat(sut.is(List.class)).isTrue();
        assertThat(sut.is(int.class)).isFalse();
    }

    @Test
    void genericWithoutEmpty_String() {
        var tag = new TypeTag(List.class, STRING);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of);
        assertThat(actual).contains(new Tuple<>(List.of(RED_STRING), List.of(BLUE_STRING), List.of(RED_STRING)));
    }

    @Test
    void genericWithoutEmpty_Wildcard() {
        var tag = new TypeTag(List.class, WILDCARD);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of);
        assertThat(actual).contains(new Tuple<>(List.of(RED_OBJECT), List.of(BLUE_OBJECT), List.of(RED_OBJECT)));
    }

    @Test
    void genericWithoutEmpty_Raw() {
        var tag = new TypeTag(List.class);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of);
        assertThat(actual).contains(new Tuple<>(List.of(RED_OBJECT), List.of(BLUE_OBJECT), List.of(RED_OBJECT)));
    }

    @Test
    void genericWithoutEmpty_EqualRedAndBlueYieldsEqualRedAndBlue() {
        var tag = new TypeTag(List.class, SINGLETON);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of);
        assertThat(actual)
                .contains(
                    new Tuple<>(List.of(OneElementEnum.ONE), List.of(OneElementEnum.ONE), List.of(OneElementEnum.ONE)));
    }

    @Test
    void genericWithEmpty_String() {
        var tag = new TypeTag(List.class, STRING);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of, List::of);
        assertThat(actual).contains(new Tuple<>(List.of(RED_STRING), List.of(BLUE_STRING), List.of(RED_STRING)));
    }

    @Test
    void genericWithEmpty_Wildcard() {
        var tag = new TypeTag(List.class, WILDCARD);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of, List::of);
        assertThat(actual).contains(new Tuple<>(List.of(RED_OBJECT), List.of(BLUE_OBJECT), List.of(RED_OBJECT)));
    }

    @Test
    void genericWithEmpty_Raw() {
        var tag = new TypeTag(List.class);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of, List::of);
        assertThat(actual).contains(new Tuple<>(List.of(RED_OBJECT), List.of(BLUE_OBJECT), List.of(RED_OBJECT)));
    }

    @Test
    void genericWithEmpty_EqualRedAndBlueYieldsEmptyBlue() {
        var tag = new TypeTag(List.class, SINGLETON);
        var actual = sut.generic(tag, SOME_ATTRIBUTES, List::of, List::of);
        assertThat(actual).contains(new Tuple<>(List.of(OneElementEnum.ONE), List.of(), List.of(OneElementEnum.ONE)));
    }

    @Test
    void collection_String() {
        var tag = new TypeTag(List.class, STRING);
        var actual = sut.collection(tag, SOME_ATTRIBUTES, ArrayList::new);
        assertThat(actual).contains(new Tuple<>(List.of(RED_STRING), List.of(BLUE_STRING), List.of(RED_STRING)));
    }

    @Test
    void collection_Wildcard() {
        var tag = new TypeTag(List.class, WILDCARD);
        var actual = sut.collection(tag, SOME_ATTRIBUTES, ArrayList::new);
        assertThat(actual).contains(new Tuple<>(List.of(RED_OBJECT), List.of(BLUE_OBJECT), List.of(RED_OBJECT)));
    }

    @Test
    void collection_Raw() {
        var tag = new TypeTag(List.class);
        var actual = sut.collection(tag, SOME_ATTRIBUTES, ArrayList::new);
        assertThat(actual).contains(new Tuple<>(List.of(RED_OBJECT), List.of(BLUE_OBJECT), List.of(RED_OBJECT)));
    }

    @Test
    void collection_EqualRedAndBlueYieldsEmptyBlue() {
        var tag = new TypeTag(List.class, SINGLETON);
        var actual = sut.collection(tag, SOME_ATTRIBUTES, ArrayList::new);
        assertThat(actual).contains(new Tuple<>(List.of(OneElementEnum.ONE), List.of(), List.of(OneElementEnum.ONE)));
    }

    @Test
    void map_String() {
        var tag = new TypeTag(Map.class, STRING, STRING);
        var actual = mapSut.map(tag, SOME_ATTRIBUTES, HashMap::new);
        assertThat(actual)
                .contains(
                    new Tuple<>(Map.of(RED_STRING, BLUE_STRING),
                            Map.of(BLUE_STRING, BLUE_STRING),
                            Map.of(RED_STRING, BLUE_STRING)));
    }

    @Test
    void map_Wildcard() {
        var tag = new TypeTag(Map.class, WILDCARD, WILDCARD);
        var actual = mapSut.map(tag, SOME_ATTRIBUTES, HashMap::new);
        assertThat(actual)
                .contains(
                    new Tuple<>(Map.of(RED_OBJECT, BLUE_OBJECT),
                            Map.of(BLUE_OBJECT, BLUE_OBJECT),
                            Map.of(RED_OBJECT, BLUE_OBJECT)));
    }

    @Test
    void map_Raw() {
        var tag = new TypeTag(Map.class);
        var actual = mapSut.map(tag, SOME_ATTRIBUTES, HashMap::new);
        assertThat(actual)
                .contains(
                    new Tuple<>(Map.of(RED_OBJECT, BLUE_OBJECT),
                            Map.of(BLUE_OBJECT, BLUE_OBJECT),
                            Map.of(RED_OBJECT, BLUE_OBJECT)));
    }

    @Test
    void map_EqualRedAndBlueYieldsEmptyBlue() {
        var tag = new TypeTag(Map.class, SINGLETON, STRING);
        var actual = mapSut.map(tag, SOME_ATTRIBUTES, HashMap::new);
        assertThat(actual)
                .contains(
                    new Tuple<>(Map.of(OneElementEnum.ONE, BLUE_STRING),
                            Map.of(),
                            Map.of(OneElementEnum.ONE, BLUE_STRING)));
    }

    static class Sut<T> extends GenericValueSupplier<T> {

        public Sut(Class<T> type, ValueProvider vp) {
            super(type, vp);
        }

        @Override
        public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
            return Optional.empty();
        }
    }
}
