package nl.jqno.equalsverifier.internal.reflection.vintage;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.jqno.equalsverifier.internal.prefabvalues.*;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeB;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassAccessorTest {

    private FactoryCache factoryCache;
    private PrefabValues prefabValues;
    private ClassAccessor<PointContainer> pointContainerAccessor;

    @BeforeEach
    public void setup() {
        factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, prefabValues);
    }

    @Test
    public void getRedObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getRedObject(TypeTag.NULL));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getRedObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor = ClassAccessor.of(
            GenericTypeVariableListContainer.class,
            prefabValues
        );
        GenericTypeVariableListContainer foo = accessor.getRedObject(
            new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class))
        );
        assertEquals(String.class, foo.tList.get(0).getClass());
    }

    @Test
    public void getRedAccessor() {
        PointContainer foo = pointContainerAccessor.getRedObject(TypeTag.NULL);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getRedAccessor(
            TypeTag.NULL
        );
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void getBlueObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getBlueObject(TypeTag.NULL));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getBlueObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor = ClassAccessor.of(
            GenericTypeVariableListContainer.class,
            prefabValues
        );
        GenericTypeVariableListContainer foo = accessor.getBlueObject(
            new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class))
        );
        assertEquals(String.class, foo.tList.get(0).getClass());
    }

    @Test
    public void getBlueAccessor() {
        PointContainer foo = pointContainerAccessor.getBlueObject(TypeTag.NULL);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getBlueAccessor(
            TypeTag.NULL
        );
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void redAndBlueNotEqual() {
        PointContainer red = pointContainerAccessor.getRedObject(TypeTag.NULL);
        PointContainer blue = pointContainerAccessor.getBlueObject(TypeTag.NULL);
        assertFalse(red.equals(blue));
    }

    @Test
    public void instantiateAllTypes() {
        ClassAccessor.of(AllTypesContainer.class, prefabValues).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateArrayTypes() {
        ClassAccessor.of(AllArrayTypesContainer.class, prefabValues).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateRecursiveTypeUsingPrefabValue() {
        factoryCache.put(
            TwoStepNodeB.class,
            values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB())
        );
        prefabValues = new PrefabValues(factoryCache);
        ClassAccessor.of(TwoStepNodeA.class, prefabValues).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateInterfaceField() {
        ClassAccessor.of(InterfaceContainer.class, prefabValues).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateAbstractClassField() {
        ClassAccessor.of(AbstractClassContainer.class, prefabValues).getRedObject(TypeTag.NULL);
    }

    @Test
    public void anInvalidTypeShouldNotThrowAnExceptionUponCreation() {
        ClassAccessor.of(null, prefabValues);
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
