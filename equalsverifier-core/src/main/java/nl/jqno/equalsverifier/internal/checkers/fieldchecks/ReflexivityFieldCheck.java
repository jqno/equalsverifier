package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertEquals;
import static nl.jqno.equalsverifier.internal.util.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Context;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class ReflexivityFieldCheck<T> implements FieldCheck<T> {

    private final TypeTag typeTag;
    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;
    private final EnumSet<Warning> warningsToSuppress;
    private final Set<String> nonnullFields;
    private final Set<String> prefabbedFields;
    private final AnnotationCache annotationCache;

    public ReflexivityFieldCheck(Context<T> context) {
        this.subjectCreator = context.getSubjectCreator();
        this.valueProvider = context.getValueProvider();

        Configuration<T> config = context.getConfiguration();
        this.typeTag = config.typeTag();
        this.warningsToSuppress = config.warningsToSuppress();
        this.nonnullFields = config.nonnullFields();
        this.prefabbedFields = config.prefabbedFields();
        this.annotationCache = config.annotationCache();
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        if (warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)) {
            return;
        }

        checkReferenceReflexivity();
        checkValueReflexivity(fieldProbe);
        checkNullReflexivity(fieldProbe);
    }

    private void checkReferenceReflexivity() {
        T left = subjectCreator.plain();
        T right = subjectCreator.plain();
        checkReflexivityFor(left, right);
    }

    private void checkValueReflexivity(FieldProbe probe) {
        Class<?> fieldType = probe.getType();
        if (warningsToSuppress.contains(Warning.REFERENCE_EQUALITY)) {
            return;
        }
        if (fieldType.equals(Object.class) || fieldType.isInterface()) {
            return;
        }
        if (probe.isStatic()) {
            return;
        }
        ClassProbe<?> fieldTypeProbe = ClassProbe.of(fieldType);
        if (!fieldTypeProbe.declaresEquals()) {
            return;
        }
        if (fieldType.isSynthetic()) {
            // Sometimes not the fieldType, but its content, is synthetic.
            return;
        }

        Field field = probe.getField();
        String fieldName = field.getName();
        TypeTag tag = TypeTag.of(field, typeTag);
        Tuple<?> tuple = valueProvider.provideOrThrow(tag, Attributes.named(fieldName));

        Object left = subjectCreator.withFieldSetTo(field, tuple.red());
        Object right = subjectCreator.withFieldSetTo(field, tuple.redCopy());

        Formatter f = Formatter
                .of(
                    """
                    Reflexivity: == used instead of .equals() on field: %%
                    If this is intentional, consider suppressing Warning.%%""",
                    probe.getDisplayName(),
                    Warning.REFERENCE_EQUALITY.toString());
        assertEquals(f, left, right);
    }

    private void checkNullReflexivity(FieldProbe fieldProbe) {
        if (prefabbedFields.contains(fieldProbe.getName())) {
            return;
        }

        Field field = fieldProbe.getField();
        boolean nullWarningIsSuppressed = warningsToSuppress.contains(Warning.NULL_FIELDS);
        boolean fieldIsNonNull = fieldProbe.isAnnotatedNonnull(annotationCache);
        boolean fieldIsMentionedExplicitly = nonnullFields.contains(field.getName());
        if (nullWarningIsSuppressed || fieldIsNonNull || fieldIsMentionedExplicitly) {
            return;
        }

        T left = subjectCreator.withFieldDefaulted(field);
        T right = subjectCreator.withFieldDefaulted(field);
        checkReflexivityFor(left, right);
    }

    private void checkReflexivityFor(T left, T right) {
        if (warningsToSuppress.contains(Warning.IDENTICAL_COPY)) {
            Formatter f = Formatter
                    .of(
                        "Unnecessary suppression: %%. Two identical copies are equal.",
                        Warning.IDENTICAL_COPY.toString());
            assertFalse(f, left.equals(right));
        }
        else {
            boolean isEntity = annotationCache.hasClassAnnotation(typeTag.getType(), SupportedAnnotations.ENTITY);
            if (isEntity) {
                Formatter f = Formatter
                        .of(
                            """
                            Reflexivity: entity does not equal an identical copy of itself:
                              %%
                            If this is intentional, consider suppressing Warning.%%.""",
                            left,
                            Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY.toString());
                assertEquals(f, left, right);
            }
            else {
                Formatter f = Formatter
                        .of(
                            """
                            Reflexivity: object does not equal an identical copy of itself:
                              %%
                            If this is intentional, consider suppressing Warning.%%.""",
                            left,
                            Warning.IDENTICAL_COPY.toString());
                assertEquals(f, left, right);
            }
        }
    }
}
