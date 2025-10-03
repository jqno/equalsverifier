package nl.jqno.equalsverifier.internal.reflection.kotlin;

import kotlin.Lazy;
import kotlin.LazyKt;

/**
 * Helper class for constructing lazy Kotlin values.
 */
public final class KotlinLazy {
    private KotlinLazy() {}

    public static <T> Lazy<T> lazy(T value) {
        return LazyKt.lazyOf(value);
    }
}
