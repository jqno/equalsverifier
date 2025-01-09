package nl.jqno.equalsverifier.internal.checkers;

import nl.jqno.equalsverifier.internal.checkers.fieldchecks.FieldCheck;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

public class FieldInspector<T> {

    private final Class<T> type;
    private final boolean isKotlin;

    public FieldInspector(Class<T> type, boolean isKotlin) {
        this.type = type;
        this.isKotlin = isKotlin;
    }

    public void check(FieldCheck<T> check) {
        FieldIterable it = isKotlin ? FieldIterable.ofKotlin(type) : FieldIterable.of(type);
        for (FieldProbe fieldProbe : it) {
            check.execute(fieldProbe);
        }
    }
}
