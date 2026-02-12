package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertNotEquals;

import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class FloatAndDoubleFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;

    public FloatAndDoubleFieldCheck(SubjectCreator<T> subjectCreator) {
        this.subjectCreator = subjectCreator;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        Class<?> type = fieldProbe.getType();
        if (isFloat(type)) {
            T reference = subjectCreator.withFieldSetTo(fieldProbe.getField(), 0.0F);
            T copy = subjectCreator.withFieldSetTo(fieldProbe.getField(), -0.0F);
            assertNotEquals(
                Formatter.of("Float: equals doesn't use Float.compare for field %%.", fieldProbe.getDisplayName()),
                reference,
                copy);
        }
        if (isDouble(type)) {
            T reference = subjectCreator.withFieldSetTo(fieldProbe.getField(), 0.0D);
            T copy = subjectCreator.withFieldSetTo(fieldProbe.getField(), -0.0D);
            assertNotEquals(
                Formatter.of("Double: equals doesn't use Double.compare for field %%.", fieldProbe.getDisplayName()),
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
