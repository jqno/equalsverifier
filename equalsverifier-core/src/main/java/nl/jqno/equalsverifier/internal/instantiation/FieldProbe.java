package nl.jqno.equalsverifier.internal.instantiation;

import static nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations.*;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.util.Configuration;

/**
 * Provides read-only reflective access to one field of an object.
 */
public final class FieldProbe {

    private final Field field;
    private final boolean isWarningZeroSuppressed;
    private final boolean isWarningNullSuppressed;
    private final Set<String> nonnullFields;
    private final AnnotationCache annotationCache;

    /** Private constructor. Call {@link #of(Field, Configuration)} to instantiate. */
    private FieldProbe(Field field, Configuration<?> config) {
        this.field = field;
        this.isWarningZeroSuppressed = config.getWarningsToSuppress().contains(Warning.ZERO_FIELDS);
        this.isWarningNullSuppressed = config.getWarningsToSuppress().contains(Warning.NULL_FIELDS);
        this.nonnullFields = config.getNonnullFields();
        this.annotationCache = config.getAnnotationCache();
    }

    /**
     * Factory method.
     *
     * @param field The field to access.
     * @param config A configuration object; cannot be null.
     * @return A {@link FieldProbe} for {@link #field}.
     */
    public static FieldProbe of(Field field, Configuration<?> config) {
        return new FieldProbe(field, config);
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
        justification = "Only called in test code, not production."
    )
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

    /** @return Whether the field can be set to the default value for its type. */
    public boolean canBeDefault() {
        if (isPrimitive()) {
            return !isWarningZeroSuppressed;
        }

        boolean isAnnotated = isAnnotatedNonnull();
        boolean isMentionedExplicitly = nonnullFields.contains(field.getName());
        return !isWarningNullSuppressed && !isAnnotated && !isMentionedExplicitly;
    }

    /**
     * Checks whether the given field is marked with an Nonnull annotation, whether directly, or
     * through some default annotation mechanism.
     *
     * @return True if the field is to be treated as Nonnull.
     */
    public boolean isAnnotatedNonnull() {
        Class<?> type = field.getDeclaringClass();
        if (annotationCache.hasFieldAnnotation(type, field.getName(), NONNULL)) {
            return true;
        }
        if (annotationCache.hasFieldAnnotation(type, field.getName(), NULLABLE)) {
            return false;
        }
        boolean hasFindbugsAnnotation = annotationCache.hasClassAnnotation(
            type,
            FINDBUGS1X_DEFAULT_ANNOTATION_NONNULL
        );
        boolean hasJsr305Annotation = annotationCache.hasClassAnnotation(
            type,
            JSR305_DEFAULT_ANNOTATION_NONNULL
        );
        boolean hasDefaultAnnotation = annotationCache.hasClassAnnotation(
            type,
            DEFAULT_ANNOTATION_NONNULL
        );
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
