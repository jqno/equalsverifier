package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class ArrayFieldCheck<T> implements FieldCheck<T> {
    private CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public ArrayFieldCheck(CachedHashCodeInitializer<T> cachedHashCodeInitializer) {
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
    }

    @Override
    public void execute(
            ObjectAccessor<T> referenceAccessor,
            ObjectAccessor<T> copyAccessor,
            FieldAccessor fieldAccessor) {
        Class<?> arrayType = fieldAccessor.getFieldType();
        if (!arrayType.isArray()) {
            return;
        }
        if (!fieldAccessor.canBeModifiedReflectively()) {
            return;
        }

        String fieldName = fieldAccessor.getFieldName();
        T reference = referenceAccessor.get();
        T changed = replaceInnermostArrayValue(copyAccessor, fieldAccessor.getField()).get();

        if (arrayType.getComponentType().isArray()) {
            assertDeep(fieldName, reference, changed);
        } else {
            assertArray(fieldName, reference, changed);
        }
    }

    private ObjectAccessor<T> replaceInnermostArrayValue(ObjectAccessor<T> accessor, Field field) {
        Object newArray = arrayCopy(accessor.getField(field));
        return accessor.withFieldSetTo(field, newArray);
    }

    private Object arrayCopy(Object array) {
        if (array == null) {
            return null;
        }
        Class<?> componentType = array.getClass().getComponentType();
        int length = Array.getLength(array);
        Object result = Array.newInstance(componentType, length);
        for (int i = 0; i < length; i++) {
            if (componentType.isArray()) {
                Array.set(result, i, arrayCopy(Array.get(array, i)));
            } else {
                Array.set(result, i, Array.get(array, i));
            }
        }
        return result;
    }

    private void assertDeep(String fieldName, Object reference, Object changed) {
        Formatter eqEqFormatter =
                Formatter.of(
                        "Multidimensional array: ==, regular equals() or Arrays.equals() used"
                                + " instead of Arrays.deepEquals() for field %%.",
                        fieldName);
        assertEquals(eqEqFormatter, reference, changed);

        Formatter regularFormatter =
                Formatter.of(
                        "Multidimensional array: regular hashCode() or Arrays.hashCode() used"
                                + " instead of Arrays.deepHashCode() for field %%.",
                        fieldName);
        assertEquals(
                regularFormatter,
                cachedHashCodeInitializer.getInitializedHashCode(reference),
                cachedHashCodeInitializer.getInitializedHashCode(changed));
    }

    private void assertArray(String fieldName, Object reference, Object changed) {
        assertEquals(
                Formatter.of(
                        "Array: == or regular equals() used instead of Arrays.equals() for field"
                                + " %%.",
                        fieldName),
                reference,
                changed);
        assertEquals(
                Formatter.of(
                        "Array: regular hashCode() used instead of Arrays.hashCode() for field %%.",
                        fieldName),
                cachedHashCodeInitializer.getInitializedHashCode(reference),
                cachedHashCodeInitializer.getInitializedHashCode(changed));
    }
}
