/*
 * Copyright 2010-2017 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.internal.reflection;

import nl.jqno.equalsverifier.internal.packageannotation.AnnotatedPackage;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.ConditionalCompiler;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.types.ColorPoint3D;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AnnotatedOuter.AnnotatedMiddle;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AnnotatedOuter.AnnotatedMiddle.AnnotatedInner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.*;
import static org.junit.Assert.*;

public class ClassAccessorTest {

    private static final Set<String> NO_INGORED_ANNOTATIONS = new HashSet<>();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private PrefabValues prefabValues;
    private ClassAccessor<PointContainer> pointContainerAccessor;
    private ClassAccessor<AbstractEqualsAndHashCode> abstractEqualsAndHashCodeAccessor;
    private ClassAccessor<DefaultValues> defaultValuesClassAccessor;

    @Before
    public void setup() {
        prefabValues = new PrefabValues();
        JavaApiPrefabValues.addTo(prefabValues);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
        abstractEqualsAndHashCodeAccessor = ClassAccessor.of(AbstractEqualsAndHashCode.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
        defaultValuesClassAccessor = ClassAccessor.of(DefaultValues.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
    }

    @Test
    public void getType() {
        assertSame(PointContainer.class, pointContainerAccessor.getType());
    }

    @Test
    public void hasAnnotation() {
        ClassAccessor<?> accessor =
                new ClassAccessor<>(AnnotatedWithRuntime.class, prefabValues, TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS, false);
        assertTrue(accessor.hasAnnotation(TYPE_RUNTIME_RETENTION));
        assertFalse(accessor.hasAnnotation(TYPE_CLASS_RETENTION));
    }

    @Test
    public void outerClassHasAnnotation() {
        ClassAccessor<?> accessor =
                new ClassAccessor<>(AnnotatedMiddle.class, prefabValues, TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS, false);
        assertTrue(accessor.outerClassHasAnnotation(TYPE_CLASS_RETENTION));
        assertFalse(accessor.outerClassHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void nestedOuterClassHasAnnotation() {
        ClassAccessor<?> accessor =
                new ClassAccessor<>(AnnotatedInner.class, prefabValues, TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS, false);
        assertTrue(accessor.outerClassHasAnnotation(TYPE_CLASS_RETENTION));
        assertFalse(accessor.outerClassHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void classIsAlreadyOuter() {
        ClassAccessor<?> accessor =
                new ClassAccessor<>(TypeHelper.class, prefabValues, TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS, false);
        assertFalse(accessor.outerClassHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void packageHasAnnotation() {
        ClassAccessor<?> accessor =
                new ClassAccessor<>(AnnotatedPackage.class, prefabValues, TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS, false);
        assertTrue(accessor.packageHasAnnotation(PACKAGE_ANNOTATION));
        assertFalse(accessor.packageHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void packageInfoDoesNotExist() {
        ClassAccessor<?> accessor =
                new ClassAccessor<>(ClassAccessorTest.class, prefabValues, TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS, false);
        assertFalse(accessor.packageHasAnnotation(PACKAGE_ANNOTATION));
    }

    @Test
    public void fieldHasAnnotation() throws NoSuchFieldException {
        ClassAccessor<?> classAccessor =
                new ClassAccessor<>(AnnotatedFields.class, prefabValues, TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS, false);
        Field field = AnnotatedFields.class.getField("runtimeRetention");
        assertTrue(classAccessor.fieldHasAnnotation(field, FIELD_RUNTIME_RETENTION));
        assertFalse(classAccessor.fieldHasAnnotation(field, FIELD_CLASS_RETENTION));
    }

    @Test
    public void classInDefaultPackageDoesntThrowNpe() throws IOException {
        File tempFileLocation = tempFolder.newFolder();
        try (ConditionalCompiler compiler = new ConditionalCompiler(tempFileLocation)) {
            Class<?> defaultPackage = compiler.compile(DEFAULT_PACKAGE_NAME, DEFAULT_PACKAGE);
            ClassAccessor<?> accessor = ClassAccessor.of(defaultPackage, prefabValues, NO_INGORED_ANNOTATIONS, false);
            accessor.packageHasAnnotation(PACKAGE_ANNOTATION);
        }
    }

    @Test
    public void declaresField() throws Exception {
        Field field = PointContainer.class.getDeclaredField("point");
        assertTrue(pointContainerAccessor.declaresField(field));
    }

    @Test
    public void doesNotDeclareField() throws Exception {
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
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
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
        assertFalse(accessor.declaresEquals());
    }

    @Test
    public void declaresHashCode() {
        assertTrue(pointContainerAccessor.declaresHashCode());
        assertTrue(abstractEqualsAndHashCodeAccessor.declaresHashCode());
    }

    @Test
    public void doesNotDeclareHashCode() {
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
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
        ClassAccessor<NoFieldsSubWithFields> accessor = ClassAccessor.of(NoFieldsSubWithFields.class, prefabValues, NO_INGORED_ANNOTATIONS, true);
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
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
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
                ClassAccessor.of(GenericTypeVariableListContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
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
                ClassAccessor.of(GenericTypeVariableListContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
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
        ObjectAccessor<DefaultValues> objectAccessor = defaultValuesClassAccessor.getDefaultValuesAccessor(TypeTag.NULL, new HashSet<String>());
        DefaultValues foo = objectAccessor.get();
        assertEquals(null, foo.s);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesAccessor_withOneNonnullValue() {
        Set<String> nonnullFields = new HashSet<>();
        nonnullFields.add("s");
        ObjectAccessor<DefaultValues> objectAccessor = defaultValuesClassAccessor.getDefaultValuesAccessor(TypeTag.NULL, nonnullFields);
        DefaultValues foo = objectAccessor.get();
        assertFalse(foo.s == null);
        // The rest is tested in getDefaultValuesObject
    }

    @Test
    public void getDefaultValuesObject() {
        ClassAccessor<DefaultValues> accessor = ClassAccessor.of(DefaultValues.class, prefabValues, NO_INGORED_ANNOTATIONS, false);
        DefaultValues foo = accessor.getDefaultValuesObject(TypeTag.NULL);
        assertEquals(0, foo.i);
        assertEquals(null, foo.s);
        assertFalse(foo.t == null);
    }

    @Test
    public void instantiateAllTypes() {
        ClassAccessor.of(AllTypesContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateArrayTypes() {
        ClassAccessor.of(AllArrayTypesContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateRecursiveApiTypes() {
        ClassAccessor.of(RecursiveApiClassesContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateCollectionImplementations() {
        ClassAccessor.of(AllRecursiveCollectionImplementationsContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false)
                .getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateInterfaceField() {
        ClassAccessor.of(InterfaceContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false).getRedObject(TypeTag.NULL);
    }

    @Test
    public void instantiateAbstractClassField() {
        ClassAccessor.of(AbstractClassContainer.class, prefabValues, NO_INGORED_ANNOTATIONS, false).getRedObject(TypeTag.NULL);
    }

    @Test
    public void anInvalidTypeShouldNotThrowAnExceptionUponCreation() {
        ClassAccessor.of(null, prefabValues, NO_INGORED_ANNOTATIONS, false);
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
