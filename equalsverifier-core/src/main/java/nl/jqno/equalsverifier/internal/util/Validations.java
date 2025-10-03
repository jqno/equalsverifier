package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.util.ListBuilders.listContainsDuplicates;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;

public final class Validations {

    private Validations() {}

    public static void validateClassCanBeVerified(Class<?> type) {
        validate(
            ArrayList.class.isAssignableFrom(type),
            "EqualsVerfifier cannot verify subclasses of java.util.ArrayList: its invariants are too complex for EqualsVerifier to maintain.");
    }

    public static void validateFieldNamesExist(Class<?> type, List<String> givenFields, Set<String> actualFields) {
        givenFields.forEach(f -> validateFieldNameExists(type, f, actualFields));
    }

    public static void validateFieldNameExists(Class<?> type, String field, Set<String> actualFields) {
        String msg = "class " + type.getSimpleName() + " does not contain field " + field + ".";
        if (KotlinScreen.isKotlin(type) && !KotlinScreen.canProbe()) {
            msg += "\n           -> " + field + " may be a Kotlin delegate field. " + KotlinScreen.ERROR_MESSAGE;
        }
        validate(!actualFields.contains(field), msg);
    }

    public static void validateCanProbeKotlinLazyDelegate(Class<?> type, Field field) {
        validate(
            KotlinScreen.isKotlin(type) && KotlinScreen.isKotlinLazy(field) && !KotlinScreen.canProbe(),
            "Field " + field.getName() + " is a Kotlin lazy delegate field." + KotlinScreen.ERROR_MESSAGE);
    }

    public static void validateWarnings(Set<Warning> warnings) {
        validate(
            warnings.contains(Warning.SURROGATE_KEY) && warnings.contains(Warning.SURROGATE_OR_BUSINESS_KEY),
            "you can't suppress Warning.SURROGATE_KEY when Warning.SURROGATE_OR_BUSINESS_KEY is also suppressed.");
    }

    public static void validateFields(Set<String> includedFields, Set<String> excludedFields) {
        validate(
            !includedFields.isEmpty() && !excludedFields.isEmpty(),
            "you can call either withOnlyTheseFields or withIgnoredFields, but not both.");
    }

    public static void validateNonnullFields(Set<String> nonnullFields, Set<Warning> warnings) {
        validate(
            !nonnullFields.isEmpty() && warnings.contains(Warning.NULL_FIELDS),
            "you can call either withNonnullFields or suppress Warning.NULL_FIELDS, but not both.");
    }

    public static <T> void validateUnequalExamples(List<T> unequalExamples, List<T> equalExamples) {
        validate(listContainsDuplicates(unequalExamples), "two objects are equal to each other.");

        unequalExamples.forEach(u -> validateExampleIsUnequal(u, equalExamples));
    }

    private static <T> void validateExampleIsUnequal(T example, List<T> equalExamples) {
        validate(equalExamples.contains(example), "an equal example also appears as unequal example.");
    }

    public static <T> void validateRedAndBlueExamples(T red, T blue) {
        validateNotNull(red, "red example is null.");
        validateNotNull(blue, "blue example is null.");
        Class<?> redType = red.getClass();
        Class<?> blueType = blue.getClass();
        validate(!redType.equals(blueType), "examples are of different types.");
        validate(red.equals(blue), "both examples are equal.");
    }

    public static <T> void validateRedAndBluePrefabValues(Class<T> type, T red, T blue) {
        validateNotNull(type, "prefab value type is null.");
        validateNotNull(red, "red prefab value of type " + type.getSimpleName() + " is null.");
        validateNotNull(blue, "blue prefab value of type " + type.getSimpleName() + " is null.");
        validate(Objects.equals(red, blue), "both prefab values of type " + type.getSimpleName() + " are equal.");
    }

    public static <T> void validateRedAndBluePrefabValues(String fieldName, T red, T blue) {
        String fieldDescription =
                red == null ? "`" + fieldName + "`" : "`" + red.getClass().getSimpleName() + " " + fieldName + "`";
        validateNotNull(red, "red prefab value for field " + fieldDescription + " is null.");
        validateNotNull(blue, "blue prefab value for field " + fieldDescription + " is null.");
        validate(Objects.equals(red, blue), "both prefab values for field " + fieldDescription + " are equal.");
    }

    public static <T> Field validateFieldTypeMatches(Class<T> container, String fieldName, Class<?> fieldType) {
        Optional<Field> opt = ClassProbe.of(container).findField(fieldName);
        if (opt.isPresent()) {
            Field f = opt.get();
            boolean typeCompatible = f.getType().isAssignableFrom(fieldType);
            boolean wrappingCompatible = fieldType.equals(PrimitiveMappers.PRIMITIVE_OBJECT_MAPPER.get(f.getType()));
            validate(
                !typeCompatible && !wrappingCompatible,
                "Prefab values for field " + fieldName + " should be of type " + f.getType().getSimpleName()
                        + " but are " + fieldType.getSimpleName() + ".");
            return f;
        }
        else {
            validate(true, "Class " + container.getSimpleName() + " has no field named " + fieldName + ".");
            return null;
        }
    }

