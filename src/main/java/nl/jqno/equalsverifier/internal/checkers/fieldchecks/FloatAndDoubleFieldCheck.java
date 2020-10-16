package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class FloatAndDoubleFieldCheck<T> implements FieldCheck<T> {
    @Override
    public void execute(
            ObjectAccessor<T> referenceAccessor,
            ObjectAccessor<T> copyAccessor,
            FieldAccessor fieldAccessor) {
        Class<?> type = fieldAccessor.getFieldType();
        Field field = fieldAccessor.getField();
        if (isFloat(type)) {
            T reference = referenceAccessor.withFieldSetTo(field, Float.NaN).get();
            T copy = copyAccessor.withFieldSetTo(field, Float.NaN).get();
            assertEquals(
                    Formatter.of(
                            "Float: equals doesn't use Float.compare for field %%.",
                            field.getName()),
                    reference,
                    copy);
        }
        if (isDouble(type)) {
            T reference = referenceAccessor.withFieldSetTo(field, Double.NaN).get();
            T copy = copyAccessor.withFieldSetTo(field, Double.NaN).get();
            assertEquals(
                    Formatter.of(
                            "Double: equals doesn't use Double.compare for field %%.",
                            field.getName()),
                    reference,
                    copy);
        }
    }

    private boolean isFloat(Class<?> type) {
        return type == float.class || type == Float.class;
    }

    private boolean isDouble(Class<?> type) {
        return type == double.class || type == Double.class;
    }
}
