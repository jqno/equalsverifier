package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.FactoryCacheFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Enum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class VintageValueProviderCreatorTest {

    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag ENUM_TAG = new TypeTag(Enum.class);
    private static final TypeTag ONE_ELT_ENUM_TAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag EMPTY_ENUM_TAG = new TypeTag(EmptyEnum.class);
    private static final TypeTag NODE_TAG = new TypeTag(Node.class);
    private static final TypeTag NODE_ARRAY_TAG = new TypeTag(NodeArray.class);
    private static final TypeTag TWOSTEP_NODE_A_TAG = new TypeTag(TwoStepNodeA.class);
    private static final TypeTag TWOSTEP_NODE_ARRAY_A_TAG = new TypeTag(TwoStepNodeArrayA.class);

    private ValueProvider prefabs;
    private FactoryCache factoryCache;
    private Objenesis objenesis;
    private VintageValueProvider valueProvider;

    @BeforeEach
    void setup() {
        prefabs = new UserPrefabValueProvider();
        factoryCache = FactoryCacheFactory.withPrimitiveFactories();
        objenesis = new ObjenesisStd();
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
    }

    @Test
    void simple() {
        Point red = valueProvider.giveRed(POINT_TAG);
        Point blue = valueProvider.giveBlue(POINT_TAG);
        assertThat(blue).isNotEqualTo(red);
    }

    @Test
    void createSecondTimeIsNoOp() {
        Point red = valueProvider.giveRed(POINT_TAG);
        Point blue = valueProvider.giveBlue(POINT_TAG);

        assertThat(valueProvider.<Point>giveRed(POINT_TAG)).isSameAs(red);
        assertThat(valueProvider.<Point>giveBlue(POINT_TAG)).isSameAs(blue);
    }

    @Test
    void createEnum() {
        assertThat(valueProvider.<Enum>giveRed(ENUM_TAG)).isNotNull();
        assertThat(valueProvider.<Enum>giveBlue(ENUM_TAG)).isNotNull();
    }

    @Test
    void createOneElementEnum() {
        assertThat(valueProvider.<OneElementEnum>giveRed(ONE_ELT_ENUM_TAG)).isNotNull();
        assertThat(valueProvider.<OneElementEnum>giveBlue(ONE_ELT_ENUM_TAG)).isNotNull();
    }

    @Test
    void createEmptyEnum() {
        assertThat(valueProvider.<EmptyEnum>giveRed(EMPTY_ENUM_TAG)).isNull();
        assertThat(valueProvider.<EmptyEnum>giveBlue(EMPTY_ENUM_TAG)).isNull();
    }

    @Test
    void oneStepRecursiveType() {
        factoryCache.put(Node.class, values(new Node(), new Node(), new Node()));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveRed(NODE_TAG);
    }

    @Test
    void dontAddOneStepRecursiveType() {
        ExpectedException.when(() -> valueProvider.giveRed(NODE_TAG)).assertThrows(RecursionException.class);
    }

    @Test
    void oneStepRecursiveArrayType() {
        factoryCache.put(NodeArray.class, values(new NodeArray(), new NodeArray(), new NodeArray()));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    void dontAddOneStepRecursiveArrayType() {
        ExpectedException.when(() -> valueProvider.giveRed(NODE_ARRAY_TAG)).assertThrows(RecursionException.class);
    }

    @Test
    void addTwoStepRecursiveType() {
        factoryCache.put(TwoStepNodeB.class, values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB()));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    void dontAddTwoStepRecursiveType() {
        ExpectedException.when(() -> valueProvider.giveRed(TWOSTEP_NODE_A_TAG)).assertThrows(RecursionException.class);
    }

    @Test
    void twoStepRecursiveArrayType() {
        factoryCache
                .put(
                    TwoStepNodeArrayB.class,
                    values(new TwoStepNodeArrayB(), new TwoStepNodeArrayB(), new TwoStepNodeArrayB()));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    void dontAddTwoStepRecursiveArrayType() {
        ExpectedException
                .when(() -> valueProvider.giveRed(TWOSTEP_NODE_ARRAY_A_TAG))
                .assertThrows(RecursionException.class);
    }

    @Test
    void sameClassTwiceButNoRecursion() {
        valueProvider.giveRed(new TypeTag(NotRecursiveA.class));
    }

    @Test
    void recursiveWithAnotherFieldFirst() {
        ExpectedException
                .when(() -> valueProvider.giveRed(new TypeTag(RecursiveWithAnotherFieldFirst.class)))
                .assertThrows(RecursionException.class)
                .assertDescriptionContains(RecursiveWithAnotherFieldFirst.class.getSimpleName())
                .assertDescriptionDoesNotContain(RecursiveThisIsTheOtherField.class.getSimpleName());
    }

    @Test
    void exceptionMessage() {
        ExpectedException
                .when(() -> valueProvider.giveRed(TWOSTEP_NODE_A_TAG))
                .assertThrows(RecursionException.class)
                .assertDescriptionContains(TwoStepNodeA.class.getSimpleName(), TwoStepNodeB.class.getSimpleName());
    }

    @Test
    void skipStaticFinal() {
        valueProvider.giveRed(new TypeTag(StaticFinalContainer.class));
    }

    static class StaticFinalContainer {

        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
