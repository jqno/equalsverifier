package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
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
        Constructor<T> constructor = accessor.getRecordConstructor();
        if (constructor == null) {
            fail(Formatter.of("Record: could not find suitable constructor."));
        }

        verifyRecordPrecondition(
                accessor, accessor.getRedAccessor(config.getTypeTag()), constructor);
        verifyRecordPrecondition(
                accessor,
                accessor.getDefaultValuesAccessor(
                        config.getTypeTag(),
                        config.getNonnullFields(),
                        config.getAnnotationCache()),
                constructor);
    }

    private void verifyRecordPrecondition(
            ClassAccessor<T> accessor,
            ObjectAccessor<T> originalAccessor,
            Constructor<T> constructor) {
        Class<T> type = config.getType();
        T original = originalAccessor.get();
        List<?> params =
                StreamSupport.stream(FieldIterable.of(type).spliterator(), false)
                        .map(originalAccessor::fieldAccessorFor)
                        .map(FieldAccessor::get)
                        .collect(Collectors.toList());
        T copy = accessor.callRecordConstructor(constructor, params);
        if (copy == null) {
            fail(Formatter.of("Record: failed to invoke constructor."));
        }

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
                fail(Formatter.of("Record: failed to invoke accessor method"));
            }
        }

        fail(
                Formatter.of(
                        "Record invariant: constructor invariant failed for field%%: %%",
                        failedFields.size() > 1 ? "s" : "",
                        failedFields.stream().collect(Collectors.joining(","))));
    }

    private Method getAccessorMethodFor(Class<T> type, Field f) {
        try {
            Method result = type.getDeclaredMethod(f.getName());
            result.setAccessible(true);
            return result;
        } catch (NoSuchMethodException | SecurityException e) {
            fail(Formatter.of("Record: failed to find accessor method for field %%", f.getName()));
            return null;
        }
    }
}
