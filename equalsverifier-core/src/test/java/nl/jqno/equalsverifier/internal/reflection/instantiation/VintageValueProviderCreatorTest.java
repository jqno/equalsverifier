package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCacheFactory;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Enum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class VintageValueProviderCreatorTest {

    private static final Attributes EMPTY_ATTRIBUTES = Attributes.unlabeled();
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag ENUM_TAG = new TypeTag(Enum.class);
    private static final TypeTag ONE_ELT_ENUM_TAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag EMPTY_ENUM_TAG = new TypeTag(EmptyEnum.class);
    private static final TypeTag NODE_TAG = new TypeTag(Node.class);
    private static final TypeTag NODE_ARRAY_TAG = new TypeTag(NodeArray.class);
    private static final TypeTag TWOSTEP_NODE_A_TAG = new TypeTag(TwoStepNodeA.class);
    private static final TypeTag TWOSTEP_NODE_ARRAY_A_TAG = new TypeTag(TwoStepNodeArrayA.class);

    private Objenesis objenesis;
    private CachedValueProvider cache;
    private FactoryCache factoryCache;
    private VintageValueProvider valueProvider;

    @BeforeEach
    public void setup() {
        objenesis = new ObjenesisStd();
        cache = new CachedValueProvider();
        factoryCache = FactoryCacheFactory.withPrimitiveFactories();
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
    }

    @Test
    public void simple() {
        Tuple<Point> tuple = valueProvider.provideOrThrow(POINT_TAG, EMPTY_ATTRIBUTES);
        assertFalse(tuple.getRed().equals(tuple.getBlue()));
    }

    @Test
    public void createEnum() {
        Tuple<Enum> tuple = valueProvider.provideOrThrow(ENUM_TAG, EMPTY_ATTRIBUTES);
        assertNotNull(tuple.getRed());
        assertNotNull(tuple.getBlue());
    }

    @Test
    public void createOneElementEnum() {
        Tuple<OneElementEnum> tuple = valueProvider.provideOrThrow(
            ONE_ELT_ENUM_TAG,
            EMPTY_ATTRIBUTES
        );
        assertNotNull(tuple.getRed());
        assertNotNull(tuple.getBlue());
    }

    @Test
    public void createEmptyEnum() {
        Tuple<EmptyEnum> tuple = valueProvider.provideOrThrow(EMPTY_ENUM_TAG, EMPTY_ATTRIBUTES);
        assertNull(tuple.getRed());
        assertNull(tuple.getBlue());
    }

    @Test
    public void oneStepRecursiveType() {
        factoryCache.put(Node.class, values(new Node(), new Node(), new Node()));
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        valueProvider.provideOrThrow(NODE_TAG, EMPTY_ATTRIBUTES);
    }

    @Test
    public void dontAddOneStepRecursiveType() {
        ExpectedException
            .when(() -> valueProvider.provideOrThrow(NODE_TAG, EMPTY_ATTRIBUTES))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void oneStepRecursiveArrayType() {
        factoryCache.put(
            NodeArray.class,
            values(new NodeArray(), new NodeArray(), new NodeArray())
        );
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        valueProvider.provideOrThrow(NODE_ARRAY_TAG, EMPTY_ATTRIBUTES);
    }

    @Test
    public void dontAddOneStepRecursiveArrayType() {
        ExpectedException
            .when(() -> valueProvider.provideOrThrow(NODE_ARRAY_TAG, EMPTY_ATTRIBUTES))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void addTwoStepRecursiveType() {
        factoryCache.put(
            TwoStepNodeB.class,
            values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB())
        );
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        valueProvider.provideOrThrow(TWOSTEP_NODE_A_TAG, EMPTY_ATTRIBUTES);
    }

    @Test
    public void dontAddTwoStepRecursiveType() {
        ExpectedException
            .when(() -> valueProvider.provideOrThrow(TWOSTEP_NODE_A_TAG, EMPTY_ATTRIBUTES))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void twoStepRecursiveArrayType() {
        factoryCache.put(
            TwoStepNodeArrayB.class,
            values(new TwoStepNodeArrayB(), new TwoStepNodeArrayB(), new TwoStepNodeArrayB())
        );
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        valueProvider.provideOrThrow(TWOSTEP_NODE_ARRAY_A_TAG, EMPTY_ATTRIBUTES);
    }

    @Test
    public void dontAddTwoStepRecursiveArrayType() {
        ExpectedException
            .when(() -> valueProvider.provideOrThrow(TWOSTEP_NODE_ARRAY_A_TAG, EMPTY_ATTRIBUTES))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void sameClassTwiceButNoRecursion() {
        valueProvider.provideOrThrow(new TypeTag(NotRecursiveA.class), EMPTY_ATTRIBUTES);
    }

    @Test
    public void recursiveWithAnotherFieldFirst() {
        ExpectedException
            .when(() ->
                valueProvider.provideOrThrow(
                    new TypeTag(RecursiveWithAnotherFieldFirst.class),
                    EMPTY_ATTRIBUTES
                )
            )
            .assertThrows(RecursionException.class)
            .assertDescriptionContains(RecursiveWithAnotherFieldFirst.class.getSimpleName())
            .assertDescriptionDoesNotContain(RecursiveThisIsTheOtherField.class.getSimpleName());
    }

    @Test
    public void exceptionMessage() {
        ExpectedException
            .when(() -> valueProvider.provideOrThrow(TWOSTEP_NODE_A_TAG, EMPTY_ATTRIBUTES))
            .assertThrows(RecursionException.class)
            .assertDescriptionContains(
                TwoStepNodeA.class.getSimpleName(),
                TwoStepNodeB.class.getSimpleName()
            );
    }

    @Test
    public void skipStaticFinal() {
        valueProvider.provideOrThrow(new TypeTag(StaticFinalContainer.class), EMPTY_ATTRIBUTES);
    }

    static class StaticFinalContainer {

        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
