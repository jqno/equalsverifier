package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.Attributes;
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
    private static final TypeTag TWOSTEP_NODE_A_TAG = new TypeTag(TwoStepNodeA.class);

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
        Tuple<Point> tuple = valueProvider.provideOrThrow(POINT_TAG, Attributes.empty());
        assertThat(tuple.blue()).isNotEqualTo(tuple.red());
    }

    @Test
    void createSecondTimeIsNoOp() {
        Tuple<Point> tuple = valueProvider.provideOrThrow(POINT_TAG, Attributes.empty());
        assertThat(valueProvider.provideOrThrow(POINT_TAG, Attributes.empty())).isSameAs(tuple);
    }

    @Test
    void createEnum() {
        Tuple<SimpleEnum> tuple = valueProvider.provideOrThrow(ENUM_TAG, Attributes.empty());
        assertThat(tuple.red()).isNotNull();
        assertThat(tuple.blue()).isNotNull();
    }

    @Test
    void createOneElementEnum() {
        Tuple<OneElementEnum> tuple = valueProvider.provideOrThrow(ONE_ELT_ENUM_TAG, Attributes.empty());
        assertThat(tuple.red()).isNotNull();
        assertThat(tuple.blue()).isNotNull();
    }

    @Test
    void createEmptyEnum() {
        Tuple<EmptyEnum> tuple = valueProvider.provideOrThrow(EMPTY_ENUM_TAG, Attributes.empty());
        assertThat(tuple.red()).isNull();
        assertThat(tuple.blue()).isNull();
    }

    @Test
    void oneStepRecursiveType() {
        factoryCache.put(Node.class, values(new Node(null), new Node(new Node(null)), new Node(null)));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.provideOrThrow(NODE_TAG, Attributes.empty());
    }

    @Test
    void dontAddOneStepRecursiveType() {
        ExpectedException
                .when(() -> valueProvider.provideOrThrow(NODE_TAG, Attributes.empty()))
                .assertThrows(RecursionException.class);
    }

    @Test
    void addTwoStepRecursiveType() {
        factoryCache
                .put(
                    TwoStepNodeB.class,
                    values(new TwoStepNodeB(null), new TwoStepNodeB(new TwoStepNodeA(null)), new TwoStepNodeB(null)));
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        valueProvider.provideOrThrow(TWOSTEP_NODE_A_TAG, Attributes.empty());
    }

    @Test
    void dontAddTwoStepRecursiveType() {
        ExpectedException
                .when(() -> valueProvider.provideOrThrow(TWOSTEP_NODE_A_TAG, Attributes.empty()))
                .assertThrows(RecursionException.class);
    }

    @Test
    void sameClassTwiceButNoRecursion() {
        valueProvider.provideOrThrow(new TypeTag(NotRecursiveA.class), Attributes.empty());
    }

    @Test
    void recursiveWithAnotherFieldFirst() {
        assertThatThrownBy(
            () -> valueProvider.provideOrThrow(new TypeTag(RecursiveWithAnotherFieldFirst.class), Attributes.empty()))
                .isInstanceOf(RecursionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains(RecursiveWithAnotherFieldFirst.class.getSimpleName())
                .doesNotContain(RecursiveThisIsTheOtherField.class.getSimpleName());
    }

    @Test
    void exceptionMessage() {
        assertThatThrownBy(() -> valueProvider.provideOrThrow(TWOSTEP_NODE_A_TAG, Attributes.empty()))
                .isInstanceOf(RecursionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains(TwoStepNodeA.class.getSimpleName(), TwoStepNodeB.class.getSimpleName());
    }

    @Test
    void skipStaticFinal() {
        valueProvider.provideOrThrow(new TypeTag(StaticFinalContainer.class), Attributes.empty());
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
