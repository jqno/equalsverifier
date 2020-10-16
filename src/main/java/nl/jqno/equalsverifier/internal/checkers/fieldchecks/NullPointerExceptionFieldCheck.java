package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldModifier;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

@SuppressFBWarnings(
        value = "RV_RETURN_VALUE_IGNORED",
        justification = "We only want to see if it throws an exception.")
public class NullPointerExceptionFieldCheck<T> implements FieldCheck<T> {
    private Configuration<T> config;

    public NullPointerExceptionFieldCheck(Configuration<T> config) {
        this.config = config;
    }

    @Override
    public void execute(
            ObjectAccessor<T> referenceAccessor,
            ObjectAccessor<T> copyAccessor,
            FieldAccessor fieldAccessor) {
        if (config.getNonnullFields().contains(fieldAccessor.getFieldName())) {
            return;
        }
        if (fieldAccessor.getFieldType().isPrimitive()) {
            return;
        }
        Field field = fieldAccessor.getField();
        if (NonnullAnnotationVerifier.fieldIsNonnull(field, config.getAnnotationCache())) {
            return;
        }

        if (fieldAccessor.fieldIsStatic()) {
            FieldModifier fieldModifier = FieldModifier.of(field, referenceAccessor.get());
            Object saved = referenceAccessor.getField(field);

            fieldModifier.defaultStaticField();
            performTests(field, referenceAccessor.get(), copyAccessor.get());
            fieldModifier.set(saved);
        } else {
            ObjectAccessor<?> changed = copyAccessor.withDefaultedField(field);
            performTests(field, referenceAccessor.get(), changed.get());
            referenceAccessor.withDefaultedField(field);
        }
    }

    private void performTests(Field field, final Object reference, final Object changed) {
        handle("equals", field, () -> reference.equals(changed));
        handle("equals", field, () -> changed.equals(reference));
        handle(
                "hashCode",
                field,
                () -> config.getCachedHashCodeInitializer().getInitializedHashCode(changed));
    }

    private void handle(String testedMethodName, Field field, Runnable r) {
        try {
            r.run();
        } catch (NullPointerException e) {
            npeThrown(testedMethodName, field, e);
        } catch (AbstractMethodError e) {
            abstractMethodErrorThrown(testedMethodName, field, e);
        } catch (ClassCastException e) {
            classCastExceptionThrown(field, e);
        } catch (Exception e) {
            exceptionThrown(testedMethodName, field, e);
        }
    }

    private void npeThrown(String method, Field field, NullPointerException e) {
        Formatter f =
                Formatter.of(
                        "Non-nullity: %% throws NullPointerException on field %%.",
                        method, field.getName());
        fail(f, e);
    }

    private void abstractMethodErrorThrown(String method, Field field, AbstractMethodError e) {
        Formatter f =
                Formatter.of(
                        "Abstract delegation: %% throws AbstractMethodError when field %% is null.\n"
                                + "Suppress Warning.NULL_FIELDS to disable this check.",
                        method, field.getName());
        fail(f, e);
    }

    private void classCastExceptionThrown(Field field, ClassCastException e) {
        Formatter f =
                Formatter.of(
                        "Generics: ClassCastException was thrown. Consider using withGenericPrefabValues for %%.",
                        field.getType().getSimpleName());
        fail(f, e);
    }

    private void exceptionThrown(String method, Field field, Exception e) {
        Formatter f =
                Formatter.of(
                        "%% throws %% when field %% is null.",
                        method, e.getClass().getSimpleName(), field.getName());
        fail(f, e);
    }
}
