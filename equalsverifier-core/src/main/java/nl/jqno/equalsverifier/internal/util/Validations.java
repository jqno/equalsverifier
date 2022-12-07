package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.util.ListBuilders.listContainsDuplicates;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;

public final class Validations {

    private Validations() {}

    public static void validateClassCanBeVerified(Class<?> type) {
        validate(
            ArrayList.class.isAssignableFrom(type),
            "EqualsVerfifier cannot verify subclasses of java.util.ArrayList: its invariants are too complex for EqualsVerifier to maintain."
        );
    }

    public static void validateFieldNamesExist(
        Class<?> type,
        List<String> givenFields,
        Set<String> actualFields
    ) {
        givenFields.forEach(f -> validateFieldNameExists(type, f, actualFields));
    }

    private static void validateFieldNameExists(
        Class<?> type,
        String field,
        Set<String> actualFields
    ) {
        validate(
            !actualFields.contains(field),
            "class " + type.getSimpleName() + " does not contain field " + field + "."
        );
    }

    public static void validateWarnings(Set<Warning> warnings) {
        validate(
            warnings.contains(Warning.SURROGATE_KEY) &&
            warnings.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY),
            "you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when Warning.SURROGATE_KEY is also suppressed."
        );
        validate(
            warnings.contains(Warning.SURROGATE_KEY) &&
            warnings.contains(Warning.SURROGATE_OR_BUSINESS_KEY),
            "you can't suppress Warning.SURROGATE_KEY when Warning.SURROGATE_OR_BUSINESS_KEY is also suppressed."
        );
    }

    public static void validateFields(Set<String> includedFields, Set<String> excludedFields) {
        validate(
            !includedFields.isEmpty() && !excludedFields.isEmpty(),
            "you can call either withOnlyTheseFields or withIgnoredFields, but not both."
        );
    }

    public static void validateNonnullFields(Set<String> nonnullFields, Set<Warning> warnings) {
        validate(
            !nonnullFields.isEmpty() && warnings.contains(Warning.NULL_FIELDS),
            "you can call either withNonnullFields or suppress Warning.NULL_FIELDS, but not both."
        );
    }

    public static <T> void validateUnequalExamples(List<T> unequalExamples, List<T> equalExamples) {
        validate(listContainsDuplicates(unequalExamples), "two objects are equal to each other.");

        unequalExamples.forEach(u -> validateExampleIsUnequal(u, equalExamples));
    }

    private static <T> void validateExampleIsUnequal(T example, List<T> equalExamples) {
        validate(
            equalExamples.contains(example),
            "an equal example also appears as unequal example."
        );
    }

    public static <T> void validateRedAndBluePrefabValues(Class<T> type, T red, T blue) {
        validateNotNull(type, "type is null.");
        validateNotNull(red, "red value is null.");
        validateNotNull(blue, "blue value is null.");
        validate(red.equals(blue), "both values are equal.");
    }

    public static <T> void validateGenericPrefabValues(
        Class<T> type,
        PrefabValueFactory<T> factory,
        int arity
    ) {
        validateNotNull(type, "type is null.");

        int n = type.getTypeParameters().length;
        String message =
            "number of generic type parameters doesn't match:\n  " +
            type.getName() +
            " has " +
            n +
            "\n  Factory has " +
            arity;
        validate(n != arity, message);
    }

    public static void validateWarningsAndFields(
        Set<Warning> warnings,
        Set<String> includedFields,
        Set<String> excludedFields
    ) {
        boolean hasSurrogateKey = warnings.contains(Warning.SURROGATE_KEY);
        boolean usesWithOnlyTheseFields = !includedFields.isEmpty();
        boolean usesWithIgnoredFields = !excludedFields.isEmpty();

        validate(
            hasSurrogateKey && usesWithOnlyTheseFields,
            "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed.\n" +
            "You can remove withOnlyTheseFields."
        );
        validate(
            hasSurrogateKey && usesWithIgnoredFields,
            "you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed.\n" +
            "You can remove withIgnoredFields."
        );
    }

    public static void validateGivenAnnotations(Class<?>... givenAnnotations) {
        Arrays.stream(givenAnnotations).forEach(a -> validateIsAnnotation(a));
    }

    private static void validateIsAnnotation(Class<?> type) {
        validate(
            !type.isAnnotation(),
            "class " + type.getCanonicalName() + " is not an annotation."
        );
    }

    public static void validateProcessedAnnotations(
        Class<?> type,
        AnnotationCache cache,
        Set<Warning> warnings,
        Set<String> includedFields,
        Set<String> excludedFields
    ) {
        validateClassAnnotations(type, cache, warnings, includedFields, excludedFields);
        validateFieldAnnotations(type, cache, includedFields);
    }

    // CHECKSTYLE OFF: VariableDeclarationUsageDistance
    private static void validateClassAnnotations(
        Class<?> type,
        AnnotationCache cache,
        Set<Warning> warnings,
        Set<String> includedFields,
        Set<String> excludedFields
    ) {
        boolean usesWithOnlyTheseFields = !includedFields.isEmpty();
        boolean usesWithIgnoredFields = !excludedFields.isEmpty();
        boolean hasNaturalId = cache.hasClassAnnotation(type, SupportedAnnotations.NATURALID);
        boolean hasSurrogateKey = warnings.contains(Warning.SURROGATE_KEY);
        boolean hasVersionedEntity = warnings.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY);

        validate(
            hasNaturalId && usesWithOnlyTheseFields,
            "you can't use withOnlyTheseFields when fields are marked with @NaturalId."
        );
        validate(
            hasNaturalId && usesWithIgnoredFields,
            "you can't use withIgnoredFields when fields are marked with @NaturalId."
        );
        validate(
            hasNaturalId && hasSurrogateKey,
            "you can't suppress Warning.SURROGATE_KEY when fields are marked @NaturalId."
        );
        validate(
            hasNaturalId && hasVersionedEntity,
            "you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when fields are marked with @NaturalId."
        );
    }

    // CHECKSTYLE ON: VariableDeclarationUsageDistance

    private static void validateFieldAnnotations(
        Class<?> type,
        AnnotationCache cache,
        Set<String> includedFields
    ) {
        FieldIterable
            .of(type)
            .forEach(f -> validateFieldAnnotation(type, f, cache, includedFields));
    }

    private static void validateFieldAnnotation(
        Class<?> type,
        Field f,
        AnnotationCache cache,
        Set<String> includedFields
    ) {
        validate(
            includedFields.contains(f.getName()) &&
            cache.hasFieldAnnotation(type, f.getName(), SupportedAnnotations.ID),
            "you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.\n" +
            "Suppress Warning.SURROGATE_KEY and remove withOnlyTheseFields " +
            "if you want to use only the @Id or @EmbeddedId fields in equals."
        );
    }

    public static void validatePackageContainsClasses(String packageName, List<Class<?>> types) {
        validate(
            types.size() == 0,
            "package " + packageName + " doesn't contain any (non-Test) types."
        );
    }

    public static void validateTypesAreKnown(List<Class<?>> types, List<Class<?>> knownTypes) {
        List<Class<?>> unknownTypes = types
            .stream()
            .filter(t -> !knownTypes.contains(t))
            .collect(Collectors.toList());
        String message =
            "Unknown class(es) found: " +
            unknownTypes.stream().map(t -> t.getCanonicalName()).collect(Collectors.joining(", "));
        validate(!unknownTypes.isEmpty(), message);
    }

    public static void validateNotNull(Object object, String errormessage) {
        if (object == null) {
            throw new NullPointerException("Precondition: " + errormessage);
        }
    }

    private static void validate(boolean condition, String errorMessage) {
        if (condition) {
            throw new IllegalStateException("Precondition: " + errorMessage);
        }
    }
}
