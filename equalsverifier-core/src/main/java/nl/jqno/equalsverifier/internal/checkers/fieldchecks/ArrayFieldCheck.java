package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;

import java.lang.reflect.Array;

import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class ArrayFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public ArrayFieldCheck(SubjectCreator<T> subjectCreator, CachedHashCodeInitializer<T> cachedHashCodeInitializer) {
        this.subjectCreator = subjectCreator;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        Class<?> arrayType = fieldProbe.getType();
        if (!arrayType.isArray()) {
            return;
        }
        if (!fieldProbe.canBeModifiedReflectively()) {
            return;
        }

        T reference = subjectCreator.plain();
        T changed = replaceInnermostArrayValue(reference, fieldProbe);

        if (arrayType.getComponentType().isArray()) {
            assertDeep(fieldProbe.getDisplayName(), reference, changed);
        }
        else {
            assertArray(fieldProbe.getDisplayName(), reference, changed);
        }
    }

    private T replaceInnermostArrayValue(T reference, FieldProbe fieldProbe) {
        Object newArray = arrayCopy(fieldProbe.getValue(reference));
        return subjectCreator.withFieldSetTo(fieldProbe.getField(), newArray);
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
            }
            else {
                Array.set(result, i, Array.get(array, i));
            }
        }
        return result;
    }

    private void assertDeep(String fieldDisplayName, Object reference, Object changed) {
        Formatter eqEqFormatter = Formatter
                .of(
                    "Multidimensional array: ==, regular equals() or Arrays.equals() used"
                            + " instead of Arrays.deepEquals() for field %%.",
                    fieldDisplayName);
        assertEquals(eqEqFormatter, reference, changed);

        Formatter regularFormatter = Formatter
                .of(
                    "Multidimensional array: regular hashCode() or Arrays.hashCode() used"
                            + " instead of Arrays.deepHashCode() for field %%.",
                    fieldDisplayName);
        assertEquals(
            regularFormatter,
            cachedHashCodeInitializer.getInitializedHashCode(reference),
            cachedHashCodeInitializer.getInitializedHashCode(changed));
    }

    private void assertArray(String fieldDisplayName, Object reference, Object changed) {
        assertEquals(
            Formatter
                    .of(
                        "Array: == or regular equals() used instead of Arrays.equals() for field %%.",
                        fieldDisplayName),
            reference,
            changed);
        assertEquals(
            Formatter.of("Array: regular hashCode() used instead of Arrays.hashCode() for field %%.", fieldDisplayName),
            cachedHashCodeInitializer.getInitializedHashCode(reference),
            cachedHashCodeInitializer.getInitializedHashCode(changed));
    }
}
