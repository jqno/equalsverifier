package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Warning;
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
    public Configuration(Class<T> type, Set<String> excludedFields, Set<String> includedFields,
                Set<String> nonnullFields, CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean hasRedefinedSuperclass,
                Class<? extends T> redefinedSubclass, boolean usingGetClass, EnumSet<Warning> warningsToSuppress,
                PrefabValues prefabValues, Set<String> ignoredAnnotationDescriptors, Set<String> actualFields,
                List<T> equalExamples, List<T> unequalExamples) {
        this.type = type;
        this.excludedFields = excludedFields;
        this.includedFields = includedFields;
        this.nonnullFields = nonnullFields;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
        this.hasRedefinedSuperclass = hasRedefinedSuperclass;
        this.redefinedSubclass = redefinedSubclass;
        this.usingGetClass = usingGetClass;
        this.warningsToSuppress = warningsToSuppress;

        this.typeTag = new TypeTag(type);
        this.prefabValues = prefabValues;
        JavaApiPrefabValues.addTo(prefabValues);
        this.classAccessor = ClassAccessor.of(type, prefabValues);
        this.annotationCache = buildAnnotationCache(ignoredAnnotationDescriptors);
        this.ignoredFields = includedFields.isEmpty() ? excludedFields : invertIncludedFields(actualFields);

        this.equalExamples = equalExamples;
        this.unequalExamples = ensureUnequalExamples(unequalExamples);
    }

    private List<T> ensureUnequalExamples(List<T> examples) {
        if (examples.size() > 0) {
            return examples;
        }

        List<T> result = new ArrayList<>();
        result.add(classAccessor.getRedObject(typeTag));
        result.add(classAccessor.getBlackObject(typeTag));
        return result;
    }

    private AnnotationCache buildAnnotationCache(Set<String> ignoredAnnotationDescriptors) {
        AnnotationCacheBuilder acb = new AnnotationCacheBuilder(SupportedAnnotations.values(), ignoredAnnotationDescriptors);
        AnnotationCache cache = new AnnotationCache();
        acb.build(type, cache);
        return cache;
    }

    private Set<String> invertIncludedFields(Set<String> actualFields) {
        Set<String> result = new HashSet<>();
        for (String name : actualFields) {
            if (!includedFields.contains(name)) {
                result.add(name);
            }
        }
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
