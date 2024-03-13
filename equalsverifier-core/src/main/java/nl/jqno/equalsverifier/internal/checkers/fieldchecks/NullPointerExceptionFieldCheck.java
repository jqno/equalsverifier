package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.instantiation.FieldProbe;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldModifier;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

@SuppressFBWarnings(
    value = "RV_RETURN_VALUE_IGNORED",
    justification = "We only want to see if it throws an exception."
)
public class NullPointerExceptionFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final Configuration<T> config;

    public NullPointerExceptionFieldCheck(
        SubjectCreator<T> subjectCreator,
        Configuration<T> config
    ) {
        this.subjectCreator = subjectCreator;
        this.config = config;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        if (config.getNonnullFields().contains(fieldProbe.getName())) {
            return;
        }
        if (fieldProbe.fieldIsPrimitive()) {
            return;
        }
        if (
            NonnullAnnotationVerifier.fieldIsNonnull(
                fieldProbe.getField(),
                config.getAnnotationCache()
            )
        ) {
            return;
        }

        if (FieldAccessor.of(fieldProbe.getField()).fieldIsStatic()) {
            T reference = subjectCreator.plain();
            FieldModifier fieldModifier = FieldModifier.of(fieldProbe.getField(), reference);
            Object saved = ObjectAccessor.of(reference).getField(fieldProbe.getField());

            fieldModifier.defaultStaticField();
            performTests(fieldProbe.getField(), subjectCreator.plain(), subjectCreator.plain());
            fieldModifier.set(saved);
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
