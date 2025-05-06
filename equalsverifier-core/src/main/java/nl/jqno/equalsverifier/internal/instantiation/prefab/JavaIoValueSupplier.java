package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.io.File;
import java.io.PrintStream;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

class JavaIoValueSupplier<T> extends ValueSupplier<T> {
    public JavaIoValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(File.class)) {
            return val(new File(""), new File("/"), new File(""));
        }
        if (is(PrintStream.class)) {
            return val(System.out, System.err, System.out);
        }

        return Optional.empty();
    }

}
