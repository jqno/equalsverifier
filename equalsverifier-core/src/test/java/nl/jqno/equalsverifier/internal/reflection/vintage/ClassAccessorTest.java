package nl.jqno.equalsverifier.internal.reflection.vintage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.PrefabValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeB;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.GenericTypeVariableListContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.InterfaceContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class ClassAccessorTest {

    private Attributes empty;
    private Objenesis objenesis;
    private PrefabValueProvider prefabs;
    private VintageValueProvider valueProvider;
    private ClassAccessor<PointContainer> pointContainerAccessor;

    @BeforeEach
    public void setup() {
        empty = Attributes.unlabeled();
        objenesis = new ObjenesisStd();
        prefabs = new PrefabValueProvider();
        valueProvider = TestValueProviders.vintage(prefabs, objenesis);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, valueProvider, objenesis);
    }

    @Test
    public void getRedObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getRedObject(TypeTag.NULL, empty));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getRedObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor = ClassAccessor.of(
            GenericTypeVariableListContainer.class,
            valueProvider,
            objenesis
        );
        GenericTypeVariableListContainer foo = accessor.getRedObject(
            new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class)),
            empty
        );
        assertEquals(String.class, foo.tList.get(0).getClass());
    }

    @Test
    public void getRedAccessor() {
        PointContainer foo = pointContainerAccessor.getRedObject(TypeTag.NULL, empty);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getRedAccessor(
            TypeTag.NULL,
            empty
        );
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void getBlueObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getBlueObject(TypeTag.NULL, empty));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getBlueObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor = ClassAccessor.of(
            GenericTypeVariableListContainer.class,
            valueProvider,
            objenesis
        );
        GenericTypeVariableListContainer foo = accessor.getBlueObject(
            new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class)),
            empty
        );
        assertEquals(String.class, foo.tList.get(0).getClass());
    }

    @Test
    public void getBlueAccessor() {
        PointContainer foo = pointContainerAccessor.getBlueObject(TypeTag.NULL, empty);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getBlueAccessor(
            TypeTag.NULL,
            empty
        );
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void redAndBlueNotEqual() {
        PointContainer red = pointContainerAccessor.getRedObject(TypeTag.NULL, empty);
        PointContainer blue = pointContainerAccessor.getBlueObject(TypeTag.NULL, empty);
        assertFalse(red.equals(blue));
    }

    @Test
    public void instantiateRecursiveTypeUsingPrefabValue() {
        prefabs.register(
            TwoStepNodeB.class,
            null,
            Tuple.of(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB())
        );
        ClassAccessor
            .of(TwoStepNodeA.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void instantiateInterfaceField() {
        ClassAccessor
            .of(InterfaceContainer.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void instantiateAbstractClassField() {
        ClassAccessor
            .of(AbstractClassContainer.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void anInvalidTypeShouldNotThrowAnExceptionUponCreation() {
        ClassAccessor.of(null, valueProvider, objenesis);
    }

    private void assertObjectHasNoNullFields(PointContainer foo) {
        assertNotNull(foo);
        assertNotNull(foo.getPoint());
    }

    static class MethodContainer {

        public void m() {}

        protected void m_protected() {}
    }

    static class ChildOfMethodContainer extends MethodContainer {}
}
