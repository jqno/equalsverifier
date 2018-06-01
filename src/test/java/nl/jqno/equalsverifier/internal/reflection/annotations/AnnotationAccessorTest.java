package nl.jqno.equalsverifier.internal.reflection.annotations;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.testhelpers.annotations.AnnotationWithClassValues;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;
import nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnnotationAccessorTest {
    private static final String RUNTIME_RETENTION = "runtimeRetention";
    private static final String CLASS_RETENTION = "classRetention";
    private static final String BOTH_RETENTIONS = "bothRetentions";
    private static final String NO_RETENTION = "noRetention";

    private static final Set<String> NO_INGORED_ANNOTATIONS = new HashSet<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void loadedBySystemClassLoaderDoesNotThrowNullPointerException() {
        AnnotationAccessor accessor =
                new AnnotationAccessor(TestSupportedAnnotations.values(), LoadedBySystemClassLoader.class, NO_INGORED_ANNOTATIONS, false);
        accessor.typeHas(null);
    }

    @Test
    public void findRuntimeAnnotationInType() {
        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION);
        assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_RUNTIME_RETENTION);

        assertTypeDoesNotHaveAnnotation(AnnotatedWithClass.class, TYPE_RUNTIME_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_RUNTIME_RETENTION);
    }

    @Test
    public void findClassAnnotationInType() {
        assertTypeHasAnnotation(AnnotatedWithClass.class, TYPE_CLASS_RETENTION);
        assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_CLASS_RETENTION);

        assertTypeDoesNotHaveAnnotation(AnnotatedWithRuntime.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_CLASS_RETENTION);
    }

    @Test
    public void findRuntimeAnnotationInField() {
        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION);
        assertFieldHasAnnotation(AnnotatedFields.class, BOTH_RETENTIONS, FIELD_RUNTIME_RETENTION);

        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, CLASS_RETENTION, FIELD_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, NO_RETENTION, FIELD_RUNTIME_RETENTION);
    }

    @Test
    public void findClassAnnotationInField() {
        assertFieldHasAnnotation(AnnotatedFields.class, CLASS_RETENTION, FIELD_CLASS_RETENTION);
        assertFieldHasAnnotation(AnnotatedFields.class, BOTH_RETENTIONS, FIELD_CLASS_RETENTION);

        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, NO_RETENTION, FIELD_CLASS_RETENTION);
    }

    @Test
    public void findTypeUseRuntimeAnnotationInField() {
        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION);
        assertFieldHasAnnotation(AnnotatedTypes.class, BOTH_RETENTIONS, TYPEUSE_RUNTIME_RETENTION);

        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, CLASS_RETENTION, TYPEUSE_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, NO_RETENTION, TYPEUSE_RUNTIME_RETENTION);
    }

    @Test
    public void findTypeUseClassAnnotationInField() {
        assertFieldHasAnnotation(AnnotatedTypes.class, CLASS_RETENTION, TYPEUSE_CLASS_RETENTION);
        assertFieldHasAnnotation(AnnotatedTypes.class, BOTH_RETENTIONS, TYPEUSE_CLASS_RETENTION);

        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, NO_RETENTION, TYPEUSE_CLASS_RETENTION);
    }

    @Test
    public void findPartialAnnotationName() {
        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR);
        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR);
        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR);
    }

    @Test
    public void findFullyQualifiedAnnotationName() {
        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR);
        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR);
        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR);
    }

    @Test
    public void searchNonExistingField() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage(containsString("does not have field x"));

        findFieldAnnotationFor(AnnotatedFields.class, "x", FIELD_RUNTIME_RETENTION);
    }

    @Test
    public void typeAnnotationInheritance() {
        assertTypeHasAnnotation(SubclassWithAnnotations.class, TYPE_INHERITS);
        assertTypeDoesNotHaveAnnotation(SubclassWithAnnotations.class, TYPE_DOESNT_INHERIT);
    }

    @Test
    public void fieldAnnotationInheritance() {
        assertFieldHasAnnotation(SubclassWithAnnotations.class, "inherits", FIELD_INHERITS);
        assertFieldDoesNotHaveAnnotation(SubclassWithAnnotations.class, "doesntInherit", FIELD_DOESNT_INHERIT);
    }

    @Test
    public void typeUseAnnotationInheritance() {
        assertFieldHasAnnotation(SubclassWithAnnotations.class, "inherits", TYPEUSE_INHERITS);
        assertFieldDoesNotHaveAnnotation(SubclassWithAnnotations.class, "doesntInherit", TYPEUSE_DOESNT_INHERIT);
    }

    @Test
    public void inapplicableAnnotationsAreNotFound() {
        assertTypeDoesNotHaveAnnotation(InapplicableAnnotations.class, INAPPLICABLE);
        assertFieldDoesNotHaveAnnotation(InapplicableAnnotations.class, "inapplicable", INAPPLICABLE);
    }

    @Test
    public void annotationsArrayParametersAreFoundOnClass() {
        AnnotationWithClassValuesDescriptor annotation = new AnnotationWithClassValuesDescriptor();
        AnnotationAccessor accessor =
                new AnnotationAccessor(new Annotation[] { annotation }, AnnotationWithClassValuesContainer.class, NO_INGORED_ANNOTATIONS, false);

        boolean annotationPresent = accessor.typeHas(annotation);

        assertTrue(annotationPresent);
        Set<String> annotations = mapGetDescriptor(annotation);
        assertTrue(annotations.contains("Ljavax/annotation/Nonnull;"));
        assertTrue(annotations.contains("Lnl/jqno/equalsverifier/testhelpers/annotations/NotNull;"));
    }

    private Set<String> mapGetDescriptor(AnnotationWithClassValuesDescriptor annotation) {
        Set<String> result = new HashSet<>();
        for (Object o : annotation.properties.getArrayValues("annotations")) {
            Type type = (Type)o;
            result.add(type.getDescriptor());
        }
        return result;
    }

    @Test
    public void dynamicClassThrowsException() {
        Class<?> type = Instantiator.of(Point.class).instantiateAnonymousSubclass().getClass();
        AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, NO_INGORED_ANNOTATIONS, false);

        thrown.expect(ReflectionException.class);
        thrown.expectMessage(containsString("Cannot read class file"));

        accessor.typeHas(TYPE_CLASS_RETENTION);
    }

    @Test
    public void dynamicClassWithSuppressedWarning() {
        Class<?> type = Instantiator.of(Point.class).instantiateAnonymousSubclass().getClass();
        AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, NO_INGORED_ANNOTATIONS, true);
        assertFalse(accessor.typeHas(TYPE_CLASS_RETENTION));

        // Checks if the short circuit works
        assertFalse(accessor.typeHas(TYPE_CLASS_RETENTION));
        assertFalse(accessor.fieldHas("x", FIELD_CLASS_RETENTION));
    }

    @Test
    public void generatedClassWithGeneratedFieldWithSuppressedWarning() {
        class Super {}
        Class<?> sub = new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .subclass(Super.class)
                .defineField("dynamicField", int.class, Visibility.PRIVATE)
                .make()
                .load(Super.class.getClassLoader(), Instantiator.getClassLoadingStrategy(Super.class))
                .getLoaded();
        AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), sub, NO_INGORED_ANNOTATIONS, true);
        assertFalse(accessor.fieldHas("dynamicField", FIELD_RUNTIME_RETENTION));
    }

    @Test
    public void regularClassWithSuppressedWarningStillProcessesAnnotation() {
        AnnotationAccessor accessor =
                new AnnotationAccessor(TestSupportedAnnotations.values(), AnnotatedWithClass.class, NO_INGORED_ANNOTATIONS, true);
        assertTrue(accessor.typeHas(TYPE_CLASS_RETENTION));
    }

    private void assertTypeHasAnnotation(Class<?> type, Annotation annotation) {
        assertTrue(findTypeAnnotationFor(type, annotation));
    }

    private void assertTypeDoesNotHaveAnnotation(Class<?> type, Annotation annotation) {
        assertFalse(findTypeAnnotationFor(type, annotation));
    }

    private void assertFieldHasAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        assertTrue(findFieldAnnotationFor(type, fieldName, annotation));
    }

    private void assertFieldDoesNotHaveAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        assertFalse(findFieldAnnotationFor(type, fieldName, annotation));
    }

    private boolean findTypeAnnotationFor(Class<?> type, Annotation annotation) {
        AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, NO_INGORED_ANNOTATIONS, false);
        return accessor.typeHas(annotation);
    }

    private boolean findFieldAnnotationFor(Class<?> type, String fieldName, Annotation annotation) {
        AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, NO_INGORED_ANNOTATIONS, false);
        return accessor.fieldHas(fieldName, annotation);
    }

    private static class AnnotationWithClassValuesDescriptor implements Annotation {
        private AnnotationProperties properties;

        @Override
        public Iterable<String> descriptors() {
            return Collections.singletonList(AnnotationWithClassValues.class.getSimpleName());
        }

        @Override
        public boolean inherits() {
            return false;
        }

        @Override
        public boolean validate(AnnotationProperties descriptor, AnnotationCache annotationCache, Set<String> ignoredAnnotations) {
            this.properties = descriptor;
            return true;
        }
    }

    @AnnotationWithClassValues(annotations={ Nonnull.class, NotNull.class })
    private static class AnnotationWithClassValuesContainer {}
}
