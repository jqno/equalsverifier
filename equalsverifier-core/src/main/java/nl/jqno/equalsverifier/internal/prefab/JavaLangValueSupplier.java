package nl.jqno.equalsverifier.internal.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.Tuple;

class JavaLangValueSupplier<T> extends ValueSupplier<T> {
    public JavaLangValueSupplier(Class<T> type) {
        super(type);
    }

    @SuppressFBWarnings(
            value = "DM_STRING_CTOR",
            justification = "We really do need a separate instance with the same value")
    public Optional<Tuple<T>> get() {
        if (is(Object.class)) {
            var red = new Object();
            return val(red, new Object(), red);
        }
        if (is(String.class)) {
            return val("one", "two", new String("one"));
        }
        if (is(Enum.class)) {
            return val(Dummy.RED, Dummy.BLUE, Dummy.RED);
        }

        return Optional.empty();
    }

    private enum Dummy {
        RED, BLUE
    }
}
