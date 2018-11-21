package equalsverifier.checkers.fieldchecks;

import equalsverifier.reflection.FieldAccessor;
import equalsverifier.utils.Formatter;

import static equalsverifier.utils.Assert.assertEquals;

public class FloatAndDoubleFieldCheck implements FieldCheck {
    @Override
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Class<?> type = referenceAccessor.getFieldType();
        if (isFloat(type)) {
            referenceAccessor.set(Float.NaN);
            changedAccessor.set(Float.NaN);
            assertEquals(Formatter.of("Float: equals doesn't use Float.compare for field %%.", referenceAccessor.getFieldName()),
                    referenceAccessor.getObject(), changedAccessor.getObject());
        }
        if (isDouble(type)) {
            referenceAccessor.set(Double.NaN);
            changedAccessor.set(Double.NaN);
            assertEquals(Formatter.of("Double: equals doesn't use Double.compare for field %%.", referenceAccessor.getFieldName()),
                    referenceAccessor.getObject(), changedAccessor.getObject());
        }
    }

    private boolean isFloat(Class<?> type) {
        return type == float.class || type == Float.class;
    }

    private boolean isDouble(Class<?> type) {
        return type == double.class || type == Double.class;
    }
}
