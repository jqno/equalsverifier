package nl.jqno.equalsverifier.internal.util;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.annotations.*;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinProbe;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;

// CHECKSTYLE OFF: ParameterNumber
public record Configuration<T>(Class<T> type, TypeTag typeTag, InstanceFactory<T> factory, Set<String> ignoredFields,
        Set<String> nonnullFields, Set<String> prefabbedFields, AnnotationCache annotationCache,
        CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean hasRedefinedSuperclass,
        Class<? extends T> redefinedSubclass, boolean usingGetClass, EnumSet<Warning> warningsToSuppress,
        Set<Mode> modes, Function<String, String> fieldnameToGetter, boolean isKotlin, List<T> equalExamples,
        List<T> unequalExamples) {

    public static <T> Configuration<T> build(
            Class<T> type,
            InstanceFactory<T> factory,
            Set<String> excludedFields,
            Set<String> includedFields,
            Set<String> nonnullFields,
            Set<String> prefabbedFields,
            CachedHashCodeInitializer<T> cachedHashCodeInitializer,
            boolean hasRedefinedSuperclass,
            Class<? extends T> redefinedSubclass,
            boolean usingGetClass,
            EnumSet<Warning> warningsToSuppress,
            Set<Mode> modes,
            Function<String, String> fieldnameToGetter,
            Set<String> ignoredAnnotationClassNames,
            Set<String> actualFields,
            List<T> equalExamples,
            List<T> unequalExamples) {
        TypeTag typeTag = new TypeTag(type);
        AnnotationCache annotationCache = buildAnnotationCache(type, ignoredAnnotationClassNames);
        Set<String> ignoredFields = determineIgnoredFields(
            type,
            annotationCache,
            warningsToSuppress,
            excludedFields,
            includedFields,
            actualFields);
        Function<String, String> converter =
                fieldnameToGetter != null ? fieldnameToGetter : Configuration::defaulFieldNameToGetterConverter;
        boolean isKotlin = KotlinScreen.isKotlin(type);

        if (isKotlin) {
            for (FieldProbe f : FieldIterable.ofKotlin(type)) {
                if (KotlinScreen.canProbe()
                        && KotlinProbe.isDataClass(type)
                        && !KotlinProbe.isDeclaredInPrimaryConstructor(f.getField())) {
                    ignoredFields.add(f.getName());
                }
                if (KotlinScreen.isSyntheticKotlinDelegate(f.getField())) {
                    nonnullFields.add(f.getName());
                }
            }
        }

        return new Configuration<>(type,
                typeTag,
                factory,
                ignoredFields,
                nonnullFields,
                prefabbedFields,
                annotationCache,
                cachedHashCodeInitializer,
                hasRedefinedSuperclass,
                redefinedSubclass,
                usingGetClass,
                warningsToSuppress,
                modes,
                converter,
                isKotlin,
                equalExamples,
                unequalExamples);
    }

    private static <T> AnnotationCache buildAnnotationCache(Class<T> type, Set<String> ignoredAnnotationClassNames) {
        AnnotationCacheBuilder acb =
                new AnnotationCacheBuilder(SupportedAnnotations.values(), ignoredAnnotationClassNames);
        AnnotationCache cache = new AnnotationCache();
        acb.build(type, cache);
        return cache;
    }

    private static <T> Set<String> determineIgnoredFields(
            Class<T> type,
            AnnotationCache annotationCache,
            EnumSet<Warning> warningsToSuppress,
            Set<String> excludedFields,
            Set<String> includedFields,
            Set<String> actualFields) {
        BiFunction<String, Annotation, Boolean> fieldHas = (f, a) -> annotationCache.hasFieldAnnotation(type, f, a);

        if (annotationCache.hasClassAnnotation(type, SupportedAnnotations.NATURALID)) {
            return actualFields
                    .stream()
                    .filter(f -> !fieldHas.apply(f, SupportedAnnotations.NATURALID))
                    .collect(Collectors.toSet());
        }

        if (annotationCache.hasClassAnnotation(type, SupportedAnnotations.ID)
                && !warningsToSuppress.contains(Warning.SURROGATE_OR_BUSINESS_KEY)) {
            if (warningsToSuppress.contains(Warning.SURROGATE_KEY)) {
                return actualFields
                        .stream()
                        .filter(f -> !fieldHas.apply(f, SupportedAnnotations.ID))
                        .collect(Collectors.toSet());
            }
            else {
                Set<String> ignored = actualFields
                        .stream()
                        .filter(f -> fieldHas.apply(f, SupportedAnnotations.ID))
                        .collect(Collectors.toSet());
                ignored.addAll(determineAnnotationlessIgnoredFields(excludedFields, includedFields, actualFields));
                return ignored;
            }
        }
        return determineAnnotationlessIgnoredFields(excludedFields, includedFields, actualFields);
    }

    private static Set<String> determineAnnotationlessIgnoredFields(
            Set<String> excludedFields,
            Set<String> includedFields,
            Set<String> actualFields) {
        if (!includedFields.isEmpty()) {
            return actualFields.stream().filter(f -> !includedFields.contains(f)).collect(Collectors.toSet());
        }
        return excludedFields;
    }

    private static String defaulFieldNameToGetterConverter(String fieldName) {
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }
}
