package nl.jqno.equalsverifier.internal.reflection;

import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;

public final class KotlinProbe {
    private KotlinProbe() {}

    public static boolean isKotlin(Class<?> type, AnnotationCache annotationCache) {
        return annotationCache.hasClassAnnotation(type, SupportedAnnotations.KOTLIN);
    }
}
