package nl.jqno.equalsverifier.internal.reflection.annotations;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import nl.jqno.equalsverifier.internal.packageannotation.AnnotatedPackage;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.Util;
import nl.jqno.equalsverifier.testhelpers.annotations.AnnotationWithClassValues;
import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationRuntimeRetention;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;
import nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnnotationCacheBuilderTest {
    private static final String RUNTIME_RETENTION = "runtimeRetention";
    private static final String CLASS_RETENTION = "classRetention";
    private static final String BOTH_RETENTIONS = "bothRetentions";
    private static final String NO_RETENTION = "noRetention";

    private static final Set<String> NO_INGORED_ANNOTATIONS = new HashSet<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AnnotationCacheBuilder cacheBuilder;
    private AnnotationCache cache;

    @Before
    public void setUp() {
        cache = new AnnotationCache();
        cacheBuilder = new AnnotationCacheBuilder(TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS);
    }

    @Test
    public void findRuntimeAnnotationInType() {
        build(AnnotatedWithRuntime.class, AnnotatedWithClass.class, AnnotatedWithBoth.class, AnnotatedFields.class);

        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION);
        assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_RUNTIME_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedWithClass.class, TYPE_RUNTIME_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_RUNTIME_RETENTION);
    }

    @Test
    public void findClassAnnotationInType() {
        build(AnnotatedWithRuntime.class, AnnotatedWithClass.class, AnnotatedWithBoth.class, AnnotatedFields.class);

        assertTypeHasAnnotation(AnnotatedWithClass.class, TYPE_CLASS_RETENTION);
        assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedWithRuntime.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_CLASS_RETENTION);
    }

    @Test
    public void findRuntimeAnnotationInField() {
        build(AnnotatedFields.class);

        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION);
        assertFieldHasAnnotation(AnnotatedFields.class, BOTH_RETENTIONS, FIELD_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, CLASS_RETENTION, FIELD_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, NO_RETENTION, FIELD_RUNTIME_RETENTION);
    }

    @Test
    public void findClassAnnotationInField() {
        build(AnnotatedFields.class);

        assertFieldHasAnnotation(AnnotatedFields.class, CLASS_RETENTION, FIELD_CLASS_RETENTION);
        assertFieldHasAnnotation(AnnotatedFields.class, BOTH_RETENTIONS, FIELD_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, NO_RETENTION, FIELD_CLASS_RETENTION);
    }

    @Test
    public void findTypeUseRuntimeAnnotationInField() {
        build(AnnotatedTypes.class);

        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION);
        assertFieldHasAnnotation(AnnotatedTypes.class, BOTH_RETENTIONS, TYPEUSE_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, CLASS_RETENTION, TYPEUSE_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, NO_RETENTION, TYPEUSE_RUNTIME_RETENTION);
    }

    @Test
    public void findTypeUseClassAnnotationInField() {
        build(AnnotatedTypes.class);

        assertFieldHasAnnotation(AnnotatedTypes.class, CLASS_RETENTION, TYPEUSE_CLASS_RETENTION);
        assertFieldHasAnnotation(AnnotatedTypes.class, BOTH_RETENTIONS, TYPEUSE_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, NO_RETENTION, TYPEUSE_CLASS_RETENTION);
    }

    @Test
    public void findPartialAnnotationName() {
        build(AnnotatedWithRuntime.class, AnnotatedFields.class, AnnotatedTypes.class);

        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_PARTIAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_PARTIAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION_PARTIAL_CLASSNAME);
    }

    @Test
    public void findFullyQualifiedAnnotationName() {
        build(AnnotatedWithRuntime.class, AnnotatedFields.class, AnnotatedTypes.class);

        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_CANONICAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_CANONICAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION_CANONICAL_CLASSNAME);
    }

    @Test
    public void typeAnnotationInheritance() {
        build(SubclassWithAnnotations.class);

        assertTypeHasAnnotation(SubclassWithAnnotations.class, TYPE_INHERITS);
        assertTypeHasAnnotation(SuperclassWithAnnotations.class, TYPE_INHERITS);
        assertTypeDoesNotHaveAnnotation(SubclassWithAnnotations.class, TYPE_DOESNT_INHERIT);
    }

    @Test
    public void fieldAnnotationInheritance() {
        build(SubclassWithAnnotations.class);

        assertFieldHasAnnotation(SubclassWithAnnotations.class, "inherits", FIELD_INHERITS);
        assertFieldHasAnnotation(SuperclassWithAnnotations.class, "inherits", FIELD_INHERITS);
        assertFieldDoesNotHaveAnnotation(SubclassWithAnnotations.class, "doesntInherit", FIELD_DOESNT_INHERIT);
    }

    @Test
    public void typeUseAnnotationInheritance() {
        build(SubclassWithAnnotations.class);

        assertFieldHasAnnotation(SubclassWithAnnotations.class, "inherits", TYPEUSE_INHERITS);
        assertFieldHasAnnotation(SuperclassWithAnnotations.class, "inherits", TYPEUSE_INHERITS);
        assertFieldDoesNotHaveAnnotation(SubclassWithAnnotations.class, "doesntInherit", TYPEUSE_DOESNT_INHERIT);
    }

    @Test
    public void typeAnnotationInOuterClass() {
        build(AnnotatedOuter.AnnotatedMiddle.class);

        assertTypeHasAnnotation(AnnotatedOuter.AnnotatedMiddle.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedOuter.AnnotatedMiddle.class, INAPPLICABLE);
    }

    @Test
    public void typeAnnotationNestedInOuterClass() {
        build(AnnotatedOuter.AnnotatedMiddle.AnnotatedInner.class);

        assertTypeHasAnnotation(AnnotatedOuter.AnnotatedMiddle.AnnotatedInner.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedOuter.AnnotatedMiddle.AnnotatedInner.class, INAPPLICABLE);
    }

    @Test
    public void typeAnnotationInPackage() {
        build(AnnotatedPackage.class);

        assertTypeHasAnnotation(AnnotatedPackage.class, PACKAGE_ANNOTATION);
        assertTypeDoesNotHaveAnnotation(AnnotatedPackage.class, INAPPLICABLE);
    }

    @Test
    public void searchIgnoredField() {
        cacheBuilder = new AnnotationCacheBuilder(
            TestSupportedAnnotations.values(),
            Util.setOf(FieldAnnotationRuntimeRetention.class.getCanonicalName()));
        build(AnnotatedFields.class);

        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, "runtimeRetention", FIELD_RUNTIME_RETENTION);
    }

    @Test
    public void searchNonExistingField() {
        build(AnnotatedFields.class);

        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, "x", FIELD_RUNTIME_RETENTION);
    }

    @Test
    public void inapplicableAnnotationsAreNotFound() {
        build(InapplicableAnnotations.class);

        assertTypeDoesNotHaveAnnotation(InapplicableAnnotations.class, INAPPLICABLE);
        assertFieldDoesNotHaveAnnotation(InapplicableAnnotations.class, "inapplicable", INAPPLICABLE);
    }

    @Test
    public void annotationsArrayParametersAreFoundOnClass() {
        AnnotationWithClassValuesAnnotation annotation = new AnnotationWithClassValuesAnnotation();
        Annotation[] supportedAnnotations = { annotation };
        AnnotationCacheBuilder acb = new AnnotationCacheBuilder(supportedAnnotations, NO_INGORED_ANNOTATIONS);
        acb.build(AnnotationWithClassValuesContainer.class, cache);

        assertTypeHasAnnotation(AnnotationWithClassValuesContainer.class, annotation);

        Set<String> annotations = new HashSet<>(annotation.properties.getArrayValues("annotations"));
        assertTrue(annotations.contains("javax.annotation.Nonnull"));
        assertTrue(annotations.contains("nl.jqno.equalsverifier.testhelpers.annotations.NotNull"));
    }

    @Test
    public void loadedBySystemClassLoaderDoesNotThrowNullPointerException() {
        build(LoadedBySystemClassLoader.class);
    }

    @Test
    public void dynamicClassDoesntGetProcessed_butDoesntThrowEither() {
        Class<?> type = Instantiator.of(AnnotatedWithRuntime.class).instantiateAnonymousSubclass().getClass();
        build(type);

        assertTypeDoesNotHaveAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION);
    }

    @Test
    public void generatedClassWithGeneratedFieldDoesNotThrow() {
        class Super {}
        Class<?> sub = new ByteBuddy()
            .with(TypeValidation.DISABLED)
            .subclass(Super.class)
            .defineField("dynamicField", int.class, Visibility.PRIVATE)
            .make()
            .load(Super.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
            .getLoaded();
        build(sub);
    }

    private void build(Class<?>... types) {
        for (Class<?> type : types) {
            cacheBuilder.build(type, cache);
        }
    }

    private void assertTypeHasAnnotation(Class<?> type, Annotation annotation) {
        assertTrue(cache.hasClassAnnotation(type, annotation));
    }

    private void assertTypeDoesNotHaveAnnotation(Class<?> type, Annotation annotation) {
        assertFalse(cache.hasClassAnnotation(type, annotation));
    }

    private void assertFieldHasAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        assertTrue(cache.hasFieldAnnotation(type, fieldName, annotation));
    }

    private void assertFieldDoesNotHaveAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        assertFalse(cache.hasFieldAnnotation(type, fieldName, annotation));
    }

    private static class AnnotationWithClassValuesAnnotation implements Annotation {
        private AnnotationProperties properties;

        @Override
        public Set<String> partialClassNames() {
            Set<String> result = new HashSet<>();
            result.add(AnnotationWithClassValues.class.getSimpleName());
            return result;
        }

        @Override
        public boolean inherits() {
            return false;
        }

        @Override
        public boolean validate(AnnotationProperties props, AnnotationCache annotationCache, Set<String> ignoredAnnotations) {
            this.properties = props;
            return true;
        }
    }

    @AnnotationWithClassValues(annotations={ Nonnull.class, NotNull.class })
    private static class AnnotationWithClassValuesContainer {}
}
