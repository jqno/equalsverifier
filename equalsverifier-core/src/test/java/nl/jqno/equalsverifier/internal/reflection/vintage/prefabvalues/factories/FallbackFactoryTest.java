package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.PrefabValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class FallbackFactoryTest {

    private FallbackFactory<?> factory;
    private VintageValueProvider valueProvider;
    private Attributes attributes;

    @BeforeEach
    public void setUp() {
        Objenesis objenesis = new ObjenesisStd();
        factory = new FallbackFactory<>(objenesis);
        valueProvider = TestValueProviders.vintage(new PrefabValueProvider(), objenesis);
        attributes = Attributes.unlabeled();
    }

    @Test
    public void giveClassWithFields() {
        assertCorrectTuple(IntContainer.class, new IntContainer(1, 1), new IntContainer(2, 2));
        // Assert that static fields are untouched
        assertEquals(-100, IntContainer.staticI);
        assertEquals(-10, IntContainer.STATIC_FINAL_I);
    }

    @Test
    public void dontGiveTwoStepRecursiveClass() {
        ExpectedException
            .when(() ->
                factory.createValues(new TypeTag(TwoStepNodeA.class), valueProvider, attributes)
            )
            .assertThrows(RecursionException.class)
            .assertDescriptionContains("TwoStepNodeA", "TwoStepNodeB");
    }

    private <T> void assertCorrectTuple(Class<T> type, T expectedRed, T expectedBlue) {
        Tuple<?> tuple = factory.createValues(new TypeTag(type), valueProvider, attributes);
        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlue, tuple.getBlue());
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

        @Override
        public String toString() {
            return "IntContainer: " + finalI + "," + i;
        }
    }
}
