package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;

import java.lang.reflect.Field;
import java.util.Set;

public final class Validations {
    private Validations() {}

    public static void validateWarnings(Set<Warning> warnings) {
        validate(warnings.contains(Warning.SURROGATE_KEY) && warnings.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY),
            "you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when Warning.SURROGATE_KEY is also suppressed.");
    }

    public static void validateWarningsAndFields(Set<Warning> warnings, Set<String> includedFields, Set<String> excludedFields) {
        boolean hasSurrogateKey = warnings.contains(Warning.SURROGATE_KEY);
        boolean usesWithOnlyTheseFields = !includedFields.isEmpty();
        boolean usesWithIgnoredFields = !excludedFields.isEmpty();

        validate(hasSurrogateKey && usesWithOnlyTheseFields, "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed.");
        validate(hasSurrogateKey && usesWithIgnoredFields, "you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed.");
    }

    public static void validateAnnotations(
            Class<?> type, AnnotationCache cache, Set<Warning> warnings, Set<String> includedFields, Set<String> excludedFields) {

        validateClassAnnotations(type, cache, warnings, includedFields, excludedFields);
        validateFieldAnnotations(type, cache, includedFields);
    }

    // CHECKSTYLE: ignore VariableDeclarationUsageDistance for 8 lines.
    private static void validateClassAnnotations(
            Class<?> type, AnnotationCache cache, Set<Warning> warnings, Set<String> includedFields, Set<String> excludedFields) {

        boolean usesWithOnlyTheseFields = !includedFields.isEmpty();
        boolean usesWithIgnoredFields = !excludedFields.isEmpty();
        boolean hasNaturalId = cache.hasClassAnnotation(type, SupportedAnnotations.NATURALID);
        boolean hasSurrogateKey = warnings.contains(Warning.SURROGATE_KEY);
        boolean hasVersionedEntity = warnings.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY);

        validate(hasNaturalId && usesWithOnlyTheseFields, "you can't use withOnlyTheseFields when fields are marked with @NaturalId.");
        validate(hasNaturalId && usesWithIgnoredFields, "you can't use withIgnoredFields when fields are marked with @NaturalId.");
        validate(hasNaturalId && hasSurrogateKey,
            "you can't suppress Warning.SURROGATE_KEY when fields are marked @NaturalId.");
        validate(hasNaturalId && hasVersionedEntity,
            "you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when fields are marked with @NaturalId.");
    }

    private static void validateFieldAnnotations(Class<?> type, AnnotationCache cache, Set<String> includedFields) {
        for (Field f : FieldIterable.of(type)) {
            validate(includedFields.contains(f.getName()) && cache.hasFieldAnnotation(type, f.getName(), SupportedAnnotations.ID),
                "you can't use withOnlyTheseFields on a field marked @Id.\n" +
                "Suppress Warning.SURROGATE_KEY if you want to use only the @Id fields in equals.");
        }
    }

    private static void validate(boolean condition, String errorMessage) {
        if (condition) {
            throw new IllegalStateException("Precondition: " + errorMessage);
        }
    }
}
