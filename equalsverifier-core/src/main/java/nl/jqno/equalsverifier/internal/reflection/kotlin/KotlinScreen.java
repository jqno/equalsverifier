package nl.jqno.equalsverifier.internal.reflection.kotlin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import nl.jqno.equalsverifier.internal.reflection.Util;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;

/**
 * Collection of 'safe' reflection utilities for Kotlin. Can be used regardless of whether the `kotlin-reflect` library
 * is available on the classpath. Therefore, it can be used as a screen to check if we can use {@link KotlinProbe}.
 */
public final class KotlinScreen {
    private KotlinScreen() {}

    public static final Class<?> LAZY = Util.classForName("kotlin.Lazy");

    public static boolean isKotlin(Class<?> type) {
        // We can't use the `AnnotationCache` here because we need to check for Kotlin before the `AnnotationCache`
        // has been built. Fortunately, the check involves an annotation that doesn't need ASM to be detected.
        for (var name : SupportedAnnotations.KOTLIN.partialClassNames()) {
            Class<Annotation> annotation = Util.classForName(name);
            if (annotation != null && type.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSyntheticKotlinDelegate(Field field) {
        return field.isSynthetic() && field.getName().startsWith("$$delegate_");
    }

    public static boolean isKotlinLazy(Field field) {
        return field.getType().getName().equals("kotlin.Lazy");
    }
}
