package equalsverifier.reflection.annotations;

import java.lang.reflect.Field;

import static equalsverifier.reflection.annotations.SupportedAnnotations.*;

/**
 * Utility class that checks whether a field is marked with an Nonnull
 * annotation of some sort.
 */
public final class NonnullAnnotationVerifier {
    private NonnullAnnotationVerifier() {}

    /**
     * Checks whether the given field is marked with an Nonnull annotation,
     * whether directly, or through some default annotation mechanism.
     *
     * @param field The field to be checked.
     * @param annotationCache To provide access to the annotations on the field
     *          and the field's class
     * @return True if the field is to be treated as Nonnull.
     */
    public static boolean fieldIsNonnull(Field field, AnnotationCache annotationCache) {
        Class<?> type = field.getDeclaringClass();
        if (annotationCache.hasFieldAnnotation(type, field.getName(), NONNULL)) {
            return true;
        }
        if (annotationCache.hasFieldAnnotation(type, field.getName(), NULLABLE)) {
            return false;
        }
        return annotationCache.hasClassAnnotation(type, FINDBUGS1X_DEFAULT_ANNOTATION_NONNULL) ||
                annotationCache.hasClassAnnotation(type, JSR305_DEFAULT_ANNOTATION_NONNULL) ||
                annotationCache.hasClassAnnotation(type, ECLIPSE_DEFAULT_ANNOTATION_NONNULL);
    }
}
