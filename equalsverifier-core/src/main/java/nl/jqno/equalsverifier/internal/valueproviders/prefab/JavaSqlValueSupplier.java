package nl.jqno.equalsverifier.internal.valueproviders.prefab;

import java.sql.*;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

public class JavaSqlValueSupplier<T> extends ValueSupplier<T> {
    public JavaSqlValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Date.class)) {
            return val(new Date(1337), new Date(42), new Date(1337));
        }
        if (is(Time.class)) {
            return val(new Time(1337), new Time(42), new Time(1337));
        }
        if (is(Timestamp.class)) {
            return val(new Timestamp(1337), new Timestamp(42), new Timestamp(1337));
        }

        return Optional.empty();
    }

}
