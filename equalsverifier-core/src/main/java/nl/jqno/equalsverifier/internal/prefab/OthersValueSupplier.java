package nl.jqno.equalsverifier.internal.prefab;

import java.beans.PropertyChangeSupport;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

class OthersValueSupplier<T> extends ValueSupplier<T> {
    public OthersValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(DateTimeFormatter.class)) {
            return val(DateTimeFormatter.ISO_TIME, DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_TIME);
        }
        if (is(Pattern.class)) {
            return val(Pattern.compile("one"), Pattern.compile("two"), Pattern.compile("one"));
        }
        if (is(PropertyChangeSupport.class)) {
            return val(
                new PropertyChangeSupport("this"),
                new PropertyChangeSupport("that"),
                new PropertyChangeSupport("this"));
        }

        return Optional.empty();
    }
}
