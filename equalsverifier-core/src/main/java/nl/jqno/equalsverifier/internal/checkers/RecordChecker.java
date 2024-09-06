package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.internal.instantiation.ClassProbe;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Context;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class RecordChecker<T> implements Checker {

    private final Context<T> context;

    public RecordChecker(Context<T> context) {
        this.context = context;
    }

    @Override
    public void check() {
        ClassProbe<T> probe = context.getClassProbe();
        if (!probe.isRecord()) {
            return;
        }

        SubjectCreator<T> subjectCreator = context.getSubjectCreator();
        verifyRecordPrecondition(ObjectAccessor.of(subjectCreator.plain()));
        verifyRecordPrecondition(
            ObjectAccessor.of(subjectCreator.withAllFieldsDefaulted(context.getConfiguration()))
        );
    }

    private void verifyRecordPrecondition(ObjectAccessor<T> originalAccessor) {
        Class<T> type = context.getType();
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
                fail(Formatter.of("Record: failed to run accessor method: " + accessorMethod));
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
        return rethrow(() -> {
            Method result = type.getDeclaredMethod(f.getName());
            result.setAccessible(true);
            return result;
        });
    }
}
