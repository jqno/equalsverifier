package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCacheBuilder;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.types.ColorPoint3D;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
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
    private ClassAccessor<AbstractEqualsAndHashCode> abstractEqualsAndHashCodeAccessor;
    private ClassAccessor<DefaultValues> defaultValuesClassAccessor;
    private AnnotationCache defaultValuesAnnotationCache;

    @BeforeEach
    public void setup() {
        factoryCache = JavaApiPrefabValues.build();
        prefabValues = new PrefabValues(factoryCache);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, prefabValues);
        abstractEqualsAndHashCodeAccessor =
            ClassAccessor.of(AbstractEqualsAndHashCode.class, prefabValues);
        defaultValuesClassAccessor = ClassAccessor.of(DefaultValues.class, prefabValues);

        defaultValuesAnnotationCache = new AnnotationCache();
        new AnnotationCacheBuilder(SupportedAnnotations.values(), new HashSet<>())
            .build(DefaultValues.class, defaultValuesAnnotationCache);
    }

    /* Tests the false case. The true case is tested in {@link ClassAccessorCompilerTest}. */
    @Test
    public void isRecord() {
        assertFalse(pointContainerAccessor.isRecord());
    }

    /* Tests the false case. The true case is tested in {@link ClassAccessorSealedTest}. */
    @Test
    public void isSealed() {
        assertFalse(pointContainerAccessor.isSealed());
    }

    @Test
    public void getType() {
        assertSame(PointContainer.class, pointContainerAccessor.getType());
    }

    @Test
    public void declaresField() throws Exception {
        Field field = PointContainer.class.getDeclaredField("point");
        assertTrue(pointContainerAccessor.declaresField(field));
    }

    @Test
    public void doesNotDeclareField() throws Exception {
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabValues);
        Field field = Point3D.class.getDeclaredField("z");
        assertFalse(accessor.declaresField(field));
    }

    @Test
    public void declaresEquals() {
        assertTrue(pointContainerAccessor.declaresEquals());
        assertTrue(abstractEqualsAndHashCodeAccessor.declaresEquals());
    }

    @Test
    public void doesNotDeclareEquals() {
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabValues);
        assertFalse(accessor.declaresEquals());
    }

    @Test
    public void declaresHashCode() {
        assertTrue(pointContainerAccessor.declaresHashCode());
        assertTrue(abstractEqualsAndHashCodeAccessor.declaresHashCode());
    }

    @Test
    public void doesNotDeclareHashCode() {
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabValues);
        assertFalse(accessor.declaresHashCode());
    }

    @Test
    public void equalsIsNotAbstract() {
        assertFalse(pointContainerAccessor.isEqualsAbstract());
    }

    @Test
    public void equalsIsAbstract() {
        assertTrue(abstractEqualsAndHashCodeAccessor.isEqualsAbstract());
    }

    @Test
    public void hashCodeIsNotAbstract() {
        assertFalse(pointContainerAccessor.isHashCodeAbstract());
    }

    @Test
    public void hashCodeIsAbstract() {
        assertTrue(abstractEqualsAndHashCodeAccessor.isHashCodeAbstract());
    }

    @Test
    public void equalsIsInheritedFromObject() {
        ClassAccessor<NoFieldsSubWithFields> accessor = ClassAccessor.of(
            NoFieldsSubWithFields.class,
            prefabValues
        );
        assertTrue(accessor.isEqualsInheritedFromObject());
    }

    @Test
    public void equalsIsNotInheritedFromObject() {
        assertFalse(pointContainerAccessor.isEqualsInheritedFromObject());
    }

    @Test
    public void getSuperAccessorForPojo() {
        ClassAccessor<? super PointContainer> superAccessor = pointContainerAccessor.getSuperAccessor();
        assertEquals(Object.class, superAccessor.getType());
    }

    @Test
    public void getSuperAccessorInHierarchy() {
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabValues);
        ClassAccessor<? super ColorPoint3D> superAccessor = accessor.getSuperAccessor();
        assertEquals(Point3D.class, superAccessor.getType());
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
    public void getDefaultValuesAccessor_withNoNonnullValues() {
        ObjectAccessor<DefaultValues> objectAccessor = defaultValuesClassAccessor.getDefaultValuesAccessor(
            TypeTag.NULL,
            false,
            false,
            new HashSet<>(),
            defaultValuesAnnotationCache
        );
        DefaultValues foo = objectAccessor.get();
        assertNull(foo.s);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesAccessor_withOneNonnullValue() {
        Set<String> nonnullFields = new HashSet<>();
        nonnullFields.add("s");
        ObjectAccessor<DefaultValues> objectAccessor = defaultValuesClassAccessor.getDefaultValuesAccessor(
            TypeTag.NULL,
            false,
            false,
            nonnullFields,
            defaultValuesAnnotationCache
        );
        DefaultValues foo = objectAccessor.get();
        assertNotNull(foo.s);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesAccessor_whenNullWarningIsSuppressed() {
        ObjectAccessor<DefaultValues> objectAccessor = defaultValuesClassAccessor.getDefaultValuesAccessor(
            TypeTag.NULL,
            true,
            false,
            new HashSet<>(),
            defaultValuesAnnotationCache
        );
        DefaultValues foo = objectAccessor.get();
        assertNotNull(foo.s);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesAccessor_whenZeroWarningIsSuppressed() {
        ObjectAccessor<DefaultValues> objectAccessor = defaultValuesClassAccessor.getDefaultValuesAccessor(
            TypeTag.NULL,
            false,
            true,
            new HashSet<>(),
            defaultValuesAnnotationCache
        );
        DefaultValues foo = objectAccessor.get();
        assertNotEquals(0, foo.i);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesAccessor_objectContent() {
        ClassAccessor<DefaultValues> accessor = ClassAccessor.of(DefaultValues.class, prefabValues);
        DefaultValues foo = accessor
            .getDefaultValuesAccessor(
                TypeTag.NULL,
                false,
                false,
                new HashSet<>(),
                defaultValuesAnnotationCache
            )
            .get();
        assertEquals(0, foo.i);
        assertNull(foo.s);
        assertNotNull(foo.t);
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

    static class DefaultValues {

        int i;
        String s;

        @NonNull
        String t;
    }
}
