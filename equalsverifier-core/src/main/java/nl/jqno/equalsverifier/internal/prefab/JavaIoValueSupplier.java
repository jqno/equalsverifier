package nl.jqno.equalsverifier.internal.prefab;

import java.io.File;
import java.io.PrintStream;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.Tuple;

class JavaIoValueSupplier<T> extends ValueSupplier<T> {
    public JavaIoValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    @SuppressFBWarnings(
            value = "DMI_HARDCODED_ABSOLUTE_FILENAME",
            justification = "We just need an instance of File; they're not for actual use.")
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
