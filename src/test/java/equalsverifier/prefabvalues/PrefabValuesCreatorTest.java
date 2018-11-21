package equalsverifier.prefabvalues;

import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.gentype.TypeTag;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.FactoryCacheFactory;
import equalsverifier.testhelpers.types.Point;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.*;
import equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import equalsverifier.testhelpers.types.TypeHelper.Enum;
import equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.Before;
import org.junit.Test;

import static equalsverifier.prefabvalues.factories.Factories.values;
import static org.junit.Assert.*;

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
    private PrefabAbstract prefabAbstract;

    @Before
    public void setup() {
        factoryCache = FactoryCacheFactory.withPrimitiveFactories();
        prefabAbstract = new PrefabValues(factoryCache);
    }

    @Test
    public void simple() {
        Point red = prefabAbstract.giveRed(POINT_TAG);
        Point black = prefabAbstract.giveBlack(POINT_TAG);
        assertFalse(red.equals(black));
    }

    @Test
    public void createSecondTimeIsNoOp() {
        Point red = prefabAbstract.giveRed(POINT_TAG);
        Point black = prefabAbstract.giveBlack(POINT_TAG);

        assertSame(red, prefabAbstract.giveRed(POINT_TAG));
        assertSame(black, prefabAbstract.giveBlack(POINT_TAG));
    }

    @Test
    public void createEnum() {
        assertNotNull(prefabAbstract.giveRed(ENUM_TAG));
        assertNotNull(prefabAbstract.giveBlack(ENUM_TAG));
    }

    @Test
    public void createOneElementEnum() {
        assertNotNull(prefabAbstract.giveRed(ONE_ELT_ENUM_TAG));
        assertNotNull(prefabAbstract.giveBlack(ONE_ELT_ENUM_TAG));
    }

    @Test
    public void createEmptyEnum() {
        assertNull(prefabAbstract.giveRed(EMPTY_ENUM_TAG));
        assertNull(prefabAbstract.giveBlack(EMPTY_ENUM_TAG));
    }

    @Test
    public void oneStepRecursiveType() {
        factoryCache.put(Node.class, values(new Node(), new Node(), new Node()));
        prefabAbstract = new PrefabValues(factoryCache);
        prefabAbstract.giveRed(NODE_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveType() {
        expectException(RecursionException.class);
        prefabAbstract.giveRed(NODE_TAG);
    }

    @Test
    public void oneStepRecursiveArrayType() {
        factoryCache.put(NodeArray.class, values(new NodeArray(), new NodeArray(), new NodeArray()));
        prefabAbstract = new PrefabValues(factoryCache);
        prefabAbstract.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    public void dontAddOneStepRecursiveArrayType() {
        expectException(RecursionException.class);
        prefabAbstract.giveRed(NODE_ARRAY_TAG);
    }

    @Test
    public void addTwoStepRecursiveType() {
        factoryCache.put(TwoStepNodeB.class, values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB()));
        prefabAbstract = new PrefabValues(factoryCache);
        prefabAbstract.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveType() {
        expectException(RecursionException.class);
        prefabAbstract.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void twoStepRecursiveArrayType() {
        factoryCache.put(TwoStepNodeArrayB.class, values(new TwoStepNodeArrayB(), new TwoStepNodeArrayB(), new TwoStepNodeArrayB()));
        prefabAbstract = new PrefabValues(factoryCache);
        prefabAbstract.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    public void dontAddTwoStepRecursiveArrayType() {
        expectException(RecursionException.class);
        prefabAbstract.giveRed(TWOSTEP_NODE_ARRAY_A_TAG);
    }

    @Test
    public void sameClassTwiceButNoRecursion() {
        prefabAbstract.giveRed(new TypeTag(NotRecursiveA.class));
    }

    @Test
    public void recursiveWithAnotherFieldFirst() {
        expectException(RecursionException.class);
        expectDescription(RecursiveWithAnotherFieldFirst.class.getSimpleName());
        expectNotInDescription(RecursiveThisIsTheOtherField.class.getSimpleName());
        prefabAbstract.giveRed(new TypeTag(RecursiveWithAnotherFieldFirst.class));
    }

    @Test
    public void exceptionMessage() {
        expectException(RecursionException.class);
        expectDescription(TwoStepNodeA.class.getSimpleName());
        expectDescription(TwoStepNodeB.class.getSimpleName());
        prefabAbstract.giveRed(TWOSTEP_NODE_A_TAG);
    }

    @Test
    public void skipStaticFinal() {
        prefabAbstract.giveRed(new TypeTag(StaticFinalContainer.class));
    }

    static class StaticFinalContainer {
        public static final StaticFinalContainer X = new StaticFinalContainer();
    }
}
