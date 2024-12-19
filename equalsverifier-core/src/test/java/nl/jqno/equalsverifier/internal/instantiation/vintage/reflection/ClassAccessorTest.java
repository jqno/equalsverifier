package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeB;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class ClassAccessorTest {

    private LinkedHashSet<TypeTag> empty;
    private Objenesis objenesis;
    private FactoryCache factoryCache;
    private VintageValueProvider valueProvider;
    private ClassAccessor<PointContainer> pointContainerAccessor;

    @BeforeEach
    void setup() {
        empty = new LinkedHashSet<>();
        objenesis = new ObjenesisStd();
        factoryCache = JavaApiPrefabValues.build();
        valueProvider = new VintageValueProvider(factoryCache, objenesis);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, valueProvider, objenesis);
    }

    @Test
    void getRedObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getRedObject(TypeTag.NULL, empty));
    }

    @Test
    @SuppressWarnings("rawtypes")
    void getRedObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor =
                ClassAccessor.of(GenericTypeVariableListContainer.class, valueProvider, objenesis);
        GenericTypeVariableListContainer foo = accessor
                .getRedObject(new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class)), empty);
        assertThat(foo.tList.get(0).getClass()).isEqualTo(String.class);
    }

    @Test
    void getRedAccessor() {
        PointContainer foo = pointContainerAccessor.getRedObject(TypeTag.NULL, empty);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getRedAccessor(TypeTag.NULL, empty);
        assertThat(objectAccessor.get()).isEqualTo(foo);
    }

    @Test
    void getBlueObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getBlueObject(TypeTag.NULL, empty));
    }

    @Test
    @SuppressWarnings("rawtypes")
    void getBlueObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor =
                ClassAccessor.of(GenericTypeVariableListContainer.class, valueProvider, objenesis);
        GenericTypeVariableListContainer foo = accessor
                .getBlueObject(new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class)), empty);
        assertThat(foo.tList.get(0).getClass()).isEqualTo(String.class);
    }

    @Test
    void getBlueAccessor() {
        PointContainer foo = pointContainerAccessor.getBlueObject(TypeTag.NULL, empty);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getBlueAccessor(TypeTag.NULL, empty);
        assertThat(objectAccessor.get()).isEqualTo(foo);
    }

    @Test
    void redAndBlueNotEqual() {
        PointContainer red = pointContainerAccessor.getRedObject(TypeTag.NULL, empty);
        PointContainer blue = pointContainerAccessor.getBlueObject(TypeTag.NULL, empty);
        assertThat(blue).isNotEqualTo(red);
    }

    @Test
    void instantiateAllTypes() {
        ClassAccessor.of(AllTypesContainer.class, valueProvider, objenesis).getRedObject(TypeTag.NULL, empty);
    }

    @Test
    void instantiateArrayTypes() {
        ClassAccessor.of(AllArrayTypesContainer.class, valueProvider, objenesis).getRedObject(TypeTag.NULL, empty);
    }

    @Test
    void instantiateRecursiveTypeUsingPrefabValue() {
        factoryCache.put(TwoStepNodeB.class, values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB()));
        valueProvider = new VintageValueProvider(factoryCache, objenesis);
        ClassAccessor.of(TwoStepNodeA.class, valueProvider, objenesis).getRedObject(TypeTag.NULL, empty);
    }

    @Test
    void instantiateInterfaceField() {
        ClassAccessor.of(InterfaceContainer.class, valueProvider, objenesis).getRedObject(TypeTag.NULL, empty);
    }

    @Test
    void instantiateAbstractClassField() {
        ClassAccessor.of(AbstractClassContainer.class, valueProvider, objenesis).getRedObject(TypeTag.NULL, empty);
    }

    @Test
    void anInvalidTypeShouldNotThrowAnExceptionUponCreation() {
        ClassAccessor.of(null, valueProvider, objenesis);
    }

    private void assertObjectHasNoNullFields(PointContainer foo) {
        assertThat(foo).isNotNull();
        assertThat(foo.getPoint()).isNotNull();
    }

    static class MethodContainer {

        public void m() {}

        protected void m_protected() {}
    }

    static class ChildOfMethodContainer extends MethodContainer {}
}
