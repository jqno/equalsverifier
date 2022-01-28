package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class BigDecimalFieldCheck<T> implements FieldCheck<T> {

    public static final String ERROR_DOC_TITLE = "BigDecimal equality";

    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public BigDecimalFieldCheck(CachedHashCodeInitializer<T> cachedHashCodeInitializer) {
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
    }

    @Override
    public void execute(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        if (BigDecimal.class.equals(fieldAccessor.getFieldType())) {
            Field field = fieldAccessor.getField();
            BigDecimal referenceField = (BigDecimal) referenceAccessor.getField(field);
            BigDecimal changedField = referenceField.setScale(
                referenceField.scale() + 1,
                RoundingMode.UNNECESSARY
            );
            ObjectAccessor<T> changed = copyAccessor.withFieldSetTo(field, changedField);

            T left = referenceAccessor.get();
            T right = changed.get();

            checkEquals(field, referenceField, changedField, left, right);
            checkHashCode(field, referenceField, changedField, left, right);
        }
    }

    private void checkEquals(
        Field field,
        BigDecimal referenceField,
        BigDecimal changedField,
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
            referenceField,
            changedField,
            Warning.BIGDECIMAL_EQUALITY
        );
        assertEquals(f, left, right);
    }

    private void checkHashCode(
        Field field,
        BigDecimal referenceField,
        BigDecimal changedField,
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
            referenceField,
            changedField,
            Warning.BIGDECIMAL_EQUALITY
        );
        int leftHashCode = cachedHashCodeInitializer.getInitializedHashCode(left);
        int rightHashCode = cachedHashCodeInitializer.getInitializedHashCode(right);
        assertEquals(f, leftHashCode, rightHashCode);
    }
}
