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
        Constructor<T> constructor = getConstructorFor(config.getType());
        verifyRecordPrecondition(accessor.getRedAccessor(config.getTypeTag()), constructor);
    }

    private void verifyRecordPrecondition(
            ObjectAccessor<T> originalAccessor, Constructor<T> constructor) {
        Class<T> type = config.getType();
        T original = originalAccessor.get();
        T copy = callConstructor(type, constructor, originalAccessor);

        if (original.equals(copy)) {
            return;
        }

        List<String> failedFields = new ArrayList<>();
        for (Field f : FieldIterable.of(type)) {
            Method accessor = getAccessorFor(type, f);
            try {
                Object originalField = accessor.invoke(original);
                Object copyField = accessor.invoke(copy);
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

    private static <T> Constructor<T> getConstructorFor(Class<T> type) {
        try {
            List<Class<?>> constructorTypes =
                    StreamSupport.stream(FieldIterable.of(type).spliterator(), false)
                            .map(Field::getType)
                            .collect(Collectors.toList());
            Constructor<T> constructor =
                    type.getConstructor(constructorTypes.toArray(new Class<?>[0]));
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException | SecurityException e) {
            fail(Formatter.of("Record: could not find suitable constructor."), e);
            return null;
        }
    }

    private T callConstructor(
            Class<T> type, Constructor<T> constructor, ObjectAccessor<T> accessor) {
        List<?> params =
                StreamSupport.stream(FieldIterable.of(type).spliterator(), false)
                        .map(accessor::fieldAccessorFor)
                        .map(FieldAccessor::get)
                        .collect(Collectors.toList());

        try {
            return constructor.newInstance(params.toArray(new Object[0]));
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            fail(Formatter.of("Record: failed to invoke constructor."), e);
            return null;
        }
    }

    private Method getAccessorFor(Class<T> type, Field f) {
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
