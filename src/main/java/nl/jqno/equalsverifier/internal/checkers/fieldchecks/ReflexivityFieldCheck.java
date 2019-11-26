package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;
import static nl.jqno.equalsverifier.internal.util.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class ReflexivityFieldCheck<T> implements FieldCheck {
    private final TypeTag typeTag;
    private final PrefabValues prefabValues;
    private final EnumSet<Warning> warningsToSuppress;
    private final Set<String> nonnullFields;
    private final AnnotationCache annotationCache;

    public ReflexivityFieldCheck(Configuration<T> config) {
        this.typeTag = config.getTypeTag();
        this.prefabValues = config.getPrefabValues();
        this.warningsToSuppress = config.getWarningsToSuppress();
        this.nonnullFields = config.getNonnullFields();
        this.annotationCache = config.getAnnotationCache();
    }

    @Override
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        if (warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)) {
            return;
        }

        checkReferenceReflexivity(referenceAccessor, changedAccessor);
        checkValueReflexivity(referenceAccessor, changedAccessor);
        checkNullReflexivity(referenceAccessor, changedAccessor);
    }

    private void checkReferenceReflexivity(
            FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        checkReflexivityFor(referenceAccessor, changedAccessor);
    }

    private void checkValueReflexivity(
            FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Class<?> fieldType = changedAccessor.getFieldType();
        if (warningsToSuppress.contains(Warning.REFERENCE_EQUALITY)) {
            return;
        }
        if (fieldType.equals(Object.class) || fieldType.isInterface()) {
            return;
        }
        if (changedAccessor.fieldIsStatic()) {
            return;
        }
        ClassAccessor<?> fieldTypeAccessor = ClassAccessor.of(fieldType, prefabValues);
        if (!fieldTypeAccessor.declaresEquals()) {
            return;
        }
        Object value = changedAccessor.get();
        if (value.getClass().isSynthetic()) {
            // Sometimes not the fieldType, but its content, is synthetic.
            return;
        }

        TypeTag tag = TypeTag.of(referenceAccessor.getField(), typeTag);
        referenceAccessor.set(prefabValues.giveRed(tag));
        changedAccessor.set(prefabValues.giveRedCopy(tag));

        Formatter f =
                Formatter.of(
                        "Reflexivity: == used instead of .equals() on field: %%"
                                + "\nIf this is intentional, consider suppressing Warning.%%",
                        changedAccessor.getFieldName(), Warning.REFERENCE_EQUALITY.toString());
        Object left = referenceAccessor.getObject();
        Object right = changedAccessor.getObject();
        assertEquals(f, left, right);
    }

    private void checkNullReflexivity(
            FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Field field = referenceAccessor.getField();
        boolean fieldIsPrimitive = referenceAccessor.fieldIsPrimitive();
        boolean fieldIsNonNull = NonnullAnnotationVerifier.fieldIsNonnull(field, annotationCache);
        boolean ignoreNull =
                fieldIsNonNull
                        || warningsToSuppress.contains(Warning.NULL_FIELDS)
                        || nonnullFields.contains(field.getName());
        if (fieldIsPrimitive || !ignoreNull) {
            referenceAccessor.defaultField();
            changedAccessor.defaultField();
            checkReflexivityFor(referenceAccessor, changedAccessor);
        }
    }

    private void checkReflexivityFor(
            FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        Object left = referenceAccessor.getObject();
        Object right = changedAccessor.getObject();

        if (warningsToSuppress.contains(Warning.IDENTICAL_COPY)) {
            assertFalse(
                    Formatter.of(
                            "Unnecessary suppression: %%. Two identical copies are equal.",
                            Warning.IDENTICAL_COPY.toString()),
                    left.equals(right));
        } else {
            Formatter f =
                    Formatter.of(
                            "Reflexivity: object does not equal an identical copy of itself:\n  %%"
                                    + "\nIf this is intentional, consider suppressing Warning.%%",
                            left, Warning.IDENTICAL_COPY.toString());
            assertEquals(f, left, right);
        }
    }
}
