package nl.jqno.equalsverifier.internal.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.Tuple;

class JavaLangValueSupplier<T> extends ValueSupplier<T> {
    public JavaLangValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    @SuppressFBWarnings(
            value = { "DM_STRING_CTOR", "DM_USELESS_THREAD" },
            justification = "We really do need a separate String instance with the same value and a Thread instance that we don't use.")
    public Optional<Tuple<T>> get() {
        if (is(Object.class)) {
            var red = new Object();
            return val(red, new Object(), red);
        }

        if (is(Enum.class)) {
            return val(Dummy.RED, Dummy.BLUE, Dummy.RED);
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
            return val(new Throwable(), new Throwable(), new Throwable());
        }

        return Optional.empty();
    }

    private enum Dummy {
        RED, BLUE
    }
}
