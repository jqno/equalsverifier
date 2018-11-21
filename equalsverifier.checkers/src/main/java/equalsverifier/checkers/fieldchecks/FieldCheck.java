package equalsverifier.checkers.fieldchecks;

import equalsverifier.reflection.FieldAccessor;

@FunctionalInterface
public interface FieldCheck {
    void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor);
}
