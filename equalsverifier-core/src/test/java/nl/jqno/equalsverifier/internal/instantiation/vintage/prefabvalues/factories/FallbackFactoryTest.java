package nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.values;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
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
        valueProvider = new VintageValueProvider(factoryCache, objenesis);
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
        assertThat((int[]) tuple.getRed()).containsExactly(new int[] { 42 });
        assertThat((int[]) tuple.getBlue()).containsExactly(new int[] { 1337 });
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

        assertThat(tuple.getRedCopy()).isEqualTo(tuple.getRed()).isNotSameAs(tuple.getRed());
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
                .assertDescriptionContains("TwoStepNodeA", "TwoStepNodeB");
    }

    @Test
    void dontGiveRecursiveArray() {
        ExpectedException
                .when(() -> factory.createValues(new TypeTag(NodeArray.class), valueProvider, typeStack))
                .assertThrows(RecursionException.class);
    }

    private <T> void assertCorrectTuple(Class<T> type, T expectedRed, T expectedBlue) {
        Tuple<?> tuple = factory.createValues(new TypeTag(type), valueProvider, typeStack);
        assertThat(tuple.getRed()).isEqualTo(expectedRed);
        assertThat(tuple.getBlue()).isEqualTo(expectedBlue);
        assertThat(tuple.getRedCopy()).isEqualTo(expectedRed);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
