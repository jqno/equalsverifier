package nl.jqno.equalsverifier.internal.valueproviders.prefab;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.net.*;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.NoValueException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.util.Rethrow.ThrowingSupplier;

public class JavaNetValueSupplier<T> extends ValueSupplier<T> {
    public JavaNetValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(InetAddress.class)) {
            return wrap(
                InetAddress.class,
                () -> val(
                    InetAddress.getLoopbackAddress(),
                    InetAddress.getByName("127.0.0.42"),
                    InetAddress.getLoopbackAddress()));
        }
        if (is(Inet4Address.class)) {
            return wrap(
                Inet4Address.class,
                () -> val(
                    Inet4Address.getByName("127.0.0.1"),
                    Inet4Address.getByName("127.0.0.42"),
                    Inet4Address.getByName("127.0.0.1")));
        }
        if (is(Inet6Address.class)) {
            return wrap(
                Inet6Address.class,
                () -> val(Inet6Address.getByName("::1"), Inet6Address.getByName("::"), Inet6Address.getByName("::1")));
        }
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

    private Optional<Tuple<T>> wrap(Class<?> type, ThrowingSupplier<Optional<Tuple<T>>> supplier) {
        try {
            return supplier.get();
        }
        catch (Exception e) {
            throw new NoValueException(
                    "Could not construct a value for " + type.getSimpleName() + ": got " + e.getClass().getSimpleName()
                            + ". Please add prefab values for this type.",
                    e);
        }
    }
}
