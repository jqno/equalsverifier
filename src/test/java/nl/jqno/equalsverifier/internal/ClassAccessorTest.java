/*
 * Copyright 2010-2015 Jan Ouwens
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
package nl.jqno.equalsverifier.internal;

import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_CLASS_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_RUNTIME_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.INAPPLICABLE;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.PACKAGE_ANNOTATION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_CLASS_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_RUNTIME_RETENTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import nl.jqno.equalsverifier.JavaApiPrefabValues;
import nl.jqno.equalsverifier.testhelpers.ConditionalCompiler;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.types.ColorPoint3D;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractEqualsAndHashCode;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AllRecursiveCollectionImplementationsContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AnnotatedFields;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AnnotatedOuter.AnnotatedMiddle;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AnnotatedOuter.AnnotatedMiddle.AnnotatedInner;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AnnotatedWithRuntime;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Empty;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.InterfaceContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.NoFieldsSubWithFields;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.RecursiveApiClassesContainer;
import nl.jqno.equalsverifier.internal.packageannotation.AnnotatedPackage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ClassAccessorTest {
    private PrefabValues prefabValues;
    private ClassAccessor<PointContainer> pointContainerAccessor;
    private ClassAccessor<AbstractEqualsAndHashCode> abstractEqualsAndHashCodeAccessor;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setup() {
        prefabValues = new PrefabValues(new StaticFieldValueStash());
        JavaApiPrefabValues.addTo(prefabValues);
        pointContainerAccessor = ClassAccessor.of(PointContainer.class, prefabValues, false);
        abstractEqualsAndHashCodeAccessor = ClassAccessor.of(AbstractEqualsAndHashCode.class, prefabValues, false);
    }

    @Test
    public void getType() {
        assertSame(PointContainer.class, pointContainerAccessor.getType());
    }

    @Test
    public void getPrefabValues() {
        assertSame(prefabValues, pointContainerAccessor.getPrefabValues());
    }

    @Test
    public void hasAnnotation() {
        ClassAccessor<?> accessor = new ClassAccessor<>(AnnotatedWithRuntime.class, prefabValues, TestSupportedAnnotations.values(), false);
        assertTrue(accessor.hasAnnotation(TYPE_RUNTIME_RETENTION));
        assertFalse(accessor.hasAnnotation(TYPE_CLASS_RETENTION));
    }

    @Test
    public void outerClassHasAnnotation() {
        ClassAccessor<?> accessor = new ClassAccessor<>(AnnotatedMiddle.class, prefabValues, TestSupportedAnnotations.values(), false);
        assertTrue(accessor.outerClassHasAnnotation(TYPE_CLASS_RETENTION));
        assertFalse(accessor.outerClassHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void nestedOuterClassHasAnnotation() {
        ClassAccessor<?> accessor = new ClassAccessor<>(AnnotatedInner.class, prefabValues, TestSupportedAnnotations.values(), false);
        assertTrue(accessor.outerClassHasAnnotation(TYPE_CLASS_RETENTION));
        assertFalse(accessor.outerClassHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void classIsAlreadyOuter() {
        ClassAccessor<?> accessor = new ClassAccessor<>(TypeHelper.class, prefabValues, TestSupportedAnnotations.values(), false);
        assertFalse(accessor.outerClassHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void packageHasAnnotation() {
        ClassAccessor<?> accessor = new ClassAccessor<>(AnnotatedPackage.class, prefabValues, TestSupportedAnnotations.values(), false);
        assertTrue(accessor.packageHasAnnotation(PACKAGE_ANNOTATION));
        assertFalse(accessor.packageHasAnnotation(INAPPLICABLE));
    }

    @Test
    public void packageInfoDoesNotExist() {
        ClassAccessor<?> accessor = new ClassAccessor<>(ClassAccessorTest.class, prefabValues, TestSupportedAnnotations.values(), false);
        assertFalse(accessor.packageHasAnnotation(PACKAGE_ANNOTATION));
    }

    @Test
    public void packageDoesNotExist() {
        Class<?> type = Instantiator.of(Object.class).instantiateAnonymousSubclass().getClass();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ClassAccessor<?> accessor = new ClassAccessor(type, prefabValues, TestSupportedAnnotations.values(), false);
        assertFalse(accessor.packageHasAnnotation(PACKAGE_ANNOTATION));
    }

    @Test
    public void fieldHasAnnotation() throws NoSuchFieldException {
        ClassAccessor<?> classAccessor = new ClassAccessor<>(AnnotatedFields.class, prefabValues, TestSupportedAnnotations.values(), false);
        Field field = AnnotatedFields.class.getField("runtimeRetention");
        assertTrue(classAccessor.fieldHasAnnotation(field, FIELD_RUNTIME_RETENTION));
        assertFalse(classAccessor.fieldHasAnnotation(field, FIELD_CLASS_RETENTION));
    }

    @Test
    public void classInDefaultPackageDoesntThrowNPE() throws IOException {
        File tempFileLocation = tempFolder.newFolder();
        ConditionalCompiler compiler = null;
        try {
            compiler = new ConditionalCompiler(tempFileLocation);
            Class<?> defaultPackage = compiler.compile(DEFAULT_PACKAGE_NAME, DEFAULT_PACKAGE);
            ClassAccessor<?> accessor = ClassAccessor.of(defaultPackage, prefabValues, false);
            accessor.packageHasAnnotation(PACKAGE_ANNOTATION);
        }
        finally {
            if (compiler != null) {
                compiler.close();
            }
        }
    }

    @Test
    public void declaresField() throws Exception {
        Field field = PointContainer.class.getDeclaredField("point");
        assertTrue(pointContainerAccessor.declaresField(field));
    }

    @Test
    public void doesNotDeclareField() throws Exception {
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabValues, false);
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
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabValues, false);
        assertFalse(accessor.declaresEquals());
    }

    @Test
    public void declaresHashCode() {
        assertTrue(pointContainerAccessor.declaresHashCode());
        assertTrue(abstractEqualsAndHashCodeAccessor.declaresHashCode());
    }

    @Test
    public void doesNotDeclareHashCode() {
        ClassAccessor<?> accessor = ClassAccessor.of(Empty.class, prefabValues, false);
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
        ClassAccessor<NoFieldsSubWithFields> accessor = ClassAccessor.of(NoFieldsSubWithFields.class, prefabValues, true);
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
        ClassAccessor<ColorPoint3D> accessor = ClassAccessor.of(ColorPoint3D.class, prefabValues, false);
        ClassAccessor<? super ColorPoint3D> superAccessor = accessor.getSuperAccessor();
        assertEquals(Point3D.class, superAccessor.getType());
    }

    @Test
    public void getRedObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getRedObject());
    }

    @Test
    public void getRedAccessor() {
        PointContainer foo = pointContainerAccessor.getRedObject();
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getRedAccessor();
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void getBlackObject() {
        assertObjectHasNoNullFields(pointContainerAccessor.getBlackObject());
    }

    @Test
    public void getBlackAccessor() {
        PointContainer foo = pointContainerAccessor.getBlackObject();
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getBlackAccessor();
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void redAndBlackNotEqual() {
        PointContainer red = pointContainerAccessor.getRedObject();
        PointContainer black = pointContainerAccessor.getBlackObject();
        assertFalse(red.equals(black));
    }

    @Test
    public void getDefaultValuesAccessor() {
        PointContainer foo = pointContainerAccessor.getDefaultValuesObject();
        ObjectAccessor<PointContainer> objectAccessor = pointContainerAccessor.getDefaultValuesAccessor();
        assertEquals(foo, objectAccessor.get());
    }

    @Test
    public void getDefaultValuesObject() {
        ClassAccessor<DefaultValues> accessor = ClassAccessor.of(DefaultValues.class, prefabValues, false);
        DefaultValues foo = accessor.getDefaultValuesObject();
        assertEquals(0, foo.i);
        assertEquals(null, foo.s);
        assertFalse(foo.t == null);
    }

    @Test
    public void instantiateAllTypes() {
        ClassAccessor.of(AllTypesContainer.class, prefabValues, false).getRedObject();
    }

    @Test
    public void instantiateArrayTypes() {
        ClassAccessor.of(AllArrayTypesContainer.class, prefabValues, false).getRedObject();
    }

    @Test
    public void instantiateRecursiveApiTypes() {
        ClassAccessor.of(RecursiveApiClassesContainer.class, prefabValues, false).getRedObject();
    }

    @Test
    public void instantiateCollectionImplementations() {
        ClassAccessor.of(AllRecursiveCollectionImplementationsContainer.class, prefabValues, false).getRedObject();
    }

    @Test
    public void instantiateInterfaceField() {
        ClassAccessor.of(InterfaceContainer.class, prefabValues, false).getRedObject();
    }

    @Test
    public void instantiateAbstractClassField() {
        ClassAccessor.of(AbstractClassContainer.class, prefabValues, false).getRedObject();
    }

    @Test
    public void anInvalidTypeShouldNotThrowAnExceptionUponCreation() {
        ClassAccessor.of(null, prefabValues, false);
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
