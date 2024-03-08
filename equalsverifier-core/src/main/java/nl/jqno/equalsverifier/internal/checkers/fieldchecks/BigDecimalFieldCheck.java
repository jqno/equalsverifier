package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class BigDecimalFieldCheck<T> implements FieldCheck<T> {

    public static final String ERROR_DOC_TITLE = "BigDecimal equality";

    private final SubjectCreator<T> subjectCreator;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public BigDecimalFieldCheck(
        SubjectCreator<T> subjectCreator,
        CachedHashCodeInitializer<T> cachedHashCodeInitializer
    ) {
        this.subjectCreator = subjectCreator;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
    }

    @Override
    public void execute(Field changedField) {
        if (BigDecimal.class.equals(changedField.getType())) {
            T left = subjectCreator.plain();
            ObjectAccessor<T> acc = ObjectAccessor.of(left);

            BigDecimal referenceValue = (BigDecimal) acc.getField(changedField);
            BigDecimal changedValue = referenceValue.setScale(
                referenceValue.scale() + 1,
                RoundingMode.UNNECESSARY
            );

            T right = subjectCreator.withFieldSetTo(changedField, changedValue);

            checkEquals(changedField, referenceValue, changedValue, left, right);
            checkHashCode(changedField, referenceValue, changedValue, left, right);
        }
    }

    private void checkEquals(
        Field field,
        BigDecimal referenceValue,
        BigDecimal changedValue,
        T left,
        T right
    ) {
        Formatter f = Formatter.of(
            ERROR_DOC_TITLE +
            ": object does not equal a copy of itself" +
            " where BigDecimal field %% has a value that is equal using compareTo: %% compared to %%" +
            "\nIf these values should be considered equal then use compareTo rather than equals for this field." +
            "\nIf these values should not be considered equal, suppress Warning.%% to disable this check.",
            field.getName(),
            referenceValue,
            changedValue,
            Warning.BIGDECIMAL_EQUALITY
        );
        assertEquals(f, left, right);
    }

    private void checkHashCode(
        Field field,
        BigDecimal referenceValue,
        BigDecimal changedValue,
        T left,
        T right
    ) {
        Formatter f = Formatter.of(
            ERROR_DOC_TITLE +
            ": hashCode of object does not equal hashCode of a copy of itself" +
            " where BigDecimal field %% has a value that is equal using compareTo: %% compared to %%" +
            "\nIf these values should be considered equal then make sure to derive the same constituent hashCode from this field." +
            "\nIf these values should not be considered equal, suppress Warning.%% to disable this check.",
            field.getName(),
            referenceValue,
            changedValue,
            Warning.BIGDECIMAL_EQUALITY
        );
        int leftHashCode = cachedHashCodeInitializer.getInitializedHashCode(left);
        int rightHashCode = cachedHashCodeInitializer.getInitializedHashCode(right);
        assertEquals(f, leftHashCode, rightHashCode);
    }
}
