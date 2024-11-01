package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

public class VintageValueProviderCreatorTest {

    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag NODE_TAG = new TypeTag(Node.class);
    private static final TypeTag NODE_ARRAY_TAG = new TypeTag(NodeArray.class);
    private static final TypeTag TWOSTEP_NODE_A_TAG = new TypeTag(TwoStepNodeA.class);
    private static final TypeTag TWOSTEP_NODE_ARRAY_A_TAG = new TypeTag(TwoStepNodeArrayA.class);

    private PrefabValueProvider prefabs;
    private VintageValueProvider valueProvider;

    @BeforeEach
    public void setup() {
        prefabs = new PrefabValueProvider();
        valueProvider = TestValueProviders.vintage(prefabs, new ObjenesisStd());
    }

    @Test
    public void simple() {
        Point red = valueProvider.giveRed(POINT_TAG);
        Point blue = valueProvider.giveBlue(POINT_TAG);
        assertFalse(red.equals(blue));
    }

    @Test
    public void createSecondTimeIsNoOp() {
        Point red = valueProvider.giveRed(POINT_TAG);
        Point blue = valueProvider.giveBlue(POINT_TAG);

        assertSame(red, valueProvider.giveRed(POINT_TAG));
        assertSame(blue, valueProvider.giveBlue(POINT_TAG));
    }

    @Test
    public void oneStepRecursiveType() {
        prefabs.register(Node.class, null, Tuple.of(new Node(), new Node(), new Node()));
        valueProvider.giveRed(NODE_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveType() {
        ExpectedException
            .when(() -> valueProvider.giveRed(NODE_TAG))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void oneStepRecursiveArrayType() {
        prefabs.register(
            NodeArray.class,
            null,
            Tuple.of(new NodeArray(), new NodeArray(), new NodeArray())
        );
        valueProvider.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveArrayType() {
        ExpectedException
            .when(() -> valueProvider.giveRed(NODE_ARRAY_TAG))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void addTwoStepRecursiveType() {
        prefabs.register(
            TwoStepNodeB.class,
            null,
            Tuple.of(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB())
        );
        valueProvider.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveType() {
        ExpectedException
            .when(() -> valueProvider.giveRed(TWOSTEP_NODE_A_TAG))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void twoStepRecursiveArrayType() {
        prefabs.register(
            TwoStepNodeArrayB.class,
            null,
            Tuple.of(new TwoStepNodeArrayB(), new TwoStepNodeArrayB(), new TwoStepNodeArrayB())
        );
        valueProvider.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveArrayType() {
        ExpectedException
            .when(() -> valueProvider.giveRed(TWOSTEP_NODE_ARRAY_A_TAG))
            .assertThrows(RecursionException.class);
    }

    @Test
    public void sameClassTwiceButNoRecursion() {
        valueProvider.giveRed(new TypeTag(NotRecursiveA.class));
    }

    @Test
    public void recursiveWithAnotherFieldFirst() {
        ExpectedException
            .when(() -> valueProvider.giveRed(new TypeTag(RecursiveWithAnotherFieldFirst.class)))
            .assertThrows(RecursionException.class)
            .assertDescriptionContains(RecursiveWithAnotherFieldFirst.class.getSimpleName())
            .assertDescriptionDoesNotContain(RecursiveThisIsTheOtherField.class.getSimpleName());
    }

    @Test
    public void exceptionMessage() {
        ExpectedException
            .when(() -> valueProvider.giveRed(TWOSTEP_NODE_A_TAG))
            .assertThrows(RecursionException.class)
            .assertDescriptionContains(
                TwoStepNodeA.class.getSimpleName(),
                TwoStepNodeB.class.getSimpleName()
            );
    }

    @Test
    public void skipStaticFinal() {
        valueProvider.giveRed(new TypeTag(StaticFinalContainer.class));
    }

    static class StaticFinalContainer {

        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
