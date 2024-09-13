package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertFalse;
import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.instantiation.FieldProbe;
import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.*;

public class SignificantFieldCheck<T> implements FieldCheck<T> {

    private final SubjectCreator<T> subjectCreator;
    private final Configuration<T> config;
    private final Class<T> type;
    private final EnumSet<Warning> warningsToSuppress;
    private final Set<String> ignoredFields;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final AnnotationCache annotationCache;
    private final Predicate<FieldProbe> isCachedHashCodeField;

    public SignificantFieldCheck(Context<T> context, Predicate<FieldProbe> isCachedHashCodeField) {
        this.subjectCreator = context.getSubjectCreator();
        this.config = context.getConfiguration();
        this.type = config.getType();
        this.warningsToSuppress = config.getWarningsToSuppress();
        this.ignoredFields = config.getIgnoredFields();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
        this.annotationCache = config.getAnnotationCache();
        this.isCachedHashCodeField = isCachedHashCodeField;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        if (isCachedHashCodeField.test(fieldProbe)) {
            return;
        }

        checkValues(
            subjectCreator.plain(),
            subjectCreator.plain(),
            subjectCreator.withFieldChanged(fieldProbe.getField()),
            fieldProbe,
            false
        );
        if (fieldProbe.canBeDefault(config)) {
            checkValues(
                subjectCreator.withAllFieldsDefaulted(),
                subjectCreator.withAllFieldsDefaulted(),
                subjectCreator.withAllFieldsDefaultedExcept(fieldProbe.getField()),
                fieldProbe,
                true
            );
        }
    }

    private void checkValues(
        T reference,
        T copy,
        T changed,
        FieldProbe probe,
        boolean testWithNull
    ) {
        String fieldName = probe.getField().getName();

        boolean equalToItself = reference.equals(copy);
        boolean equalsChanged = !reference.equals(changed);
        boolean hashCodeChanged =
            cachedHashCodeInitializer.getInitializedHashCode(reference) !=
            cachedHashCodeInitializer.getInitializedHashCode(changed);

        assertEqualsAndHashCodeRelyOnSameFields(
            equalsChanged,
            hashCodeChanged,
            reference,
            changed,
            fieldName,
            testWithNull
        );
        assertFieldShouldBeIgnored(
            equalToItself,
            equalsChanged,
            reference,
            probe,
            fieldName,
            testWithNull
        );
    }

    private void assertEqualsAndHashCodeRelyOnSameFields(
        boolean equalsChanged,
        boolean hashCodeChanged,
        T reference,
        T changed,
        String fieldName,
        boolean testWithNull
    ) {
        if (equalsChanged != hashCodeChanged) {
            boolean skipEqualsHasMoreThanHashCodeTest =
                warningsToSuppress.contains(Warning.STRICT_HASHCODE) || testWithNull;
            if (!skipEqualsHasMoreThanHashCodeTest) {
                Formatter formatter = Formatter.of(
                    "Significant fields: equals relies on %%, but hashCode does not." +
                    "\n  %% has hashCode %%\n  %% has hashCode %%",
                    fieldName,
                    reference,
                    reference.hashCode(),
                    changed,
                    changed.hashCode()
                );
                assertFalse(formatter, equalsChanged);
            }
            Formatter formatter = Formatter.of(
                "Significant fields: hashCode relies on %%, but equals does not.\n" +
                "These objects are equal, but probably shouldn't be:\n  %%\nand\n  %%",
                fieldName,
                reference,
                changed
            );
            assertFalse(formatter, hashCodeChanged);
        }
    }

