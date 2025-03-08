package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.Objects;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class FallbackFactoryTest {

    private FallbackFactory<?> factory;
    private VintageValueProvider valueProvider;
    private LinkedHashSet<TypeTag> typeStack;

    @BeforeEach
    void setUp() {
        Objenesis objenesis = new ObjenesisStd();
        factory = new FallbackFactory<>(objenesis);
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(int.class, values(42, 1337, 42));
        var prefabs = new UserPrefabValueProvider();
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        typeStack = new LinkedHashSet<>();
    }

    @Test
    void giveNullInsteadOfEmptyEnum() {
        assertCorrectTuple(EmptyEnum.class, null, null);
    }

    @Test
    void giveOneElementEnum() {
        assertCorrectTuple(OneElementEnum.class, OneElementEnum.ONE, OneElementEnum.ONE);
    }

    @Test
    void giveMultiElementEnum() {
        assertCorrectTuple(TwoElementEnum.class, TwoElementEnum.ONE, TwoElementEnum.TWO);
    }

    @Test
    void giveArray() {
        Tuple<?> tuple = factory.createValues(new TypeTag(int[].class), valueProvider, typeStack);
        assertThat((int[]) tuple.red()).containsExactly(new int[] { 42 });
        assertThat((int[]) tuple.blue()).containsExactly(new int[] { 1337 });
    }

    @Test
    void giveClassWithFields() {
        assertCorrectTuple(IntContainer.class, new IntContainer(42, 42), new IntContainer(1337, 1337));
        // Assert that static fields are untouched
        assertThat(IntContainer.staticI).isEqualTo(-100);
        assertThat(IntContainer.STATIC_FINAL_I).isEqualTo(-10);
    }

    @Test
    void redCopyIsNotSameAsRed() {
        Tuple<?> tuple = factory.createValues(new TypeTag(IntContainer.class), valueProvider, typeStack);

        assertThat(tuple.redCopy()).isEqualTo(tuple.red()).isNotSameAs(tuple.red());
    }

    @Test
    void dontGiveRecursiveClass() {
        ExpectedException
                .when(() -> factory.createValues(new TypeTag(Node.class), valueProvider, typeStack))
                .assertThrows(RecursionException.class);
    }

    @Test
    void dontGiveTwoStepRecursiveClass() {
        ExpectedException
                .when(() -> factory.createValues(new TypeTag(TwoStepNodeA.class), valueProvider, typeStack))
                .assertThrows(RecursionException.class)
                .assertMessageContains("TwoStepNodeA", "TwoStepNodeB");
    }

    @Test
    void dontGiveRecursiveArray() {
        ExpectedException
                .when(() -> factory.createValues(new TypeTag(NodeArray.class), valueProvider, typeStack))
                .assertThrows(RecursionException.class);
    }

    private <T> void assertCorrectTuple(Class<T> type, T expectedRed, T expectedBlue) {
        Tuple<?> tuple = factory.createValues(new TypeTag(type), valueProvider, typeStack);
        assertThat(tuple.red()).isEqualTo(expectedRed);
        assertThat(tuple.blue()).isEqualTo(expectedBlue);
        assertThat(tuple.redCopy()).isEqualTo(expectedRed);
    }

    private static final class IntContainer {

        @SuppressWarnings("unused")
        private static int staticI = -100;

        private static final int STATIC_FINAL_I = -10;

        @SuppressWarnings("unused")
        private final int finalI;

        @SuppressWarnings("unused")
        private int i;

        public IntContainer(int finalI, int i) {
            this.finalI = finalI;
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IntContainer other
                    && Objects.equals(finalI, other.finalI)
                    && Objects.equals(i, other.i);
        }

        @Override
        public int hashCode() {
            return Objects.hash(finalI, i);
        }
    }
}
