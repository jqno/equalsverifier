package nl.jqno.equalsverifier.internal.reflection.annotations;

import java.util.Set;

/**
 * Describes an annotation that can be recognised by EqualsVerifier.
 *
 * The annotation can have {@link java.lang.annotation.RetentionPolicy#RUNTIME}
 * or {@link java.lang.annotation.RetentionPolicy#CLASS}, and must have either
 * {@link java.lang.annotation.ElementType#TYPE} or
 * {@link java.lang.annotation.ElementType#FIELD}.
 */
public interface Annotation {
    /**
     * One or more strings that contain the annotation's class name. A
     * descriptor can be the annotation's fully qualified canonical name, or a
     * substring thereof.
     *
     * An annotation can be described by more than one descriptor. For
     * instance, @Nonnull, @NonNull and @NotNull have the same semantics; their
     * descriptors can be grouped together in one {@link Annotation} instance.
     *
     * @return An Iterable of annotation descriptor strings.
     */
    public Iterable<String> descriptors();

    /**
     * Whether the annotation applies to the class in which is appears only, or
     * whether it applies to that class and all its subclasses.
     *
     * @return True if the annotation is inherited by subclasses of the class
     *          in which the annotation appears.
     */
    public boolean inherits();

    /**
     * Validates the annotation based on its properties.
     *
     * @param properties An object that contains information about the annotation.
     * @param ignoredAnnotations A collection of type descriptors for annotations
     *          to ignore.
     * @return True if the annotation is valid and can be used as intended.
     */
    public boolean validate(AnnotationProperties properties, Set<String> ignoredAnnotations);
}
