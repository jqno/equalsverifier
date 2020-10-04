package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class FloatAndDoubleFieldCheck<T> implements FieldCheck<T> {
    @Override
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Class<?> type = referenceAccessor.getFieldType();
        if (isFloat(type)) {
            referenceAccessor.set(Float.NaN);
            changedAccessor.set(Float.NaN);
            assertEquals(
                    Formatter.of(
                            "Float: equals doesn't use Float.compare for field %%.",
                            referenceAccessor.getFieldName()),
                    referenceAccessor.getObject(),
                    changedAccessor.getObject());
        }
        if (isDouble(type)) {
            referenceAccessor.set(Double.NaN);
            changedAccessor.set(Double.NaN);
            assertEquals(
                    Formatter.of(
                            "Double: equals doesn't use Double.compare for field %%.",
                            referenceAccessor.getFieldName()),
                    referenceAccessor.getObject(),
                    changedAccessor.getObject());
        }
    }

    private boolean isFloat(Class<?> type) {
        return type == float.class || type == Float.class;
    }

    private boolean isDouble(Class<?> type) {
        return type == double.class || type == Double.class;
    }
}
