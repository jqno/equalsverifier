package nl.jqno.equalsverifier.internal.checkers;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.FieldCheck;
import nl.jqno.equalsverifier.internal.instantiation.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.util.Configuration;

public class FieldInspector<T> {

    private final Class<T> type;
    private final Configuration<T> config;

    public FieldInspector(Class<T> type, Configuration<T> config) {
        this.type = type;
        this.config = config;
    }

    public void check(FieldCheck<T> check) {
        for (Field field : FieldIterable.of(type)) {
            FieldProbe fieldProbe = FieldProbe.of(field, config);
            check.execute(fieldProbe);
        }
    }
}
