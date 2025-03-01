package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.text.*;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

class JavaTextValueSupplier<T> extends ValueSupplier<T> {
    public JavaTextValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(DateFormat.class)) {
            return val(DateFormat.getTimeInstance(), DateFormat.getDateInstance(), DateFormat.getTimeInstance());
        }
        if (is(DecimalFormat.class)) {
            return val(new DecimalFormat("x0.0"), new DecimalFormat("y0.0"), new DecimalFormat("x0.0"));
        }
        if (is(NumberFormat.class)) {
            return val(new DecimalFormat("x0.0"), new DecimalFormat("y0.0"), new DecimalFormat("x0.0"));
        }
        if (is(SimpleDateFormat.class)) {
            return val(new SimpleDateFormat("yMd"), new SimpleDateFormat("dMy"), new SimpleDateFormat("yMd"));
        }

        return Optional.empty();
    }
}
