package nl.jqno.equalsverifier.internal.instantiation;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;

public class FieldMutator {

    private final FieldProbe probe;
    private final Field field;

    public FieldMutator(FieldProbe probe) {
        this.probe = probe;
        this.field = probe.getField();
    }

    public void setNewValue(Object object, Object newValue) {
        rethrow(() -> {
            if (probe.canBeModifiedReflectively()) {
                field.setAccessible(true);
                field.set(object, newValue);
            }
        });
    }
}
