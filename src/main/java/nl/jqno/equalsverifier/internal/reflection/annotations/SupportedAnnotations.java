package nl.jqno.equalsverifier.internal.reflection.annotations;

import static nl.jqno.equalsverifier.internal.reflection.Util.classForName;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.Warning;

/**
 * Descriptions of the annotations that EqualsVerifier supports.
 *
 * <p>The actual annotations cannot be referenced here, as that would create dependencies on the
 * libraries that contain them, and it would preclude people from creating and using their own
 * annotations with the same name.
 */
public enum SupportedAnnotations implements Annotation {
    /**
     * If a class is marked @Immutable, EqualsVerifier will not complain about fields not being
     * final.
     */
    IMMUTABLE(false, "Immutable"),

    /**
     * If a field is marked @Nonnull (or @NonNull or @NotNull), EqualsVerifier will not complain
     * about potential {@link NullPointerException}s being thrown if this field is null.
     */
    NONNULL(true, "Nonnull", "NonNull", "NotNull"),

    /** If a class is marked @Nonnull, @Nullable can be used to revert that for specific fields. */
    NULLABLE(false, "Nullable", "CheckForNull"),

    /**
     * If a class or package is marked with @DefaultAnnotation(Nonnull.class), EqualsVerifier will
     * not complain about potential {@link NullPointerException}s being thrown if any of the fields
     * in that class or package are null.
     *
     * <p>Note that @DefaultAnnotation is deprecated. Nevertheless, EqualsVerifier still supports
     * it.
     */
    FINDBUGS1X_DEFAULT_ANNOTATION_NONNULL(
        false,
        "edu.umd.cs.findbugs.annotations.DefaultAnnotation",
        "edu.umd.cs.findbugs.annotations.DefaultAnnotationForFields"
    ) {
        @Override
        public boolean validate(
            AnnotationProperties properties,
            AnnotationCache annotationCache,
            Set<String> ignoredAnnotations
        ) {
            Set<String> values = properties.getArrayValues("value");
            for (String value : values) {
                for (String className : NONNULL.partialClassNames()) {
                    if (value.contains(className) && !ignoredAnnotations.contains(value)) {
                        return true;
                    }
                }
            }
            return false;
        }
    },

    /**
     * Represents any annotation that is marked with @Nonnull and @TypeQualifierDefault. If a class
     * or package is marked with such an annotation, EqualsVerifier will not complain about
     * potential {@link NullPointerException}s being thrown if any of the fields in that class or
     * package are null.
     */
    JSR305_DEFAULT_ANNOTATION_NONNULL(false, "") {
        @Override
        public boolean validate(
            AnnotationProperties properties,
            AnnotationCache annotationCache,
            Set<String> ignoredAnnotations
        ) {
            try {
                Class<?> annotationType = classForName(properties.getClassName());
                if (annotationType == null) {
                    return false;
                }
                AnnotationCacheBuilder builder = new AnnotationCacheBuilder(
                    new Annotation[] { NONNULL, JSR305_TYPE_QUALIFIER_DEFAULT },
                    ignoredAnnotations
                );
                builder.build(annotationType, annotationCache);

                boolean hasNonnullAnnotation = annotationCache.hasClassAnnotation(
                    annotationType,
                    NONNULL
                );
                boolean hasValidTypeQualifierDefault = annotationCache.hasClassAnnotation(
                    annotationType,
                    JSR305_TYPE_QUALIFIER_DEFAULT
                );
                return hasNonnullAnnotation && hasValidTypeQualifierDefault;
            } catch (UnsupportedClassVersionError ignored) {
                return false;
            }
        }
    },

    /**
     * If an annotation type is marked with JSR305's @TypeQualifierDefault annotation, it becomes a
     * 'default' annotation for whatever other annotation it is marked with; for example @Nonnull.
     */
    JSR305_TYPE_QUALIFIER_DEFAULT(false, "javax.annotation.meta.TypeQualifierDefault") {
        @Override
        public boolean validate(
            AnnotationProperties properties,
            AnnotationCache annotationCache,
            Set<String> ignoredAnnotations
        ) {
            Set<String> value = properties.getArrayValues("value");
            return value.contains("FIELD") || value.contains("PARAMETER");
        }
    },

    /**
     * If a class or package is marked with @NonNullByDefault, EqualsVerifier will not complain
     * about potential {@link NullPointerException}s being thrown if any of the fields in that class
     * or package are null.
     */
    ECLIPSE_DEFAULT_ANNOTATION_NONNULL(false, "org.eclipse.jdt.annotation.NonNullByDefault") {
        @Override
        public boolean validate(
            AnnotationProperties properties,
            AnnotationCache annotationCache,
            Set<String> ignoredAnnotations
        ) {
            Set<String> values = properties.getArrayValues("value");
            if (values == null) {
                return true;
            }
            for (Object value : values) {
                if ("FIELD".equals(value)) {
                    return true;
                }
            }
            return false;
        }
    },

    /**
     * JPA Entities cannot be final, nor can their fields be. EqualsVerifier will not complain about
     * non-final fields on @Entity, @Embeddable and @MappedSuperclass classes.
     */
    ENTITY(
        false,
        "javax.persistence.Entity",
        "javax.persistence.Embeddable",
        "javax.persistence.MappedSuperclass"
    ),

    /**
     * Fields in JPA Entities that are marked @Transient should not be included in the
     * equals/hashCode contract, like fields that have the Java transient modifier. EqualsVerifier
     * will treat these the same.
     */
    TRANSIENT(true, "javax.persistence.Transient"),

    /**
     * Fields in JPA Entities that are marked @Id or @EmbeddedId are usually part of the entity's
     * surrogate key. EqualsVerifier will therefore assume that it must not be used in the
     * equals/hashCode contract, unless {@link Warning#SURROGATE_KEY} is suppressed.
     */
    ID(false, "javax.persistence.Id", "javax.persistence.EmbeddedId") {
        @Override
        public void postProcess(Set<Class<?>> types, AnnotationCache annotationCache) {
            types.forEach(t -> annotationCache.addClassAnnotation(t, ID));
        }
    },

    /**
     * Fields in JPA Entities that are marked @NaturalId are part of the entity's natural/business
     * identity. If a @NaturalId annotation is present in an entity, all fields marked with this
     * annotation must be part of the equals/hashCode contract, and all fields NOT marked with it
     * must NOT be part of the contract.
     */
    NATURALID(false, "org.hibernate.annotations.NaturalId") {
        @Override
        public void postProcess(Set<Class<?>> types, AnnotationCache annotationCache) {
            types.forEach(t -> annotationCache.addClassAnnotation(t, NATURALID));
        }
    };

    private final boolean inherits;
    private final Set<String> partialClassNames;

    SupportedAnnotations(boolean inherits, String... partialClassNames) {
        this.inherits = inherits;
        this.partialClassNames = new HashSet<>();
        this.partialClassNames.addAll(Arrays.asList(partialClassNames));
    }

    @Override
    public Set<String> partialClassNames() {
        return Collections.unmodifiableSet(partialClassNames);
    }

    @Override
    public boolean inherits() {
        return inherits;
    }
}
