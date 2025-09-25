package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations.*;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinProbe;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;
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

    /**
     * Returns the field itself.
     *
     * @return The field itself.
     */
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
    public Object getValue(Object object) {
        field.setAccessible(true);
        return rethrow(() -> field.get(object));
    }

    /**
     * Returns the field's type.
     *
     * @return The field's type.
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * Returns the field's name.
     *
     * @return The field's name.
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Returns the field's display name. This accounts for Kotlin delegates.
     *
     * @return The field's display name.
     */
    public String getDisplayName() {
        if (KotlinScreen.isKotlin(field.getType())) {
            return KotlinProbe.getKotlinPropertyNameFor(field).orElse(field.getName());
        }
        return field.getName();
    }

    /**
     * Returns whether the field is primitive.
     *
     * @return Whether the field is of a primitive type.
     */
    public boolean isPrimitive() {
        return getType().isPrimitive();
    }

    /**
     * Returns whether the field is public.
     *
     * @return Whether the field has public visibility
     */
    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }

    /**
     * Returns whether the field is final.
     *
     * @return Whether the field is marked with the final modifier.
     */
    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    /**
     * Returns whether the field is static.
     *
     * @return Whether the field is marked with the static modifier.
     */
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * Returns whether the field is transient.
     *
     * @return Whether the field is marked with the transient modifier.
     */
    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    /**
     * Returns whether the field is a single-value enum.
     *
     * @return Whether the field is an enum with a single value.
     */
    public boolean isEmptyOrSingleValueEnum() {
        Class<?> type = field.getType();
        return type.isEnum() && type.getEnumConstants().length <= 1;
    }

    /**
     * Returns whether the field can be set to the default value for its type.
     *
     * @param config The configuration affects whether a given field van be default.
     * @return Whether the field can be set to the default value for its type.
     */
    public boolean canBeDefault(Configuration<?> config) {
        if (isPrimitive()) {
            return !config.prefabbedFields().contains(getName());
        }

        boolean isAnnotated = isAnnotatedNonnull(config.annotationCache());
        boolean isMentionedExplicitly = config.nonnullFields().contains(field.getName());
        return !config.warningsToSuppress().contains(Warning.NULL_FIELDS) && !isAnnotated && !isMentionedExplicitly;
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
        if (field.isSynthetic() && !KotlinScreen.isSyntheticKotlinDelegate(field)) {
            return false;
        }
        if (isFinal() && isStatic()) {
            return false;
        }
        return true;
    }
}
