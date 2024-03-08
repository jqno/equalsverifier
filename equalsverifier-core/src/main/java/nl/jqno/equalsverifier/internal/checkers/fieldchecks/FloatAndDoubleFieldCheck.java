package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class FloatAndDoubleFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;

    public FloatAndDoubleFieldCheck(SubjectCreator<T> subjectCreator) {
        this.subjectCreator = subjectCreator;
    }

    @Override
    public void execute(Field changedField) {
        Class<?> type = changedField.getType();
        if (isFloat(type)) {
            T reference = subjectCreator.withFieldSetTo(changedField, Float.NaN);
            T copy = subjectCreator.withFieldSetTo(changedField, Float.NaN);
            assertEquals(
                Formatter.of(
                    "Float: equals doesn't use Float.compare for field %%.",
                    changedField.getName()
                ),
                reference,
                copy
            );
        }
        if (isDouble(type)) {
            T reference = subjectCreator.withFieldSetTo(changedField, Double.NaN);
            T copy = subjectCreator.withFieldSetTo(changedField, Double.NaN);
            assertEquals(
                Formatter.of(
                    "Double: equals doesn't use Double.compare for field %%.",
                    changedField.getName()
                ),
                reference,
                copy
            );
        }
    }

    private boolean isFloat(Class<?> type) {
        return type == float.class || type == Float.class;
    }

    private boolean isDouble(Class<?> type) {
        return type == double.class || type == Double.class;
    }
}
