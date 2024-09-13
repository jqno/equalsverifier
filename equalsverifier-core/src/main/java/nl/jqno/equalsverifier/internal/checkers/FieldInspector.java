package nl.jqno.equalsverifier.internal.checkers;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.FieldCheck;
import nl.jqno.equalsverifier.internal.instantiation.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;

public class FieldInspector<T> {

    private final Class<T> type;

    public FieldInspector(Class<T> type) {
        this.type = type;
    }

    public void check(FieldCheck<T> check) {
        for (Field field : FieldIterable.of(type)) {
            FieldProbe fieldProbe = FieldProbe.of(field);
            check.execute(fieldProbe);
        }
    }
}
