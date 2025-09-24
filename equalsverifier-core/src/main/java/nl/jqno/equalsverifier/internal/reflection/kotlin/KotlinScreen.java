package nl.jqno.equalsverifier.internal.reflection.kotlin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import nl.jqno.equalsverifier.internal.reflection.Util;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;

public final class KotlinScreen {
    private KotlinScreen() {}

    public static final Class<?> LAZY = Util.classForName("kotlin.Lazy");

    public static boolean isKotlin(Class<?> type) {
        Class<Annotation> annotation =
                Util.classForName(SupportedAnnotations.KOTLIN.partialClassNames().iterator().next());
        return annotation != null && type.isAnnotationPresent(annotation);
    }

    public static boolean isKotlinLazy(Field field) {
        return field.getType().getName().equals("kotlin.Lazy");
    }
}
