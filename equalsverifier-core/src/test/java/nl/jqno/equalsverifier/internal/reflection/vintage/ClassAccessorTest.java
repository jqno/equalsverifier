package nl.jqno.equalsverifier.internal.reflection.vintage;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.CachedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeB;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class ClassAccessorTest {

    private Attributes empty;
    private Objenesis objenesis;
    private CachedValueProvider cache;
    private FactoryCache factoryCache;
    private VintageValueProvider valueProvider;
    private ClassAccessor<PointContainer> pointContainerAccessor;

    @BeforeEach
    public void setup() {
        empty = Attributes.unlabeled();
        objenesis = new ObjenesisStd();
        cache = new CachedValueProvider();
        factoryCache = JavaApiPrefabValues.build();
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        pointContainerAccessor =
            new ClassAccessor<>(PointContainer.class, valueProvider, objenesis);
    }

    @Test
    public void getRedObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getRedObject(TypeTag.NULL, empty));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getRedObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor = new ClassAccessor<>(
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
    public void getBlueObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getBlueObject(TypeTag.NULL, empty));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getBlueObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor = new ClassAccessor<>(
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
    public void redAndBlueNotEqual() {
        PointContainer red = pointContainerAccessor.getRedObject(TypeTag.NULL, empty);
        PointContainer blue = pointContainerAccessor.getBlueObject(TypeTag.NULL, empty);
        assertFalse(red.equals(blue));
    }

    @Test
    public void instantiateAllTypes() {
        new ClassAccessor<>(AllTypesContainer.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void instantiateArrayTypes() {
        new ClassAccessor<>(AllArrayTypesContainer.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void instantiateRecursiveTypeUsingPrefabValue() {
        factoryCache.put(
            TwoStepNodeB.class,
            values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB())
        );
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        new ClassAccessor<>(TwoStepNodeA.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void instantiateInterfaceField() {
        new ClassAccessor<>(InterfaceContainer.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void instantiateAbstractClassField() {
        new ClassAccessor<>(AbstractClassContainer.class, valueProvider, objenesis)
            .getRedObject(TypeTag.NULL, empty);
    }

    @Test
    public void anInvalidTypeShouldNotThrowAnExceptionUponCreation() {
        new ClassAccessor<>(null, valueProvider, objenesis);
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
