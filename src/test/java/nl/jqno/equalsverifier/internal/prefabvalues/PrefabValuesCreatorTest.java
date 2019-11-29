package nl.jqno.equalsverifier.internal.prefabvalues;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.Assert.*;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.FactoryCacheFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Enum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.Before;
import org.junit.Test;

public class PrefabValuesCreatorTest extends ExpectedExceptionTestBase {
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag ENUM_TAG = new TypeTag(Enum.class);
    private static final TypeTag ONE_ELT_ENUM_TAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag EMPTY_ENUM_TAG = new TypeTag(EmptyEnum.class);
    private static final TypeTag NODE_TAG = new TypeTag(Node.class);
    private static final TypeTag NODE_ARRAY_TAG = new TypeTag(NodeArray.class);
    private static final TypeTag TWOSTEP_NODE_A_TAG = new TypeTag(TwoStepNodeA.class);
    private static final TypeTag TWOSTEP_NODE_ARRAY_A_TAG = new TypeTag(TwoStepNodeArrayA.class);

    private FactoryCache factoryCache;
    private PrefabValues prefabValues;

    @Before
    public void setup() {
        factoryCache = FactoryCacheFactory.withPrimitiveFactories();
        prefabValues = new PrefabValues(factoryCache);
    }

    @Test
    public void simple() {
        Point red = prefabValues.giveRed(POINT_TAG);
        Point black = prefabValues.giveBlack(POINT_TAG);
        assertFalse(red.equals(black));
    }

    @Test
    public void createSecondTimeIsNoOp() {
        Point red = prefabValues.giveRed(POINT_TAG);
        Point black = prefabValues.giveBlack(POINT_TAG);

        assertSame(red, prefabValues.giveRed(POINT_TAG));
        assertSame(black, prefabValues.giveBlack(POINT_TAG));
    }

    @Test
    public void createEnum() {
        assertNotNull(prefabValues.giveRed(ENUM_TAG));
        assertNotNull(prefabValues.giveBlack(ENUM_TAG));
    }

    @Test
    public void createOneElementEnum() {
        assertNotNull(prefabValues.giveRed(ONE_ELT_ENUM_TAG));
        assertNotNull(prefabValues.giveBlack(ONE_ELT_ENUM_TAG));
    }

    @Test
    public void createEmptyEnum() {
        assertNull(prefabValues.giveRed(EMPTY_ENUM_TAG));
        assertNull(prefabValues.giveBlack(EMPTY_ENUM_TAG));
    }

    @Test
    public void oneStepRecursiveType() {
        factoryCache.put(Node.class, values(new Node(), new Node(), new Node()));
        prefabValues = new PrefabValues(factoryCache);
        prefabValues.giveRed(NODE_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveType() {
        expectException(RecursionException.class);
        prefabValues.giveRed(NODE_TAG);
    }

    @Test
    public void oneStepRecursiveArrayType() {
        factoryCache.put(
                NodeArray.class, values(new NodeArray(), new NodeArray(), new NodeArray()));
        prefabValues = new PrefabValues(factoryCache);
        prefabValues.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveArrayType() {
        expectException(RecursionException.class);
        prefabValues.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    public void addTwoStepRecursiveType() {
        factoryCache.put(
                TwoStepNodeB.class,
                values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB()));
        prefabValues = new PrefabValues(factoryCache);
        prefabValues.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveType() {
        expectException(RecursionException.class);
        prefabValues.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void twoStepRecursiveArrayType() {
        factoryCache.put(
                TwoStepNodeArrayB.class,
                values(new TwoStepNodeArrayB(), new TwoStepNodeArrayB(), new TwoStepNodeArrayB()));
        prefabValues = new PrefabValues(factoryCache);
        prefabValues.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveArrayType() {
        expectException(RecursionException.class);
        prefabValues.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    public void sameClassTwiceButNoRecursion() {
        prefabValues.giveRed(new TypeTag(NotRecursiveA.class));
    }

    @Test
    public void recursiveWithAnotherFieldFirst() {
        expectException(RecursionException.class);
        expectDescription(RecursiveWithAnotherFieldFirst.class.getSimpleName());
        expectNotInDescription(RecursiveThisIsTheOtherField.class.getSimpleName());
        prefabValues.giveRed(new TypeTag(RecursiveWithAnotherFieldFirst.class));
    }

    @Test
    public void exceptionMessage() {
        expectException(RecursionException.class);
        expectDescription(TwoStepNodeA.class.getSimpleName());
        expectDescription(TwoStepNodeB.class.getSimpleName());
        prefabValues.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void skipStaticFinal() {
        prefabValues.giveRed(new TypeTag(StaticFinalContainer.class));
    }

    static class StaticFinalContainer {
        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
