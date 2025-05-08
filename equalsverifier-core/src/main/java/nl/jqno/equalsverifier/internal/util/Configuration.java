package nl.jqno.equalsverifier.internal.util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.annotations.*;

public final class Configuration<T> {

    private final Class<T> type;
    private final Set<String> nonnullFields;
    private final Set<String> prefabbedFields;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final boolean hasRedefinedSuperclass;
    private final Class<? extends T> redefinedSubclass;
    private final boolean usingGetClass;
    private final EnumSet<Warning> warningsToSuppress;
    private final Set<Mode> modes;
    private final Function<String, String> fieldnameToGetter;
    private final boolean isKotlin;

    private final TypeTag typeTag;
    private final AnnotationCache annotationCache;
    private final Set<String> ignoredFields;

    private final List<T> equalExamples;
    private final List<T> unequalExamples;

    // CHECKSTYLE OFF: ParameterNumber
    private Configuration(
            Class<T> type,
            TypeTag typeTag,
            Set<String> ignoredFields,
            Set<String> nonnullFields,
            Set<String> prefabbedFields,
            AnnotationCache annotationCache,
            CachedHashCodeInitializer<T> cachedHashCodeInitializer,
            boolean hasRedefinedSuperclass,
            Class<? extends T> redefinedSubclass,
            boolean usingGetClass,
            EnumSet<Warning> warningsToSuppress,
            Set<Mode> modes,
            Function<String, String> fieldnameToGetter,
            boolean isKotlin,
            List<T> equalExamples,
            List<T> unequalExamples) {
        this.type = type;
        this.typeTag = typeTag;
        this.ignoredFields = ignoredFields;
        this.nonnullFields = nonnullFields;
        this.prefabbedFields = prefabbedFields;
        this.annotationCache = annotationCache;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
        this.hasRedefinedSuperclass = hasRedefinedSuperclass;
        this.redefinedSubclass = redefinedSubclass;
        this.usingGetClass = usingGetClass;
        this.warningsToSuppress = warningsToSuppress;
        this.modes = modes;
        this.fieldnameToGetter = fieldnameToGetter;
        this.isKotlin = isKotlin;
        this.equalExamples = equalExamples;
        this.unequalExamples = unequalExamples;
    }

    public static <T> Configuration<T> build(
            Class<T> type,
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
        boolean isKotlin = annotationCache.hasClassAnnotation(type, SupportedAnnotations.KOTLIN);

        return new Configuration<>(type,
                typeTag,
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

    // CHECKSTYLE ON: ParameterNumber

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

    public Class<T> getType() {
        return type;
    }

    public Set<String> getNonnullFields() {
        return Collections.unmodifiableSet(nonnullFields);
    }

    public Set<String> getPrefabbedFields() {
        return Collections.unmodifiableSet(prefabbedFields);
    }

    public CachedHashCodeInitializer<T> getCachedHashCodeInitializer() {
        return cachedHashCodeInitializer;
    }

    public boolean hasRedefinedSuperclass() {
        return hasRedefinedSuperclass;
    }

    public Class<? extends T> getRedefinedSubclass() {
        return redefinedSubclass;
    }

    public boolean isUsingGetClass() {
        return usingGetClass;
    }

    public EnumSet<Warning> getWarningsToSuppress() {
        return EnumSet.copyOf(warningsToSuppress);
    }

    public Set<Mode> getModes() {
        return Collections.unmodifiableSet(modes);
    }

    public Function<String, String> getFieldnameToGetter() {
        return fieldnameToGetter;
    }

    public boolean isKotlin() {
        return isKotlin;
    }

    public List<T> getEqualExamples() {
        return Collections.unmodifiableList(equalExamples);
    }

    public List<T> getUnequalExamples() {
        return Collections.unmodifiableList(unequalExamples);
    }

    public TypeTag getTypeTag() {
        return typeTag;
    }

    public AnnotationCache getAnnotationCache() {
        return annotationCache;
    }

    public Set<String> getIgnoredFields() {
        return Collections.unmodifiableSet(ignoredFields);
    }
}