    public static <T> void validateGenericPrefabValues(Class<T> type, PrefabValueFactory<T> factory, int arity) {
        validateNotNull(type, "type is null.");

        int n = type.getTypeParameters().length;
        String message = "number of generic type parameters doesn't match:\n  " + type.getName() + " has " + n
                + "\n  Factory has " + arity;
        validate(n != arity, message);
    }

    public static void validateWarningsAndFields(
            Set<Warning> warnings,
            Set<String> includedFields,
            Set<String> excludedFields) {
        boolean hasSurrogateKey = warnings.contains(Warning.SURROGATE_KEY);
        boolean usesWithOnlyTheseFields = !includedFields.isEmpty();
        boolean usesWithIgnoredFields = !excludedFields.isEmpty();

        validate(
            hasSurrogateKey && usesWithOnlyTheseFields,
            """
            you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed.
            You can remove withOnlyTheseFields.""");
        validate(
            hasSurrogateKey && usesWithIgnoredFields,
            """
            you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed.
            You can remove withIgnoredFields.""");
    }

    public static void validateGivenAnnotations(Class<?>... givenAnnotations) {
        Arrays.stream(givenAnnotations).forEach(a -> validateIsAnnotation(a));
    }

    private static void validateIsAnnotation(Class<?> type) {
        validate(!type.isAnnotation(), "class " + type.getCanonicalName() + " is not an annotation.");
    }

    public static void validateProcessedAnnotations(
            Class<?> type,
            AnnotationCache cache,
            Set<Warning> warnings,
            Set<String> includedFields,
            Set<String> excludedFields) {
        validateClassAnnotations(type, cache, warnings, includedFields, excludedFields);
        validateFieldAnnotations(type, cache, warnings, includedFields);
    }

    // CHECKSTYLE OFF: VariableDeclarationUsageDistance
    private static void validateClassAnnotations(
            Class<?> type,
            AnnotationCache cache,
            Set<Warning> warnings,
            Set<String> includedFields,
            Set<String> excludedFields) {
        boolean usesWithOnlyTheseFields = !includedFields.isEmpty();
        boolean usesWithIgnoredFields = !excludedFields.isEmpty();
        boolean hasNaturalId = cache.hasClassAnnotation(type, SupportedAnnotations.NATURALID);
        boolean hasSurrogateKey = warnings.contains(Warning.SURROGATE_KEY);
        boolean hasVersionedEntity = warnings.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY);

        validate(
            hasNaturalId && usesWithOnlyTheseFields,
            "you can't use withOnlyTheseFields when fields are marked with @NaturalId.");
        validate(
            hasNaturalId && usesWithIgnoredFields,
            "you can't use withIgnoredFields when fields are marked with @NaturalId.");
        validate(
            hasNaturalId && hasSurrogateKey,
            "you can't suppress Warning.SURROGATE_KEY when fields are marked @NaturalId.");
        validate(
            hasNaturalId && hasVersionedEntity,
            "you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when fields are marked with @NaturalId.");
    }

    // CHECKSTYLE ON: VariableDeclarationUsageDistance

    private static void validateFieldAnnotations(
            Class<?> type,
            AnnotationCache cache,
            Set<Warning> warnings,
            Set<String> includedFields) {
        FieldIterable.of(type).forEach(p -> validateFieldAnnotation(type, p, cache, warnings, includedFields));
    }

    private static void validateFieldAnnotation(
            Class<?> type,
            FieldProbe p,
            AnnotationCache cache,
            Set<Warning> warnings,
            Set<String> includedFields) {
        validate(
            includedFields.contains(p.getName())
                    && cache.hasFieldAnnotation(type, p.getName(), SupportedAnnotations.ID)
                    && !warnings.contains(Warning.SURROGATE_OR_BUSINESS_KEY),
            """
            you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.
            Suppress Warning.SURROGATE_KEY and remove withOnlyTheseFields\
             if you want to use only the @Id or @EmbeddedId fields in equals.""");
    }

    public static void validatePackageContainsClasses(String packageName, List<Class<?>> types) {
        validate(types.size() == 0, "package " + packageName + " doesn't contain any (non-Test) types.");
    }

    public static void validateTypesAreKnown(Collection<Class<?>> types, List<Class<?>> knownTypes) {
        List<Class<?>> unknownTypes = types.stream().filter(t -> !knownTypes.contains(t)).toList();
        String message = "Unknown class(es) found: "
                + unknownTypes.stream().map(t -> t.getCanonicalName()).collect(Collectors.joining(", "));
        validate(!unknownTypes.isEmpty(), message);
    }

    public static <T> void validateEqual(T red, T blue, String message) {
        validate(!red.equals(blue), message);
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
