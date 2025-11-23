package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.*;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.PointContainer;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.TwoStepNodeB;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.GenericTypeVariableContainerContainer;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.InterfaceContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class ClassAccessorTest {

    private Objenesis objenesis;
    private LinkedHashSet<TypeTag> empty;
    private UserPrefabValueCaches prefabs;
    private UserPrefabValueProvider prefabValueProvider;
    private FactoryCache factoryCache;
    private VintageValueProvider valueProvider;
    private ClassAccessor<PointContainer> pointContainerAccessor;

    @BeforeEach
    void setup() {
        empty = new LinkedHashSet<>();
        prefabs = new UserPrefabValueCaches();
        prefabValueProvider = new UserPrefabValueProvider(prefabs);
        var chain = new ChainedValueProvider(prefabValueProvider, new BuiltinPrefabValueProvider());
        factoryCache = new FactoryCache();
        objenesis = new ObjenesisStd();
        valueProvider = new VintageValueProvider(chain, factoryCache, objenesis);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, valueProvider, objenesis);
    }

    @Test
    void getRedObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getRedObject(TypeTag.NULL, empty));
    }

    @Test
    @SuppressWarnings("rawtypes")
    void getRedObjectGeneric() {
        ClassAccessor<GenericTypeVariableContainerContainer> accessor =
                ClassAccessor.of(GenericTypeVariableContainerContainer.class, valueProvider, objenesis);
        GenericTypeVariableContainerContainer foo = accessor
                .getRedObject(
                    new TypeTag(GenericTypeVariableContainerContainer.class, new TypeTag(String.class)),
                    empty);
        assertThat(foo.container.t.getClass()).isEqualTo(String.class);
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
        ClassAccessor<GenericTypeVariableContainerContainer> accessor =
                ClassAccessor.of(GenericTypeVariableContainerContainer.class, valueProvider, objenesis);
        GenericTypeVariableContainerContainer foo = accessor
                .getBlueObject(
                    new TypeTag(GenericTypeVariableContainerContainer.class, new TypeTag(String.class)),
                    empty);
        assertThat(foo.container.t.getClass()).isEqualTo(String.class);
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
    void instantiateRecursiveTypeUsingPrefabValue() {
        prefabs
                .register(
                    TwoStepNodeB.class,
                    new TwoStepNodeB(null),
                    new TwoStepNodeB(new TwoStepNodeA(null)),
                    new TwoStepNodeB(null));
        valueProvider = new VintageValueProvider(prefabValueProvider, factoryCache, objenesis);
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
