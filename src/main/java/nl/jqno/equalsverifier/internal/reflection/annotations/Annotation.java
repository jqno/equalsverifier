package nl.jqno.equalsverifier.internal.reflection.annotations;

import java.util.Set;

/**
 * Describes an annotation that can be recognised by EqualsVerifier.
 *
 * <p>The annotation can have {@link java.lang.annotation.RetentionPolicy#RUNTIME} or {@link
 * java.lang.annotation.RetentionPolicy#CLASS}, and must have either {@link
 * java.lang.annotation.ElementType#TYPE} or {@link java.lang.annotation.ElementType#FIELD}.
 */
public interface Annotation {
    /**
     * One or more strings that contain the annotation's (partial) class name. This can be the
     * annotation's fully qualified canonical name, or a substring thereof.
     *
     * <p>An annotation can be described by more than one partial class name. For
     * instance, @Nonnull, @NonNull and @NotNull have the same semantics; their partialClassNames
     * can be grouped together in one {@link Annotation} instance.
     *
     * @return A Set of potentially partial annotation class names.
     */
    public Set<String> partialClassNames();

    /**
     * Whether the annotation applies to the class in which is appears only, or whether it applies
     * to that class and all its subclasses.
     *
     * <p>Note: this encompasses more than {@link java.lang.annotation.Inherited} does: this flag
     * also applies, for example, to annotations on fields that are declared in a superclass.
     *
     * @return True if the annotation is inherited by subclasses of the class in which the
     *     annotation appears.
     */
    public boolean inherits();

    /**
     * Validates the annotation based on its properties.
     *
     * @param properties An object that contains information about the annotation.
     * @param annotationCache A cache containing all annotations for known types.
     * @param ignoredAnnotations A collection of type partialClassNames for annotations to ignore.
     * @return True if the annotation is valid and can be used as intended.
     */
    public default boolean validate(
            AnnotationProperties properties,
            AnnotationCache annotationCache,
            Set<String> ignoredAnnotations) {
        return true;
    }

    /**
     * Performs post processing after the annotation was added to the cache.
     *
     * @param types The types that the annotation applies to.
     * @param annotationCache A cache containing all annotations for known types.
     */
    public default void postProcess(Set<Class<?>> types, AnnotationCache annotationCache) {}
}
