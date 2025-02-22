package nl.jqno.equalsverifier.internal.prefab;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

public class JavaNetValueSupplier<T> extends ValueSupplier<T> {
    public JavaNetValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(InetSocketAddress.class)) {
            return val(
                InetSocketAddress.createUnresolved("localhost", 8080),
                InetSocketAddress.createUnresolved("127.0.0.1", 8080),
                InetSocketAddress.createUnresolved("localhost", 8080));
        }
        if (is(URI.class)) {
            return val(URI.create("x"), URI.create("y"), URI.create("x"));
        }
        if (is(URL.class)) {
            return rethrow(
                () -> val(new URL("http://example.com"), new URL("http://localhost"), new URL("http://example.com")),
                e -> "Can't add prefab values for java.net.URL");
        }

        return Optional.empty();
    }

}
