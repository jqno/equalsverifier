package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.FieldMutator;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.util.*;

@SuppressFBWarnings(
    value = "RV_RETURN_VALUE_IGNORED",
    justification = "We only want to see if it throws an exception."
)
public class NullPointerExceptionFieldCheck<T> implements FieldCheck<T> {

    private final Configuration<T> config;
    private final SubjectCreator<T> subjectCreator;

    public NullPointerExceptionFieldCheck(Context<T> context) {
        this.config = context.getConfiguration();
        this.subjectCreator = context.getSubjectCreator();
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        if (config.getNonnullFields().contains(fieldProbe.getName())) {
            return;
        }
        if (fieldProbe.isPrimitive()) {
            return;
        }
        if (fieldProbe.isAnnotatedNonnull(config.getAnnotationCache())) {
            return;
        }

        if (fieldProbe.isStatic()) {
            T reference = subjectCreator.plain();
            FieldMutator fieldMutator = new FieldMutator(fieldProbe);
            Object saved = fieldProbe.getValue(reference);

            fieldMutator.setNewValue(
                reference,
                PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(fieldProbe.getType())
            );
            performTests(fieldProbe.getField(), subjectCreator.plain(), subjectCreator.plain());
            fieldMutator.setNewValue(reference, saved);
        } else {
            T changed = subjectCreator.withFieldDefaulted(fieldProbe.getField());
            performTests(fieldProbe.getField(), subjectCreator.plain(), changed);
        }
    }

    private void performTests(Field field, final Object reference, final Object changed) {
        handle("equals", "the parameter's field", field, () -> reference.equals(changed));
        handle("equals", "the 'this' object's field", field, () -> changed.equals(reference));
        handle(
            "hashCode",
            "field",
            field,
            () -> config.getCachedHashCodeInitializer().getInitializedHashCode(changed)
        );
    }

    @SuppressFBWarnings(
        value = "DCN_NULLPOINTER_EXCEPTION",
        justification = "We're catching and wrapping it to provide better output to the user."
    )
    private void handle(String testedMethodName, String whichOne, Field field, Runnable r) {
        try {
            r.run();
        } catch (NullPointerException e) {
            npeThrown(testedMethodName, whichOne, field, e);
        } catch (AbstractMethodError e) {
            abstractMethodErrorThrown(testedMethodName, field, e);
        } catch (ClassCastException e) {
            classCastExceptionThrown(field, e);
        } catch (Exception e) {
            exceptionThrown(testedMethodName, field, e);
        }
    }

    private void npeThrown(String method, String whichOne, Field field, NullPointerException e) {
        Formatter f = Formatter.of(
            "Non-nullity: %% throws NullPointerException on %% %%.",
            method,
            whichOne,
            field.getName()
        );
        fail(f, e);
    }

    private void abstractMethodErrorThrown(String method, Field field, AbstractMethodError e) {
        Formatter f = Formatter.of(
            "Abstract delegation: %% throws AbstractMethodError when field %% is null.\n" +
            "Suppress Warning.NULL_FIELDS to disable this check.",
            method,
            field.getName()
        );
        fail(f, e);
    }

    private void classCastExceptionThrown(Field field, ClassCastException e) {
        Formatter f = Formatter.of(
            "Generics: ClassCastException was thrown. Consider using withGenericPrefabValues for %%.",
            field.getType().getSimpleName()
        );
        fail(f, e);
    }

    private void exceptionThrown(String method, Field field, Exception e) {
        Formatter f = Formatter.of(
            "%% throws %% when field %% is null.",
            method,
            e.getClass().getSimpleName(),
            field.getName()
        );
        fail(f, e);
    }
}
