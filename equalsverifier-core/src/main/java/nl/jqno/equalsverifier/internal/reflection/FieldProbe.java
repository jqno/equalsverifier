package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations.*;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.util.Configuration;

/**
 * Provides read-only reflective access to one field of an object.
 */
public final class FieldProbe {

    private final Field field;

    /** Private constructor. Call {@link #of(Field)} to instantiate. */
    private FieldProbe(Field field) {
        this.field = field;
    }

    /**
     * Factory method.
     *
     * @param field The field to access.
     * @return A {@link FieldProbe} for {@link #field}.
     */
    public static FieldProbe of(Field field) {
        return new FieldProbe(field);
    }

    /** @return The field itself. */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Can't defensively copy a Field.")
    public Field getField() {
        return field;
    }

    /**
     * Tries to get the field's value.
     *
     * @param object The object that contains the field whose value we want to get.
     * @return The field's value.
     * @throws ReflectionException If the operation fails.
     */
    @SuppressFBWarnings(
            value = "DP_DO_INSIDE_DO_PRIVILEGED",
            justification = "Only called in test code, not production.")
    public Object getValue(Object object) {
        field.setAccessible(true);
        return rethrow(() -> field.get(object));
    }

    /** @return The field's type. */
    public Class<?> getType() {
        return field.getType();
    }

    /** @return The field's name. */
    public String getName() {
        return field.getName();
    }

    /** @return Whether the field is of a primitive type. */
    public boolean isPrimitive() {
        return getType().isPrimitive();
    }

    /** @return Whether the field has public visibility */
    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }

    /** @return Whether the field is marked with the final modifier. */
    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    /** @return Whether the field is marked with the static modifier. */
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    /** @return Whether the field is marked with the transient modifier. */
    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    /** @return Whether the field is an enum with a single value. */
    public boolean isEmptyOrSingleValueEnum() {
        Class<?> type = field.getType();
        return type.isEnum() && type.getEnumConstants().length <= 1;
    }

    /**
     * @param config The configuration affects whether a given field van be default.
     * @return Whether the field can be set to the default value for its type.
     */
    public boolean canBeDefault(Configuration<?> config) {
        if (isPrimitive()) {
            return !config.getPrefabbedFields().contains(getName());
        }

        boolean isAnnotated = isAnnotatedNonnull(config.getAnnotationCache());
        boolean isMentionedExplicitly = config.getNonnullFields().contains(field.getName());
        return !config.getWarningsToSuppress().contains(Warning.NULL_FIELDS) && !isAnnotated && !isMentionedExplicitly;
    }

    /**
     * Checks whether the given field is marked with an Nonnull annotation, whether directly, or through some default
     * annotation mechanism.
     *
     * @param annotationCache To retrieve annotations from.
     * @return True if the field is to be treated as Nonnull.
     */
    public boolean isAnnotatedNonnull(AnnotationCache annotationCache) {
        Class<?> type = field.getDeclaringClass();
        if (annotationCache.hasFieldAnnotation(type, field.getName(), NONNULL)) {
            return true;
        }
        if (annotationCache.hasFieldAnnotation(type, field.getName(), NULLABLE)) {
            return false;
        }
        boolean hasFindbugsAnnotation = annotationCache.hasClassAnnotation(type, FINDBUGS1X_DEFAULT_ANNOTATION_NONNULL);
        boolean hasJsr305Annotation = annotationCache.hasClassAnnotation(type, JSR305_DEFAULT_ANNOTATION_NONNULL);
        boolean hasDefaultAnnotation = annotationCache.hasClassAnnotation(type, DEFAULT_ANNOTATION_NONNULL);
        return hasFindbugsAnnotation || hasJsr305Annotation || hasDefaultAnnotation;
    }

    /**
     * Determines whether the field can be modified using reflection.
     *
     * @return Whether or not the field can be modified reflectively.
     */
    public boolean canBeModifiedReflectively() {
        if (field.isSynthetic()) {
            return false;
        }
        if (isFinal() && isStatic()) {
            return false;
        }
        return true;
    }
}
