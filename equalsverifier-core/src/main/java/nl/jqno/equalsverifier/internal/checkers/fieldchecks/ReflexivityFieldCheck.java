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
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class ReflexivityFieldCheck<T> implements FieldCheck<T> {

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
    public void execute(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        if (warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)) {
            return;
        }

        checkReferenceReflexivity(referenceAccessor, copyAccessor);
        checkValueReflexivity(referenceAccessor, copyAccessor, fieldAccessor);
        checkNullReflexivity(referenceAccessor, copyAccessor, fieldAccessor);
    }

    private void checkReferenceReflexivity(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor
    ) {
        T left = referenceAccessor.get();
        T right = copyAccessor.get();
        checkReflexivityFor(left, right);
    }

    private void checkValueReflexivity(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        Field field = fieldAccessor.getField();
        Class<?> fieldType = field.getType();
        if (warningsToSuppress.contains(Warning.REFERENCE_EQUALITY)) {
            return;
        }
        if (fieldType.equals(Object.class) || fieldType.isInterface()) {
            return;
        }
        if (fieldAccessor.fieldIsStatic()) {
            return;
        }
        ClassAccessor<?> fieldTypeAccessor = ClassAccessor.of(fieldType, prefabValues);
        if (!fieldTypeAccessor.declaresEquals()) {
            return;
        }
        if (fieldType.isSynthetic()) {
            // Sometimes not the fieldType, but its content, is synthetic.
            return;
        }

        TypeTag tag = TypeTag.of(field, typeTag);
        Object left = referenceAccessor.withFieldSetTo(field, prefabValues.giveRed(tag)).get();
        Object right = copyAccessor.withFieldSetTo(field, prefabValues.giveRedCopy(tag)).get();

        Formatter f = Formatter.of(
            "Reflexivity: == used instead of .equals() on field: %%" +
            "\nIf this is intentional, consider suppressing Warning.%%",
            field.getName(),
            Warning.REFERENCE_EQUALITY.toString()
        );
        assertEquals(f, left, right);
    }

    private void checkNullReflexivity(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        if (fieldAccessor.fieldIsPrimitive() && warningsToSuppress.contains(Warning.ZERO_FIELDS)) {
            return;
        }

        Field field = fieldAccessor.getField();
        boolean nullWarningIsSuppressed = warningsToSuppress.contains(Warning.NULL_FIELDS);
        boolean fieldIsNonNull = NonnullAnnotationVerifier.fieldIsNonnull(field, annotationCache);
        boolean fieldIsMentionedExplicitly = nonnullFields.contains(field.getName());
        if (nullWarningIsSuppressed || fieldIsNonNull || fieldIsMentionedExplicitly) {
            return;
        }

        T left = referenceAccessor.withDefaultedField(field).get();
        T right = copyAccessor.withDefaultedField(field).get();
        checkReflexivityFor(left, right);
    }

    private void checkReflexivityFor(T left, T right) {
        if (warningsToSuppress.contains(Warning.IDENTICAL_COPY)) {
            Formatter f = Formatter.of(
                "Unnecessary suppression: %%. Two identical copies are equal.",
                Warning.IDENTICAL_COPY.toString()
            );
            assertFalse(f, left.equals(right));
        } else {
            Formatter f = Formatter.of(
                "Reflexivity: object does not equal an identical copy of itself:\n  %%" +
                "\nIf this is intentional, consider suppressing Warning.%%",
                left,
                Warning.IDENTICAL_COPY.toString()
            );
            assertEquals(f, left, right);
        }
    }
}
