package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

import java.lang.reflect.Field;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

@SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED", justification = "We only want to see if it throws an exception.")
public class NullPointerExceptionFieldCheck<T> implements FieldCheck {
    private Configuration<T> config;

    public NullPointerExceptionFieldCheck(Configuration<T> config) {
        this.config = config;
    }

    @Override
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Field field = referenceAccessor.getField();
        if (config.getNonnullFields().contains(field.getName())) {
            return;
        }
        if (field.getType().isPrimitive()) {
            return;
        }
        if (NonnullAnnotationVerifier.fieldIsNonnull(field, config.getAnnotationCache())) {
            return;
        }

        if (referenceAccessor.fieldIsStatic()) {
            Object saved = referenceAccessor.get();
            referenceAccessor.defaultStaticField();
            performTests(field, referenceAccessor.getObject(), changedAccessor.getObject());
            referenceAccessor.set(saved);
        }
        else {
            changedAccessor.defaultField();
            performTests(field, referenceAccessor.getObject(), changedAccessor.getObject());
            referenceAccessor.defaultField();
        }
    }

    private void performTests(Field field, final Object reference, final Object changed) {
        handle("equals", field, () -> reference.equals(changed));
        handle("equals", field, () -> changed.equals(reference));
        handle("hashCode", field, () -> config.getCachedHashCodeInitializer().getInitializedHashCode(changed));
    }

    private void handle(String testedMethodName, Field field, Runnable r) {
        try {
            r.run();
        }
        catch (NullPointerException e) {
            npeThrown(testedMethodName, field, e);
        }
        catch (AbstractMethodError e) {
            abstractMethodErrorThrown(testedMethodName, field, e);
        }
        catch (Exception e) {
            exceptionThrown(testedMethodName, field, e);
        }
    }

    private void npeThrown(String method, Field field, NullPointerException e) {
        fail(Formatter.of("Non-nullity: %% throws NullPointerException on field %%.", method, field.getName()), e);
    }

    private void abstractMethodErrorThrown(String method, Field field, AbstractMethodError e) {
        fail(Formatter.of(
            "Abstract delegation: %% throws AbstractMethodError when field %% is null.\n" +
                "Suppress Warning.NULL_FIELDS to disable this check.",
            method, field.getName()), e);
    }

    private void exceptionThrown(String method, Field field, Exception e) {
        fail(Formatter.of("%% throws %% when field %% is null.", method, e.getClass().getSimpleName(), field.getName()), e);
    }
}
