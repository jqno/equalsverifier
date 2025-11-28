package nl.jqno.equalsverifier.internal.reflection.annotations;

import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import net.bytebuddy.description.modifier.Visibility;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.packages.annotated.AnnotatedPackage;
import nl.jqno.equalsverifier_testhelpers.annotations.AnnotationWithValues;
import nl.jqno.equalsverifier_testhelpers.annotations.FieldAnnotationRuntimeRetention;
import nl.jqno.equalsverifier_testhelpers.annotations.NotNull;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnnotationCacheBuilderTest {

    private static final String RUNTIME_RETENTION = "runtimeRetention";
    private static final String CLASS_RETENTION = "classRetention";
    private static final String BOTH_RETENTIONS = "bothRetentions";
    private static final String NO_RETENTION = "noRetention";

    private static final Set<String> NO_INGORED_ANNOTATIONS = new HashSet<>();

    private AnnotationCacheBuilder cacheBuilder;
    private AnnotationCache cache;

    @BeforeEach
    void setUp() {
        cache = new AnnotationCache();
        cacheBuilder = new AnnotationCacheBuilder(TestSupportedAnnotations.values(), NO_INGORED_ANNOTATIONS);
    }

    @Test
    void findRuntimeAnnotationInType() {
        build(AnnotatedWithRuntime.class, AnnotatedWithClass.class, AnnotatedWithBoth.class, AnnotatedFields.class);

        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION);
        assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_RUNTIME_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedWithClass.class, TYPE_RUNTIME_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_RUNTIME_RETENTION);
    }

    @Test
    void findClassAnnotationInType() {
        build(AnnotatedWithRuntime.class, AnnotatedWithClass.class, AnnotatedWithBoth.class, AnnotatedFields.class);

        assertTypeHasAnnotation(AnnotatedWithClass.class, TYPE_CLASS_RETENTION);
        assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedWithRuntime.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_CLASS_RETENTION);
    }

    @Test
    void findRuntimeAnnotationInField() {
        build(AnnotatedFields.class);

        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION);
        assertFieldHasAnnotation(AnnotatedFields.class, BOTH_RETENTIONS, FIELD_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, CLASS_RETENTION, FIELD_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, NO_RETENTION, FIELD_RUNTIME_RETENTION);
    }

    @Test
    void findClassAnnotationInField() {
        build(AnnotatedFields.class);

        assertFieldHasAnnotation(AnnotatedFields.class, CLASS_RETENTION, FIELD_CLASS_RETENTION);
        assertFieldHasAnnotation(AnnotatedFields.class, BOTH_RETENTIONS, FIELD_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, NO_RETENTION, FIELD_CLASS_RETENTION);
    }

    @Test
    void findTypeUseRuntimeAnnotationInField() {
        build(AnnotatedTypes.class);

        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION);
        assertFieldHasAnnotation(AnnotatedTypes.class, BOTH_RETENTIONS, TYPEUSE_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, CLASS_RETENTION, TYPEUSE_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, NO_RETENTION, TYPEUSE_RUNTIME_RETENTION);
    }

    @Test
    void findTypeUseClassAnnotationInField() {
        build(AnnotatedTypes.class);

        assertFieldHasAnnotation(AnnotatedTypes.class, CLASS_RETENTION, TYPEUSE_CLASS_RETENTION);
        assertFieldHasAnnotation(AnnotatedTypes.class, BOTH_RETENTIONS, TYPEUSE_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedTypes.class, NO_RETENTION, TYPEUSE_CLASS_RETENTION);
    }

    @Test
    void findRuntimeAnnotationInMethod() {
        build(AnnotatedMethods.class);

        assertFieldHasAnnotation(AnnotatedMethods.class, RUNTIME_RETENTION, METHOD_RUNTIME_RETENTION);
        assertFieldHasAnnotation(AnnotatedMethods.class, BOTH_RETENTIONS, METHOD_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedMethods.class, CLASS_RETENTION, METHOD_RUNTIME_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedMethods.class, NO_RETENTION, METHOD_RUNTIME_RETENTION);
    }

    @Test
    void findClassAnnotationInMethod() {
        build(AnnotatedMethods.class);

        assertFieldHasAnnotation(AnnotatedMethods.class, CLASS_RETENTION, METHOD_CLASS_RETENTION);
        assertFieldHasAnnotation(AnnotatedMethods.class, BOTH_RETENTIONS, METHOD_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedMethods.class, RUNTIME_RETENTION, METHOD_CLASS_RETENTION);
        assertFieldDoesNotHaveAnnotation(AnnotatedMethods.class, NO_RETENTION, METHOD_CLASS_RETENTION);
    }

    @Test
    void findPartialAnnotationName() {
        build(AnnotatedWithRuntime.class, AnnotatedFields.class, AnnotatedTypes.class, AnnotatedMethods.class);

        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_PARTIAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_PARTIAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedTypes.class, RUNTIME_RETENTION, TYPEUSE_RUNTIME_RETENTION_PARTIAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedMethods.class, RUNTIME_RETENTION, METHOD_RUNTIME_RETENTION_PARTIAL_CLASSNAME);
    }

    @Test
    void findFullyQualifiedAnnotationName() {
        build(AnnotatedWithRuntime.class, AnnotatedFields.class, AnnotatedTypes.class, AnnotatedMethods.class);

        assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_CANONICAL_CLASSNAME);
        assertFieldHasAnnotation(AnnotatedFields.class, RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_CANONICAL_CLASSNAME);
        assertFieldHasAnnotation(
            AnnotatedTypes.class,
            RUNTIME_RETENTION,
            TYPEUSE_RUNTIME_RETENTION_CANONICAL_CLASSNAME);
        assertFieldHasAnnotation(
            AnnotatedMethods.class,
            RUNTIME_RETENTION,
            METHOD_RUNTIME_RETENTION_CANONICAL_CLASSNAME);
    }

    @Test
    void typeAnnotationInheritance() {
        build(SubclassWithAnnotations.class);

        assertTypeHasAnnotation(SubclassWithAnnotations.class, TYPE_INHERITS);
        assertTypeHasAnnotation(SuperclassWithAnnotations.class, TYPE_INHERITS);
        assertTypeDoesNotHaveAnnotation(SubclassWithAnnotations.class, TYPE_DOESNT_INHERIT);
    }

    @Test
    void fieldAnnotationInheritance() {
        build(SubclassWithAnnotations.class);

        assertFieldHasAnnotation(SubclassWithAnnotations.class, "inherits", FIELD_INHERITS);
        assertFieldHasAnnotation(SuperclassWithAnnotations.class, "inherits", FIELD_INHERITS);
        assertFieldDoesNotHaveAnnotation(SubclassWithAnnotations.class, "doesntInherit", FIELD_DOESNT_INHERIT);
    }

    @Test
    void typeUseAnnotationInheritance() {
        build(SubclassWithAnnotations.class);

        assertFieldHasAnnotation(SubclassWithAnnotations.class, "inherits", TYPEUSE_INHERITS);
        assertFieldHasAnnotation(SuperclassWithAnnotations.class, "inherits", TYPEUSE_INHERITS);
        assertFieldDoesNotHaveAnnotation(SubclassWithAnnotations.class, "doesntInherit", TYPEUSE_DOESNT_INHERIT);
    }

    @Test
    void typeAnnotationInOuterClass() {
        build(AnnotatedOuter.AnnotatedMiddle.class);

        assertTypeHasAnnotation(AnnotatedOuter.AnnotatedMiddle.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedOuter.AnnotatedMiddle.class, INAPPLICABLE);
    }

    @Test
    void typeAnnotationNestedInOuterClass() {
        build(AnnotatedOuter.AnnotatedMiddle.AnnotatedInner.class);

        assertTypeHasAnnotation(AnnotatedOuter.AnnotatedMiddle.AnnotatedInner.class, TYPE_CLASS_RETENTION);
        assertTypeDoesNotHaveAnnotation(AnnotatedOuter.AnnotatedMiddle.AnnotatedInner.class, INAPPLICABLE);
    }

    @Test
    void typeAnnotationInPackage() {
        build(AnnotatedPackage.class);

        assertTypeHasAnnotation(AnnotatedPackage.class, PACKAGE_ANNOTATION);
        assertTypeDoesNotHaveAnnotation(AnnotatedPackage.class, INAPPLICABLE);
    }

    @Test
    void searchIgnoredField() {
        cacheBuilder = new AnnotationCacheBuilder(TestSupportedAnnotations.values(),
                Set.of(FieldAnnotationRuntimeRetention.class.getCanonicalName()));
        build(AnnotatedFields.class);

        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, "runtimeRetention", FIELD_RUNTIME_RETENTION);
    }

    @Test
    void searchNonExistingField() {
        build(AnnotatedFields.class);

        assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, "x", FIELD_RUNTIME_RETENTION);
    }

    @Test
    void annotationsAreValidated() {
        build(InapplicableAnnotations.class);

        assertTypeDoesNotHaveAnnotation(InapplicableAnnotations.class, INAPPLICABLE);
        assertFieldDoesNotHaveAnnotation(InapplicableAnnotations.class, "inapplicable", INAPPLICABLE);
    }

    @Test
    void annotationsArePostProcessed() {
        build(PostProcessedFieldAnnotation.class);

        assertTypeHasAnnotation(PostProcessedFieldAnnotation.class, POST_PROCESS);
        assertFieldHasAnnotation(PostProcessedFieldAnnotation.class, "postProcessed", POST_PROCESS);
    }

    @Test
    void annotationsEnumParametersAreFoundOnClass() {
        AnnotationWithClassValuesAnnotation annotation = new AnnotationWithClassValuesAnnotation();
        Annotation[] supportedAnnotations = { annotation };
        AnnotationCacheBuilder acb = new AnnotationCacheBuilder(supportedAnnotations, NO_INGORED_ANNOTATIONS);
        acb.build(AnnotationWithValuesContainer.class, cache);

        assertTypeHasAnnotation(AnnotationWithValuesContainer.class, annotation);

        String value = annotation.properties.getEnumValue("elementType");
        assertThat(value).isEqualTo("FIELD");
    }

    @Test
    void annotationsArrayParametersAreFoundOnClass() {
        AnnotationWithClassValuesAnnotation annotation = new AnnotationWithClassValuesAnnotation();
        Annotation[] supportedAnnotations = { annotation };
        AnnotationCacheBuilder acb = new AnnotationCacheBuilder(supportedAnnotations, NO_INGORED_ANNOTATIONS);
        acb.build(AnnotationWithValuesContainer.class, cache);

        assertTypeHasAnnotation(AnnotationWithValuesContainer.class, annotation);

        var annotations = annotation.properties.getArrayValues("annotations");
        assertThat(annotations).contains("javax.annotation.Nonnull");
        assertThat(annotations).contains("nl.jqno.equalsverifier_testhelpers.annotations.NotNull");
    }

    @Test
    void classWithMethodNamedGetDoesNotThrow() {
        build(ClassContainsMethodNamedGet.class);
    }

    @Test
    void classWithDifficultAnnotationDoesNotThrow() {
        build(DifficultAnnotationHolder.class);
    }

    @Test
    void loadedBySystemClassLoaderDoesNotThrowNullPointerException() {
        build(LoadedBySystemClassLoader.class);
    }

    @Test
    void dynamicClassDoesntGetProcessed_butDoesntThrowEither() {
        Class<?> type = Instantiator.giveDynamicSubclass(AnnotatedWithRuntime.class);
        build(type);

        assertTypeDoesNotHaveAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION);
    }

    @Test
    void generatedClassWithGeneratedFieldDoesNotThrow() {
        class Super {}
        Class<?> sub = Instantiator
                .giveDynamicSubclass(
                    Super.class,
                    "dynamicField",
                    b -> b.defineField("dynamicField", int.class, Visibility.PRIVATE));
        build(sub);
    }

    private void build(Class<?>... types) {
        for (Class<?> type : types) {
            cacheBuilder.build(type, cache);
        }
    }

    private void assertTypeHasAnnotation(Class<?> type, Annotation annotation) {
        assertThat(cache.hasClassAnnotation(type, annotation)).isTrue();
    }

    private void assertTypeDoesNotHaveAnnotation(Class<?> type, Annotation annotation) {
        assertThat(cache.hasClassAnnotation(type, annotation)).isFalse();
    }

    private void assertFieldHasAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        assertThat(cache.hasFieldAnnotation(type, fieldName, annotation)).isTrue();
    }

    private void assertFieldDoesNotHaveAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        assertThat(cache.hasFieldAnnotation(type, fieldName, annotation)).isFalse();
    }

    private static final class AnnotationWithClassValuesAnnotation implements Annotation {

        private AnnotationProperties properties;

        @Override
        public Set<String> partialClassNames() {
            return Set.of(AnnotationWithValues.class.getSimpleName());
        }

        @Override
        public boolean inherits() {
            return false;
        }

        @Override
        public boolean validate(
                AnnotationProperties props,
                AnnotationCache annotationCache,
                Set<String> ignoredAnnotations) {
            this.properties = props;
            return true;
        }
    }

    @AnnotationWithValues(annotations = { Nonnull.class, NotNull.class }, elementType = ElementType.FIELD)
    private static final class AnnotationWithValuesContainer {}
}
