package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCacheBuilder;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;

import java.util.*;

public class Configuration<T> {
    private final Class<T> type;
    private final Set<String> excludedFields;
    private final Set<String> includedFields;
    private final Set<String> nonnullFields;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final boolean hasRedefinedSuperclass;
    private final Class<? extends T> redefinedSubclass;
    private final boolean usingGetClass;
    private final EnumSet<Warning> warningsToSuppress;

    private final TypeTag typeTag;
    private final PrefabValues prefabValues;
    private final ClassAccessor<T> classAccessor;
    private final AnnotationCache annotationCache;
    private final Set<String> ignoredFields;

    private final List<T> equalExamples;
    private final List<T> unequalExamples;

    // CHECKSTYLE: ignore ParameterNumber for 1 line.
    public Configuration(Class<T> type, TypeTag typeTag, ClassAccessor<T> classAccessor, PrefabValues prefabValues,
                Set<String> excludedFields, Set<String> includedFields, Set<String> ignoredFields, Set<String> nonnullFields,
                AnnotationCache annotationCache, CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean hasRedefinedSuperclass,
                Class<? extends T> redefinedSubclass, boolean usingGetClass, EnumSet<Warning> warningsToSuppress,
                List<T> equalExamples, List<T> unequalExamples) {
        this.type = type;
        this.typeTag = typeTag;
        this.classAccessor = classAccessor;
        this.prefabValues = prefabValues;
        this.excludedFields = excludedFields;
        this.includedFields = includedFields;
        this.ignoredFields = ignoredFields;
        this.nonnullFields = nonnullFields;
        this.annotationCache = annotationCache;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
        this.hasRedefinedSuperclass = hasRedefinedSuperclass;
        this.redefinedSubclass = redefinedSubclass;
        this.usingGetClass = usingGetClass;
        this.warningsToSuppress = warningsToSuppress;
        this.equalExamples = equalExamples;
        this.unequalExamples = unequalExamples;
    }

    // CHECKSTYLE: ignore ParameterNumber for 1 line.
    public static <T> Configuration<T> build(Class<T> type, Set<String> excludedFields, Set<String> includedFields,
                Set<String> nonnullFields, CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean hasRedefinedSuperclass,
                Class<? extends T> redefinedSubclass, boolean usingGetClass, EnumSet<Warning> warningsToSuppress,
                FactoryCache factoryCache, Set<String> ignoredAnnotationDescriptors, Set<String> actualFields,
                List<T> equalExamples, List<T> unequalExamples) {

        TypeTag typeTag = new TypeTag(type);
        FactoryCache cache = JavaApiPrefabValues.build().merge(factoryCache);
        PrefabValues prefabValues = new PrefabValues(cache);
        ClassAccessor<T> classAccessor = ClassAccessor.of(type, prefabValues);
        AnnotationCache annotationCache = buildAnnotationCache(type, ignoredAnnotationDescriptors);
        Set<String> ignoredFields = includedFields.isEmpty() ? excludedFields : invertIncludedFields(actualFields, includedFields);
        List<T> unequals = ensureUnequalExamples(typeTag, classAccessor, unequalExamples);

        return new Configuration<>(type, typeTag, classAccessor, prefabValues, excludedFields, includedFields, ignoredFields,
            nonnullFields, annotationCache, cachedHashCodeInitializer, hasRedefinedSuperclass, redefinedSubclass, usingGetClass,
            warningsToSuppress, equalExamples, unequals);
    }

    private static <T> AnnotationCache buildAnnotationCache(Class<T> type, Set<String> ignoredAnnotationDescriptors) {
        AnnotationCacheBuilder acb = new AnnotationCacheBuilder(SupportedAnnotations.values(), ignoredAnnotationDescriptors);
        AnnotationCache cache = new AnnotationCache();
        acb.build(type, cache);
        return cache;
    }

    private static Set<String> invertIncludedFields(Set<String> actualFields, Set<String> includedFields) {
        Set<String> result = new HashSet<>();
        for (String name : actualFields) {
            if (!includedFields.contains(name)) {
                result.add(name);
            }
        }
        return result;
    }

    private static <T> List<T> ensureUnequalExamples(TypeTag typeTag, ClassAccessor<T> classAccessor, List<T> examples) {
        if (examples.size() > 0) {
            return examples;
        }

        List<T> result = new ArrayList<>();
        result.add(classAccessor.getRedObject(typeTag));
        result.add(classAccessor.getBlackObject(typeTag));
        return result;
    }

    public Class<T> getType() {
        return type;
    }

    public Set<String> getExcludedFields() {
        return Collections.unmodifiableSet(excludedFields);
    }

    public Set<String> getIncludedFields() {
        return Collections.unmodifiableSet(includedFields);
    }

    public Set<String> getNonnullFields() {
        return Collections.unmodifiableSet(nonnullFields);
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

    public List<T> getEqualExamples() {
        return Collections.unmodifiableList(equalExamples);
    }

    public List<T> getUnequalExamples() {
        return Collections.unmodifiableList(unequalExamples);
    }

    public TypeTag getTypeTag() {
        return typeTag;
    }

    public PrefabValues getPrefabValues() {
        return prefabValues;
    }

    public ClassAccessor<T> getClassAccessor() {
        return classAccessor;
    }

    public AnnotationCache getAnnotationCache() {
        return annotationCache;
    }

    public Set<String> getIgnoredFields() {
        return Collections.unmodifiableSet(ignoredFields);
    }
}
