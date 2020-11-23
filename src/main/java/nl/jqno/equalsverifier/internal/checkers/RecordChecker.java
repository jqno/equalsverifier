package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.*;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class RecordChecker<T> implements Checker {

    private final Configuration<T> config;

    public RecordChecker(Configuration<T> config) {
        this.config = config;
    }

    @Override
    public void check() {
        ClassAccessor<T> accessor = config.getClassAccessor();
        if (!accessor.isRecord()) {
            return;
        }

        verifyRecordPrecondition(accessor.getRedAccessor(config.getTypeTag()));
        verifyRecordPrecondition(
            accessor.getDefaultValuesAccessor(
                config.getTypeTag(),
                config.getNonnullFields(),
                config.getAnnotationCache()
            )
        );
    }

    private void verifyRecordPrecondition(ObjectAccessor<T> originalAccessor) {
        Class<T> type = config.getType();
        T original = originalAccessor.get();
        T copy = originalAccessor.copy();

        if (original.equals(copy)) {
            return;
        }

        List<String> failedFields = new ArrayList<>();
        for (Field f : FieldIterable.of(type)) {
            Method accessorMethod = getAccessorMethodFor(type, f);
            try {
                Object originalField = accessorMethod.invoke(original);
                Object copyField = accessorMethod.invoke(copy);
                if (!originalField.equals(copyField)) {
                    failedFields.add(f.getName());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail(Formatter.of("Record: failed to invoke accessor method: " + accessorMethod));
            }
        }

        fail(
            Formatter.of(
                "Record invariant: constructor invariant failed for field%%: %%",
                failedFields.size() > 1 ? "s" : "",
                failedFields.stream().collect(Collectors.joining(","))
            )
        );
    }

    private Method getAccessorMethodFor(Class<T> type, Field f) {
        return rethrow(
            () -> {
                Method result = type.getDeclaredMethod(f.getName());
                result.setAccessible(true);
                return result;
            }
        );
    }
}
