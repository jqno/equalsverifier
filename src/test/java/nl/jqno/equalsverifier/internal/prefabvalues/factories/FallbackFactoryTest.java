package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FallbackFactoryTest extends ExpectedExceptionTestBase {
    private FallbackFactory<?> factory;
    private PrefabValues prefabValues;
    private LinkedHashSet<TypeTag> typeStack;

    @Before
    public void setUp() {
        factory = new FallbackFactory<>();
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(int.class, values(42, 1337, 42));
        prefabValues = new PrefabValues(factoryCache);
        typeStack = new LinkedHashSet<>();
    }

    @Test
    public void giveNullInsteadOfEmptyEnum() {
        assertCorrectTuple(EmptyEnum.class, null, null);
    }

    @Test
    public void giveOneElementEnum() {
        assertCorrectTuple(OneElementEnum.class, OneElementEnum.ONE, OneElementEnum.ONE);
    }

    @Test
    public void giveMultiElementEnum() {
        assertCorrectTuple(TwoElementEnum.class, TwoElementEnum.ONE, TwoElementEnum.TWO);
    }

    @Test
    public void giveArray() {
        Tuple<?> tuple = factory.createValues(new TypeTag(int[].class), prefabValues, typeStack);
        assertArrayEquals(new int[]{ 42 }, (int[])tuple.getRed());
        assertArrayEquals(new int[]{ 1337 }, (int[])tuple.getBlack());
    }

    @Test
    public void giveClassWithFields() {
        assertCorrectTuple(IntContainer.class, new IntContainer(42, 42), new IntContainer(1337, 1337));
        // Assert that static fields are untouched
        assertEquals(-100, IntContainer.staticI);
        assertEquals(-10, IntContainer.STATIC_FINAL_I);
    }

    @Test
    public void dontGiveRecursiveClass() {
        expectException(RecursionException.class);
        factory.createValues(new TypeTag(Node.class), prefabValues, typeStack);
    }

    @Test
    public void dontGiveTwoStepRecursiveClass() {
        expectException(RecursionException.class);
        expectDescription("TwoStepNodeA", "TwoStepNodeB");
        factory.createValues(new TypeTag(TwoStepNodeA.class), prefabValues, typeStack);
    }

    @Test
    public void dontGiveRecursiveArray() {
        expectException(RecursionException.class);
        factory.createValues(new TypeTag(NodeArray.class), prefabValues, typeStack);
    }

    private <T> void assertCorrectTuple(Class<T> type, T expectedRed, T expectedBlack) {
        Tuple<?> tuple = factory.createValues(new TypeTag(type), prefabValues, typeStack);
        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }

    private static final class IntContainer {
        @SuppressWarnings("unused") private static int staticI = -100;
        private static final int STATIC_FINAL_I = -10;
        @SuppressWarnings("unused") private final int finalI;
        @SuppressWarnings("unused") private int i;

        public IntContainer(int finalI, int i) { this.finalI = finalI; this.i = i; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
