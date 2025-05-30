package nl.jqno.equalsverifier.internal.instantiation.prefab;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

class JavaLangReflectValueSupplier<T> extends ValueSupplier<T> {
    public JavaLangReflectValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Constructor.class)) {
            return rethrow(() -> {
                Constructor<?> c1 = JavaApiReflectionClassesContainer.class.getDeclaredConstructor();
                Constructor<?> c2 = JavaApiReflectionClassesContainer.class.getDeclaredConstructor(Object.class);
                return val(c1, c2, c1);
            }, e -> "Can't add prefab values for java.lang.reflect.Constructor");
        }
        if (is(Field.class)) {
            return rethrow(() -> {
                Field f1 = JavaApiReflectionClassesContainer.class.getDeclaredField("a");
                Field f2 = JavaApiReflectionClassesContainer.class.getDeclaredField("b");
                return val(f1, f2, f1);
            }, e -> "Can't add prefab values for java.lang.reflect.Field");
        }
        if (is(Method.class)) {
            return rethrow(() -> {
                Method m1 = JavaApiReflectionClassesContainer.class.getDeclaredMethod("m1");
                Method m2 = JavaApiReflectionClassesContainer.class.getDeclaredMethod("m2");
                return val(m1, m2, m1);
            }, e -> "Can't add prefab values for java.lang.reflect.Method");
        }

        return Optional.empty();
    }

    @SuppressWarnings("unused")
    private static class JavaApiReflectionClassesContainer {

        Object a;

        Object b;

        public JavaApiReflectionClassesContainer() {}

        public JavaApiReflectionClassesContainer(Object o) {}

        void m1() {}

        void m2() {}
    }
}
