package equalsverifier.reflection;

import equalsverifier.prefabvalues.FactoryCache;
import equalsverifier.prefabvalues.JavaApiPrefabValues;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabvalues.PrefabValues;
import equalsverifier.gentype.TypeTag;
import equalsverifier.reflection.annotations.AnnotationCache;
import equalsverifier.reflection.annotations.AnnotationCacheBuilder;
import equalsverifier.reflection.annotations.SupportedAnnotations;
import equalsverifier.testhelpers.annotations.NonNull;
import equalsverifier.testhelpers.types.ColorPoint3D;
import equalsverifier.testhelpers.types.Point3D;
import equalsverifier.testhelpers.types.PointContainer;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.TwoStepNodeB;
import equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static equalsverifier.prefabvalues.factories.Factories.values;
import static org.junit.Assert.*;

public class ClassAccessorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private FactoryCache factoryCache;
    private PrefabAbstract prefabAbstract;
    private ClassAccessor<PointContainer> pointContainerAccessor;
    private ClassAccessor<AbstractEqualsAndHashCode> abstractEqualsAndHashCodeAccessor;
    private ClassAccessor<DefaultValues> defaultValuesClassAccessor;
    private AnnotationCache defaultValuesAnnotationCache;

    @Before
    public void setup() {
        factoryCache = JavaApiPrefabValues.build();
        prefabAbstract = new PrefabValues(factoryCache);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, prefabAbstract);
        abstractEqualsAndHashCodeAccessor = ClassAccessor.of(AbstractEqualsAndHashCode.class, prefabAbstract);
        defaultValuesClassAccessor = ClassAccessor.of(DefaultValues.class, prefabAbstract);

        defaultValuesAnnotationCache = new AnnotationCache();
        new AnnotationCacheBuilder(SupportedAnnotations.values(), new HashSet<>()).build(DefaultValues.class, defaultValuesAnnotationCache);
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
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabAbstract);
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
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabAbstract);
        assertFalse(accessor.declaresEquals());
    }

    @Test
    public void declaresHashCode() {
        assertTrue(pointContainerAccessor.declaresHashCode());
        assertTrue(abstractEqualsAndHashCodeAccessor.declaresHashCode());
    }

    @Test
    public void doesNotDeclareHashCode() {
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabAbstract);
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
        ClassAccessor<NoFieldsSubWithFields> accessor = ClassAccessor.of(NoFieldsSubWithFields.class, prefabAbstract);
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
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabAbstract);
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
        ClassAccessor<GenericTypeVariableListContainer> accessor =
                ClassAccessor.of(GenericTypeVariableListContainer.class, prefabAbstract);
        GenericTypeVariableListContainer foo =
                accessor.getRedObject(new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class)));
        assertEquals(String.class, foo.tList.get(0).getClass());
    }

    @Test
    public void getRedAccessor() {
        PointContainer foo = pointContainerAccessor.getRedObject(TypeTag.NULL);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getRedAccessor(TypeTag.NULL);
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void getBlackObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getBlackObject(TypeTag.NULL));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getBlackObjectGeneric() {
        ClassAccessor<GenericTypeVariableListContainer> accessor =
                ClassAccessor.of(GenericTypeVariableListContainer.class, prefabAbstract);
        GenericTypeVariableListContainer foo =
                accessor.getBlackObject(new TypeTag(GenericTypeVariableListContainer.class, new TypeTag(String.class)));
        assertEquals(String.class, foo.tList.get(0).getClass());
    }

    @Test
    public void getBlackAccessor() {
        PointContainer foo = pointContainerAccessor.getBlackObject(TypeTag.NULL);
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getBlackAccessor(TypeTag.NULL);
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void redAndBlackNotEqual() {
        PointContainer red = pointContainerAccessor.getRedObject(TypeTag.NULL);
        PointContainer black = pointContainerAccessor.getBlackObject(TypeTag.NULL);
        assertFalse(red.equals(black));
    }

    @Test
    public void getDefaultValuesAccessor_withNoNonnullValues() {
        ObjectAccessor<DefaultValues> objectAccessor =
            defaultValuesClassAccessor.getDefaultValuesAccessor(TypeTag.NULL, new HashSet<>(), defaultValuesAnnotationCache);
        DefaultValues foo = objectAccessor.get();
        assertEquals(null, foo.s);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesAccessor_withOneNonnullValue() {
        Set<String> nonnullFields = new HashSet<>();
        nonnullFields.add("s");
        ObjectAccessor<DefaultValues> objectAccessor =
            defaultValuesClassAccessor.getDefaultValuesAccessor(TypeTag.NULL, nonnullFields, defaultValuesAnnotationCache);
        DefaultValues foo = objectAccessor.get();
        assertFalse(foo.s == null);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesAccessor_objectContent() {
        ClassAccessor<DefaultValues> accessor = ClassAccessor.of(DefaultValues.class, prefabAbstract);
        DefaultValues foo = accessor.getDefaultValuesAccessor(TypeTag.NULL, new HashSet<>(), defaultValuesAnnotationCache).get();
        assertEquals(0, foo.i);
        assertEquals(null, foo.s);
        assertFalse(foo.t == null);
    }

    @Test
    public void instantiateAllTypes() {
        ClassAccessor.of(AllTypesContainer.class, prefabAbstract).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateArrayTypes() {
        ClassAccessor.of(AllArrayTypesContainer.class, prefabAbstract).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateRecursiveTypeUsingPrefabValue() {
        factoryCache.put(TwoStepNodeB.class, values(new TwoStepNodeB(), new TwoStepNodeB(), new TwoStepNodeB()));
        prefabAbstract = new PrefabValues(factoryCache);
        ClassAccessor.of(TwoStepNodeA.class, prefabAbstract).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateInterfaceField() {
        ClassAccessor.of(InterfaceContainer.class, prefabAbstract).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateAbstractClassField() {
        ClassAccessor.of(AbstractClassContainer.class, prefabAbstract).getRedObject(TypeTag.NULL);
    }

    @Test
    public void anInvalidTypeShouldNotThrowAnExceptionUponCreation() {
        ClassAccessor.of(null, prefabAbstract);
    }

    private void assertObjectHasNoNullFields(PointContainer foo) {
        assertNotNull(foo);
        assertNotNull(foo.getPoint());
    }

    static class DefaultValues {
        int i;
        String s;
        @NonNull String t;
    }

    // CHECKSTYLE: ignore DeclarationOrder for 3 lines.
    // Generated at runtime, so we don't actually have to put a class in the default package.
    private static final String DEFAULT_PACKAGE_NAME = "DefaultPackage";
    private static final String DEFAULT_PACKAGE =
            "\npublic final class DefaultPackage {" +
            "\n    private final int i;" +
            "\n    " +
            "\n    public DefaultPackage(int i) {" +
            "\n        this.i = i;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof DefaultPackage)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        return i == ((DefaultPackage)obj).i;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return i;" +
            "\n    }" +
            "\n}";
}
