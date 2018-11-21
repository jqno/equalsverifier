package equalsverifier.prefabvalues.factories;

import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabvalues.RecursionException;
import equalsverifier.prefabvalues.FactoryCache;
import equalsverifier.prefabvalues.PrefabValues;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.gentype.TypeTag;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.NodeArray;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;

import static equalsverifier.prefabvalues.factories.Factories.values;
import static equalsverifier.testhelpers.Util.defaultEquals;
import static equalsverifier.testhelpers.Util.defaultHashCode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FallbackFactoryTest extends ExpectedExceptionTestBase {
    private FallbackFactory<?> factory;
    private PrefabAbstract prefabAbstract;
    private LinkedHashSet<TypeTag> typeStack;

    @Before
    public void setUp() {
        factory = new FallbackFactory<>();
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(int.class, values(42, 1337, 42));
        prefabAbstract = new PrefabValues(factoryCache);
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
        Tuple<?> tuple = factory.createValues(new TypeTag(int[].class), prefabAbstract, typeStack);
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
        factory.createValues(new TypeTag(Node.class), prefabAbstract, typeStack);
    }

    @Test
    public void dontGiveTwoStepRecursiveClass() {
        expectException(RecursionException.class);
        expectDescription("TwoStepNodeA", "TwoStepNodeB");
        factory.createValues(new TypeTag(TwoStepNodeA.class), prefabAbstract, typeStack);
    }

    @Test
    public void dontGiveRecursiveArray() {
        expectException(RecursionException.class);
        factory.createValues(new TypeTag(NodeArray.class), prefabAbstract, typeStack);
    }

    private <T> void assertCorrectTuple(Class<T> type, T expectedRed, T expectedBlack) {
        Tuple<?> tuple = factory.createValues(new TypeTag(type), prefabAbstract, typeStack);
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
