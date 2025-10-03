package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.*;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.SimpleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class VintageValueProviderCreatorTest {

    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag ENUM_TAG = new TypeTag(SimpleEnum.class);
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
        factoryCache = cacheWithPrimitiveFactories();
        objenesis = new ObjenesisStd();
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
    }

    @Test
    void simple() {
        Tuple<Point> tuple = valueProvider.giveTuple(POINT_TAG);
        assertThat(tuple.blue()).isNotEqualTo(tuple.red());
    }

    @Test
    void createSecondTimeIsNoOp() {
        Tuple<Point> tuple = valueProvider.giveTuple(POINT_TAG);
        assertThat(valueProvider.giveTuple(POINT_TAG)).isSameAs(tuple);
    }

    @Test
    void createEnum() {
        Tuple<SimpleEnum> tuple = valueProvider.giveTuple(ENUM_TAG);
        assertThat(tuple.red()).isNotNull();
        assertThat(tuple.blue()).isNotNull();
    }

    @Test
    void createOneElementEnum() {
        Tuple<OneElementEnum> tuple = valueProvider.giveTuple(ONE_ELT_ENUM_TAG);
        assertThat(tuple.red()).isNotNull();
        assertThat(tuple.blue()).isNotNull();
    }

    @Test
    void createEmptyEnum() {
        Tuple<EmptyEnum> tuple = valueProvider.giveTuple(EMPTY_ENUM_TAG);
        assertThat(tuple.red()).isNull();
        assertThat(tuple.blue()).isNull();
    }

    @Test
    void oneStepRecursiveType() {
        factoryCache.put(Node.class, values(new Node(null), new Node(new Node(null)), new Node(null)));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveTuple(NODE_TAG);
    }

    @Test
    void dontAddOneStepRecursiveType() {
        ExpectedException.when(() -> valueProvider.giveTuple(NODE_TAG)).assertThrows(RecursionException.class);
    }

    @Test
    void oneStepRecursiveArrayType() {
        factoryCache
                .put(
                    NodeArray.class,
                    values(new NodeArray(null), new NodeArray(new NodeArray[] {}), new NodeArray(null)));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveTuple(NODE_ARRAY_TAG);
    }

    @Test
    void dontAddOneStepRecursiveArrayType() {
        ExpectedException.when(() -> valueProvider.giveTuple(NODE_ARRAY_TAG)).assertThrows(RecursionException.class);
    }

    @Test
    void addTwoStepRecursiveType() {
        factoryCache
                .put(
                    TwoStepNodeB.class,
                    values(new TwoStepNodeB(null), new TwoStepNodeB(new TwoStepNodeA(null)), new TwoStepNodeB(null)));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveTuple(TWOSTEP_NODE_A_TAG);
    }

    @Test
    void dontAddTwoStepRecursiveType() {
        ExpectedException
                .when(() -> valueProvider.giveTuple(TWOSTEP_NODE_A_TAG))
                .assertThrows(RecursionException.class);
    }

    @Test
    void twoStepRecursiveArrayType() {
        factoryCache
                .put(
                    TwoStepNodeArrayB.class,
                    values(
                        new TwoStepNodeArrayB(null),
                        new TwoStepNodeArrayB(new TwoStepNodeArrayA[] {}),
                        new TwoStepNodeArrayB(null)));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.giveTuple(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    void dontAddTwoStepRecursiveArrayType() {
        ExpectedException
                .when(() -> valueProvider.giveTuple(TWOSTEP_NODE_ARRAY_A_TAG))
                .assertThrows(RecursionException.class);
    }

    @Test
    void sameClassTwiceButNoRecursion() {
        valueProvider.giveTuple(new TypeTag(NotRecursiveA.class));
    }

    @Test
    void recursiveWithAnotherFieldFirst() {
        assertThatThrownBy(() -> valueProvider.giveTuple(new TypeTag(RecursiveWithAnotherFieldFirst.class)))
                .isInstanceOf(RecursionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains(RecursiveWithAnotherFieldFirst.class.getSimpleName())
                .doesNotContain(RecursiveThisIsTheOtherField.class.getSimpleName());
    }

    @Test
    void exceptionMessage() {
        assertThatThrownBy(() -> valueProvider.giveTuple(TWOSTEP_NODE_A_TAG))
                .isInstanceOf(RecursionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains(TwoStepNodeA.class.getSimpleName(), TwoStepNodeB.class.getSimpleName());
    }

    @Test
    void skipStaticFinal() {
        valueProvider.giveTuple(new TypeTag(StaticFinalContainer.class));
    }

    static class StaticFinalContainer {

        public static final StaticFinalContainer X = new StaticFinalContainer();
    }

    public static FactoryCache cacheWithPrimitiveFactories() {
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(boolean.class, values(true, false, true));
        factoryCache.put(byte.class, values((byte) 1, (byte) 2, (byte) 1));
        factoryCache.put(char.class, values('a', 'b', 'a'));
        factoryCache.put(double.class, values(0.5D, 1.0D, 0.5D));
        factoryCache.put(float.class, values(0.5F, 1.0F, 0.5F));
        factoryCache.put(int.class, values(1, 2, 1));
        factoryCache.put(long.class, values(1L, 2L, 1L));
        factoryCache.put(short.class, values((short) 1, (short) 2, (short) 1));
        return factoryCache;
    }
}
