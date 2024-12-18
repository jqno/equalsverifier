package nl.jqno.equalsverifier.internal.checkers;

import nl.jqno.equalsverifier.internal.checkers.fieldchecks.FieldCheck;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

public class FieldInspector<T> {

    private final Class<T> type;

    public FieldInspector(Class<T> type) {
        this.type = type;
    }

    public void check(FieldCheck<T> check) {
        for (FieldProbe fieldProbe : FieldIterable.of(type)) {
            check.execute(fieldProbe);
        }
    }
}
