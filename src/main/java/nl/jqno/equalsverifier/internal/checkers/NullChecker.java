package nl.jqno.equalsverifier.internal.checkers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.FieldInspector;
import nl.jqno.equalsverifier.internal.util.Formatter;

import java.lang.reflect.Field;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

public class NullChecker<T> implements Checker {
    private final Configuration<T> config;
    private final ClassAccessor<T> classAccessor;

    public NullChecker(Configuration<T> config) {
        this.config = config;
        this.classAccessor = config.createClassAccessor();
    }

    @Override
    public void check() {
        if (config.getWarningsToSuppress().contains(Warning.NULL_FIELDS)) {
            return;
        }

        FieldInspector<T> inspector = new FieldInspector<>(classAccessor, config.getTypeTag());
        inspector.check(new NullPointerExceptionFieldCheck());
    }

    private class NullPointerExceptionFieldCheck implements FieldInspector.FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Field field = referenceAccessor.getField();
            if (config.getNonnullFields().contains(field.getName())) {
                return;
            }
            if (field.getType().isPrimitive()) {
                return;
            }
            if (NonnullAnnotationVerifier.fieldIsNonnull(classAccessor, field)) {
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
            handle("equals", field, new Runnable() {
                @Override
                @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED", justification = "We only want to see if it throws an exception.")
                public void run() {
                    reference.equals(changed);
                }
            });

            handle("equals", field, new Runnable() {
                @Override
                @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED", justification = "We only want to see if it throws an exception.")
                public void run() {
                    changed.equals(reference);
                }
            });

            handle("hashCode", field, new Runnable() {
                @Override
                public void run() {
                    config.getCachedHashCodeInitializer().getInitializedHashCode(changed);
                }
            });
        }

        private void handle(String testedMethodName, Field field, Runnable r) {
            try {
                r.run();
            }
            catch (NullPointerException e) {
                npeThrown(testedMethodName, field, e);
            }
            catch (Exception e) {
                exceptionThrown(testedMethodName, field, e);
            }
        }

        private void npeThrown(String method, Field field, Exception e) {
            fail(Formatter.of("Non-nullity: %% throws NullPointerException on field %%.", method, field.getName()), e);
        }

        private void exceptionThrown(String method, Field field, Exception e) {
            fail(Formatter.of("%% throws %% when field %% is null.", method, e.getClass().getSimpleName(), field.getName()), e);
        }
    }
}