    private void assertFieldShouldBeIgnored(
        boolean equalToItself,
        boolean equalsChanged,
        T object,
        FieldProbe fieldProbe,
        String fieldName,
        boolean testWithNull
    ) {
        if (!shouldAllFieldsBeUsed(fieldProbe) || !isFieldEligible(fieldProbe)) {
            return;
        }

        boolean fieldShouldBeIgnored = ignoredFields.contains(fieldName);
        boolean thisFieldIsMarkedAsId = annotationCache.hasFieldAnnotation(
            type,
            fieldName,
            SupportedAnnotations.ID
        );
        boolean anotherFieldIsMarkedAsId =
            !thisFieldIsMarkedAsId &&
            annotationCache.hasClassAnnotation(type, SupportedAnnotations.ID);

        if (!fieldIsEmptyAndItsOk(thisFieldIsMarkedAsId, fieldProbe, object)) {
            if (!fieldShouldBeIgnored) {
                assertTrue(
                    Formatter.of("Significant fields: equals does not use %%.", fieldName),
                    equalToItself
                );
            }
            assertFieldShouldHaveBeenUsed(
                fieldName,
                equalsChanged,
                fieldShouldBeIgnored,
                thisFieldIsMarkedAsId,
                anotherFieldIsMarkedAsId
            );
        }
        assertFieldShouldNotBeUsed(
            fieldName,
            equalsChanged,
            fieldShouldBeIgnored,
            thisFieldIsMarkedAsId,
            anotherFieldIsMarkedAsId,
            testWithNull
        );
    }

    private boolean shouldAllFieldsBeUsed(FieldProbe fieldProbe) {
        return (
            !warningsToSuppress.contains(Warning.ALL_FIELDS_SHOULD_BE_USED) &&
            !(warningsToSuppress.contains(Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED) &&
                !fieldProbe.isFinal())
        );
    }

    private boolean isFieldEligible(FieldProbe fieldProbe) {
        return (
            !fieldProbe.isStatic() &&
            !fieldProbe.isTransient() &&
            !fieldProbe.isEmptyOrSingleValueEnum() &&
            !annotationCache.hasFieldAnnotation(
                type,
                fieldProbe.getField().getName(),
                SupportedAnnotations.TRANSIENT
            )
        );
    }

    private boolean fieldIsEmptyAndItsOk(
        boolean thisFieldIsMarkedAsId,
        FieldProbe fieldProbe,
        T object
    ) {
        Object value = fieldProbe.getValue(object);
        Class<?> fieldType = fieldProbe.getType();
        Object zero = PrimitiveMappers.DEFAULT_WRAPPED_VALUE_MAPPER.get(fieldType);
        boolean fieldIsEmpty = value == null || value.equals(zero);

        return (
            thisFieldIsMarkedAsId &&
            warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY) &&
            fieldIsEmpty
        );
    }

    private void assertFieldShouldHaveBeenUsed(
        String fieldName,
        boolean equalsChanged,
        boolean fieldShouldBeIgnored,
        boolean thisFieldIsMarkedAsId,
        boolean anotherFieldIsMarkedAsId
    ) {
        final String message;
        if (thisFieldIsMarkedAsId) {
            message =
                "Significant fields: %% is marked @Id or @EmbeddedId and Warning.SURROGATE_KEY" +
                " is suppressed, but equals does not use it.";
        } else if (anotherFieldIsMarkedAsId) {
            message =
                "Significant fields: equals does not use %%, or it is stateless.\n" +
                "Suppress Warning.SURROGATE_KEY if you want to use only the @Id or" +
                " @EmbeddedId field(s).";
        } else {
            message = "Significant fields: equals does not use %%, or it is stateless.";
        }

        assertTrue(Formatter.of(message, fieldName), fieldShouldBeIgnored || equalsChanged);
    }

    private void assertFieldShouldNotBeUsed(
        String fieldName,
        boolean equalsChanged,
        boolean fieldShouldBeIgnored,
        boolean thisFieldIsMarkedAsId,
        boolean anotherFieldIsMarkedAsId,
        boolean testWithNull
    ) {
        final String message;
        if (thisFieldIsMarkedAsId) {
            message =
                "Significant fields: %% is marked @Id or @EmbeddedId so equals should not use" +
                " it, but it does.\n" +
                "Suppress Warning.SURROGATE_KEY if you want to use only the @Id or" +
                " @EmbeddedId field(s).";
        } else if (anotherFieldIsMarkedAsId) {
            message =
                "Significant fields: equals should not use %% because Warning.SURROGATE_KEY is" +
                " suppressed and it is not marked as @Id or @EmbeddedId, but it does.";
        } else {
            message = "Significant fields: equals should not use %%, but it does.";
        }

        assertTrue(
            Formatter.of(message, fieldName),
            !fieldShouldBeIgnored || !equalsChanged || testWithNull
        );
    }
}
