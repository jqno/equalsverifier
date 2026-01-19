package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertFalse;
import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.*;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

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
        this.type = config.type();
        this.warningsToSuppress = config.warningsToSuppress();
        this.ignoredFields = config.ignoredFields();
        this.cachedHashCodeInitializer = config.cachedHashCodeInitializer();
        this.annotationCache = config.annotationCache();
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
            false);
        if (fieldProbe.canBeDefault(config)) {
            checkValues(
                subjectCreator.withAllFieldsDefaulted(),
                subjectCreator.withAllFieldsDefaulted(),
                subjectCreator.withAllFieldsDefaultedExcept(fieldProbe.getField()),
                fieldProbe,
                true);
        }
    }

    private void checkValues(T reference, T copy, T changed, FieldProbe probe, boolean testWithNull) {
        String fieldDisplayName = probe.getDisplayName();

        boolean equalToItself = reference.equals(copy);
        boolean equalsChanged = !reference.equals(changed);
        boolean hashCodeChanged = cachedHashCodeInitializer
                .getInitializedHashCode(reference) != cachedHashCodeInitializer.getInitializedHashCode(changed);

        assertEqualsAndHashCodeRelyOnSameFields(
            equalsChanged,
            hashCodeChanged,
            reference,
            changed,
            fieldDisplayName,
            testWithNull);
        assertFieldShouldBeIgnored(equalToItself, equalsChanged, reference, probe, testWithNull);
    }

    private void assertEqualsAndHashCodeRelyOnSameFields(
            boolean equalsChanged,
            boolean hashCodeChanged,
            T reference,
            T changed,
            String fieldDisplayName,
            boolean testWithNull) {
        if (equalsChanged != hashCodeChanged) {
            boolean skipEqualsHasMoreThanHashCodeTest =
                    warningsToSuppress.contains(Warning.STRICT_HASHCODE) || testWithNull;
            if (!skipEqualsHasMoreThanHashCodeTest) {
                Formatter formatter = Formatter
                        .of(
                            """
                            Significant fields: equals relies on %%, but hashCode does not.
                              %% has hashCode %%
                              %% has hashCode %%""",
                            fieldDisplayName,
                            reference,
                            reference.hashCode(),
                            changed,
                            changed.hashCode());
                assertFalse(formatter, equalsChanged);
            }
            Formatter formatter = Formatter.of("""
                                               Significant fields: hashCode relies on %%, but equals does not.
                                               These objects are equal, but probably shouldn't be:
                                                 %%
                                               and
                                                 %%""", fieldDisplayName, reference, changed);
            assertFalse(formatter, hashCodeChanged);
        }
    }

    private void assertFieldShouldBeIgnored(
            boolean equalToItself,
            boolean equalsChanged,
            T object,
            FieldProbe fieldProbe,
            boolean testWithNull) {
        if (!shouldAllFieldsBeUsed(fieldProbe) || !isFieldEligible(fieldProbe)) {
            return;
        }

        String fieldName = fieldProbe.getName();
        String fieldDisplayName = fieldProbe.getDisplayName();
        boolean fieldShouldBeIgnored = ignoredFields.contains(fieldName);
        boolean thisFieldIsMarkedAsId = annotationCache.hasFieldAnnotation(type, fieldName, SupportedAnnotations.ID);
        boolean anotherFieldIsMarkedAsId =
                !thisFieldIsMarkedAsId && annotationCache.hasClassAnnotation(type, SupportedAnnotations.ID);

        if (!fieldIsEmptyAndItsOk(thisFieldIsMarkedAsId, fieldProbe, object)) {
            if (!fieldShouldBeIgnored) {
                assertTrue(
                    Formatter.of("Significant fields: equals does not use %%.", fieldDisplayName),
                    equalToItself);
            }
            assertFieldShouldHaveBeenUsed(
                fieldDisplayName,
                equalsChanged,
                fieldShouldBeIgnored,
                thisFieldIsMarkedAsId,
                anotherFieldIsMarkedAsId);
        }
        assertFieldShouldNotBeUsed(
            fieldDisplayName,
            equalsChanged,
            fieldShouldBeIgnored,
            thisFieldIsMarkedAsId,
            anotherFieldIsMarkedAsId,
            testWithNull);
    }

    private boolean shouldAllFieldsBeUsed(FieldProbe fieldProbe) {
        return !warningsToSuppress.contains(Warning.ALL_FIELDS_SHOULD_BE_USED)
                && !(warningsToSuppress.contains(Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED) && !fieldProbe.isFinal());
    }

    private boolean isFieldEligible(FieldProbe fieldProbe) {
        return !fieldProbe.isStatic()
                && !fieldProbe.isTransient()
                && !fieldProbe.isEmptyOrSingleValueEnum()
                && !annotationCache
                        .hasFieldAnnotation(type, fieldProbe.getField().getName(), SupportedAnnotations.TRANSIENT);
    }

    private boolean fieldIsEmptyAndItsOk(boolean thisFieldIsMarkedAsId, FieldProbe fieldProbe, T object) {
        Object value = fieldProbe.getValue(object);
        Class<?> fieldType = fieldProbe.getType();
        Object zero = PrimitiveMappers.DEFAULT_WRAPPED_VALUE_MAPPER.get(fieldType);
        boolean fieldIsEmpty = value == null || value.equals(zero);

        return thisFieldIsMarkedAsId
                && warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                && fieldIsEmpty;
    }

    private void assertFieldShouldHaveBeenUsed(
            String fieldDisplayName,
            boolean equalsChanged,
            boolean fieldShouldBeIgnored,
            boolean thisFieldIsMarkedAsId,
            boolean anotherFieldIsMarkedAsId) {
        final String message;
        if (thisFieldIsMarkedAsId) {
            message = "Significant fields: %% is marked @Id or @EmbeddedId and Warning.SURROGATE_KEY"
                    + " is suppressed, but equals does not use it.";
        }
        else if (anotherFieldIsMarkedAsId) {
            message = """
                      Significant fields: equals does not use %%, or it is stateless.
                      Suppress Warning.SURROGATE_KEY if you want to use only the @Id or @EmbeddedId field(s).""";
        }
        else {
            message = "Significant fields: equals does not use %%, or it is stateless.";
        }

        assertTrue(Formatter.of(message, fieldDisplayName), fieldShouldBeIgnored || equalsChanged);
    }

    private void assertFieldShouldNotBeUsed(
            String fieldDisplayName,
            boolean equalsChanged,
            boolean fieldShouldBeIgnored,
            boolean thisFieldIsMarkedAsId,
            boolean anotherFieldIsMarkedAsId,
            boolean testWithNull) {
        final String message;
        if (thisFieldIsMarkedAsId) {
            message = """
                      Significant fields: %% is marked @Id or @EmbeddedId so equals should not use it, but it does.
                      Suppress Warning.SURROGATE_KEY if you want to use only the @Id or @EmbeddedId field(s).""";
        }
        else if (anotherFieldIsMarkedAsId) {
            message = """
                      Significant fields: equals should not use %% because Warning.SURROGATE_KEY is suppressed\
                       and it is not marked as @Id or @EmbeddedId, but it does.""";
        }
        else {
            message = "Significant fields: equals should not use %%, but it does.";
        }

        assertTrue(Formatter.of(message, fieldDisplayName), !fieldShouldBeIgnored || !equalsChanged || testWithNull);
    }
}
