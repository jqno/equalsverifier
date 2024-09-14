package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

@FunctionalInterface
public interface FieldCheck<T> {
    void execute(FieldProbe fieldProbe);
}
