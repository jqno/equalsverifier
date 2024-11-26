package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.CachedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import nl.jqno.equalsverifier.testhelpers.types.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

@SuppressWarnings("rawtypes")
public class SimpleGenericFactoryTest {

    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag INTEGER_TYPETAG = new TypeTag(Integer.class);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag STRINGOPTIONAL_TYPETAG = new TypeTag(
        Optional.class,
        STRING_TYPETAG
    );
    private static final TypeTag WILDCARDOPTIONAL_TYPETAG = new TypeTag(
        Optional.class,
        OBJECT_TYPETAG
    );
    private static final TypeTag RAWOPTIONAL_TYPETAG = new TypeTag(Optional.class);
    private static final TypeTag PAIR_TYPETAG = new TypeTag(
        Pair.class,
        STRING_TYPETAG,
        INTEGER_TYPETAG
    );

    private static final PrefabValueFactory<Optional> OPTIONAL_FACTORY = Factories.simple(
        Optional::of,
        Optional::empty
    );
    private static final PrefabValueFactory<Pair> PAIR_FACTORY = Factories.simple(Pair::new, null);

    private final Attributes attributes = Attributes.unlabeled();
    private VintageValueProvider valueProvider;
    private String redString;
    private String blueString;
    private Integer redInt;
    private Integer blueInt;
    private Object redObject;
    private Object blueObject;

    @BeforeEach
    public void setUp() {
        valueProvider =
            new VintageValueProvider(
                TestValueProviders.empty(),
                new CachedValueProvider(),
                JavaApiPrefabValues.build(),
                new ObjenesisStd()
            );
        redString = valueProvider.giveRed(STRING_TYPETAG);
        blueString = valueProvider.giveBlue(STRING_TYPETAG);
        redInt = valueProvider.giveRed(INTEGER_TYPETAG);
        blueInt = valueProvider.giveBlue(INTEGER_TYPETAG);
        redObject = valueProvider.giveRed(OBJECT_TYPETAG);
        blueObject = valueProvider.giveBlue(OBJECT_TYPETAG);
    }

    @Test
    public void createOptionalsOfMapOfString() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(
            STRINGOPTIONAL_TYPETAG,
            valueProvider,
            attributes
        );
        assertEquals(Optional.of(redString), tuple.getRed());
        assertEquals(Optional.of(blueString), tuple.getBlue());
    }

    @Test
    public void createOptionalsOfWildcard() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(
            WILDCARDOPTIONAL_TYPETAG,
            valueProvider,
            attributes
        );
        assertEquals(Optional.of(redObject), tuple.getRed());
        assertEquals(Optional.of(blueObject), tuple.getBlue());
    }

    @Test
    public void createRawOptionals() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(
            RAWOPTIONAL_TYPETAG,
            valueProvider,
            attributes
        );
        assertEquals(Optional.of(redObject), tuple.getRed());
        assertEquals(Optional.of(blueObject), tuple.getBlue());
    }

    @Test
    public void createSomethingWithMoreThanOneTypeParameter() {
        Tuple<Pair> tuple = PAIR_FACTORY.createValues(PAIR_TYPETAG, valueProvider, attributes);
        assertEquals(new Pair<>(redString, redInt), tuple.getRed());
        assertEquals(new Pair<>(blueString, blueInt), tuple.getBlue());
    }
}
