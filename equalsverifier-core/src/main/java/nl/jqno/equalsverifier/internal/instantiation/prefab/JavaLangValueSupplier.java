package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

// CHECKSTYLE OFF: NPathComplexity

public class JavaLangValueSupplier<T> extends ValueSupplier<T> {
    public JavaLangValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Object.class)) {
            var red = new Object();
            return val(red, new Object(), red);
        }

        if (is(Class.class)) {
            return val(Class.class, Object.class, Class.class);
        }
        if (is(Enum.class)) {
            return val(Dummy.RED, Dummy.BLUE, Dummy.RED);
        }
        if (is(Exception.class)) {
            var red = new Exception();
            return val(red, new Exception(), red);
        }
        if (is(RuntimeException.class)) {
            var red = new RuntimeException();
            return val(red, new RuntimeException(), red);
        }
        if (is(String.class)) {
            return val("one", "two", new String("one"));
        }
        if (is(StringBuilder.class)) {
            return val(new StringBuilder("one"), new StringBuilder("two"), new StringBuilder("three"));
        }
        if (is(Thread.class)) {
            return val(new Thread("one"), new Thread("two"), new Thread("one"));
        }
        if (is(Throwable.class)) {
            var red = new Throwable();
            return val(red, new Throwable(), red);
        }

        return Optional.empty();
    }

    private enum Dummy {
        RED, BLUE
    }
}
