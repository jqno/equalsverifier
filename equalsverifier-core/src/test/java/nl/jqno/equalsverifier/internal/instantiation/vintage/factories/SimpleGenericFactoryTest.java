package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.prefab.BuiltinPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

@SuppressWarnings({ "rawtypes", "unchecked" })
class SimpleGenericFactoryTest {

    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag INTEGER_TYPETAG = new TypeTag(Integer.class);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag STRINGOPTIONAL_TYPETAG = new TypeTag(Optional.class, STRING_TYPETAG);
    private static final TypeTag WILDCARDOPTIONAL_TYPETAG = new TypeTag(Optional.class, OBJECT_TYPETAG);
    private static final TypeTag RAWOPTIONAL_TYPETAG = new TypeTag(Optional.class);
    private static final TypeTag PAIR_TYPETAG = new TypeTag(Pair.class, STRING_TYPETAG, INTEGER_TYPETAG);

    private static final PrefabValueFactory<Optional> OPTIONAL_FACTORY =
            Factories.simple(Optional::of, Optional::empty);
    private static final PrefabValueFactory<Pair> PAIR_FACTORY = Factories.simple(Pair::new, null);

    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private VintageValueProvider valueProvider;
    private String redString;
    private String blueString;
    private Integer redInt;
    private Integer blueInt;
    private Object redObject;
    private Object blueObject;

    @BeforeEach
    void setUp() {
        valueProvider =
                new VintageValueProvider(new BuiltinPrefabValueProvider(), new FactoryCache(), new ObjenesisStd());

        Tuple<String> strings = valueProvider.provideOrThrow(STRING_TYPETAG, Attributes.empty());
        redString = strings.red();
        blueString = strings.blue();

        Tuple<Integer> ints = valueProvider.provideOrThrow(INTEGER_TYPETAG, Attributes.empty());
        redInt = ints.red();
        blueInt = ints.blue();

        Tuple<Object> objects = valueProvider.provideOrThrow(OBJECT_TYPETAG, Attributes.empty());
        redObject = objects.red();
        blueObject = objects.blue();
    }

    @Test
    void createOptionalsOfMapOfString() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(STRINGOPTIONAL_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.red()).isEqualTo(Optional.of(redString));
        assertThat(tuple.blue()).isEqualTo(Optional.of(blueString));
    }

    @Test
    void createOptionalsOfWildcard() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(WILDCARDOPTIONAL_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.red()).isEqualTo(Optional.of(redObject));
        assertThat(tuple.blue()).isEqualTo(Optional.of(blueObject));
    }

    @Test
    void createRawOptionals() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(RAWOPTIONAL_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.red()).isEqualTo(Optional.of(redObject));
        assertThat(tuple.blue()).isEqualTo(Optional.of(blueObject));
    }

    @Test
    void createSomethingWithMoreThanOneTypeParameter() {
        Tuple<Pair> tuple = PAIR_FACTORY.createValues(PAIR_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.red()).isEqualTo(new Pair<>(redString, redInt));
        assertThat(tuple.blue()).isEqualTo(new Pair<>(blueString, blueInt));
    }
}
