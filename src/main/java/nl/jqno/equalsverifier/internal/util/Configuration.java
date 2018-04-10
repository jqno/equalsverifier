package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;

import java.util.*;

public final class Configuration<T> {
    private final Class<T> type;
    private final TypeTag typeTag;
    private final PrefabValues prefabValues;

    private final List<T> equalExamples;
    private final List<T> unequalExamples;

    private final Set<String> actualFields;
    private final Set<String> excludedFields;
    private final Set<String> includedFields;
    private final Set<String> nonnullFields;
    private final Set<String> ignoredAnnotations;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final boolean hasRedefinedSuperclass;
    private final Class<? extends T> redefinedSubclass;
    private final boolean usingGetClass;
    private final EnumSet<Warning> warningsToSuppress;

    // CHECKSTYLE: ignore ParameterNumber for 1 line.
    private Configuration(Class<T> type, PrefabValues prefabValues, List<T> equalExamples, List<T> unequalExamples, Set<String> actualFields,
                          Set<String> excludedFields, Set<String> includedFields, Set<String> nonnullFields, Set<String> ignoredAnnotations,
                          CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean hasRedefinedSuperclass,
                          Class<? extends T> redefinedSubclass, boolean usingGetClass, EnumSet<Warning> warningsToSuppress) {

        this.type = type;
        this.typeTag = new TypeTag(type);
        this.prefabValues = prefabValues;
        this.equalExamples = equalExamples;
        this.unequalExamples = unequalExamples;
        this.actualFields = actualFields;
        this.excludedFields = excludedFields;
        this.includedFields = includedFields;
        this.nonnullFields = nonnullFields;
        this.ignoredAnnotations = ignoredAnnotations;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
        this.hasRedefinedSuperclass = hasRedefinedSuperclass;
        this.redefinedSubclass = redefinedSubclass;
        this.usingGetClass = usingGetClass;
        this.warningsToSuppress = warningsToSuppress;
    }

    public static <T> Configuration<T> of(Class<T> type) {
        return new Configuration<>(type, new PrefabValues(), new ArrayList<T>(), new ArrayList<T>(),
                FieldNameExtractor.extractFieldNames(type), new HashSet<String>(), new HashSet<String>(),
                new HashSet<String>(), new HashSet<String>(), CachedHashCodeInitializer.<T>passthrough(),
                false, null, false, EnumSet.noneOf(Warning.class));
    }

    public Class<T> getType() {
        return type;
    }

    public TypeTag getTypeTag() {
        return typeTag;
    }

    public PrefabValues getPrefabValues() {
        return prefabValues;
    }

    public Configuration<T> withEqualExamples(List<T> value) {
        return new Configuration<>(type, prefabValues, value, unequalExamples, actualFields, excludedFields,
                includedFields, nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public List<T> getEqualExamples() {
        return Collections.unmodifiableList(equalExamples);
    }

    public Configuration<T> withUnequalExamples(List<T> value) {
        return new Configuration<>(type, prefabValues, equalExamples, value, actualFields, excludedFields,
                includedFields, nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public List<T> getUnequalExamples() {
        return Collections.unmodifiableList(unequalExamples);
    }

    public Set<String> getActualFields() {
        return Collections.unmodifiableSet(actualFields);
    }

    public Set<String> getExcludedFields() {
        return Collections.unmodifiableSet(excludedFields);
    }

    public Configuration<T> withExcludedFields(List<String> value) {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, new HashSet<>(value),
                includedFields, nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public Set<String> getIncludedFields() {
        return Collections.unmodifiableSet(includedFields);
    }

    public Configuration<T> withIncludedFields(List<String> value) {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                new HashSet<>(value), nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public Set<String> getIgnoredFields() {
        return Collections.unmodifiableSet(includedFields.isEmpty() ? excludedFields : invertIncludedFields());
    }

    public Configuration<T> withNonnullFields(List<String> value) {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                includedFields, new HashSet<>(value), ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public Set<String> getNonnullFields() {
        return Collections.unmodifiableSet(nonnullFields);
    }

    public Configuration<T> withIgnoredAnnotations(List<String> value) {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                includedFields, nonnullFields, new HashSet<>(value), cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public Set<String> getIgnoredAnnotations() {
        return Collections.unmodifiableSet(ignoredAnnotations);
    }

    public Configuration<T> withCachedHashCodeInitializer(CachedHashCodeInitializer<T> value) {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                includedFields, nonnullFields, ignoredAnnotations, value, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public CachedHashCodeInitializer<T> getCachedHashCodeInitializer() {
        return cachedHashCodeInitializer;
    }

    public Configuration<T> withRedefinedSuperclass() {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                includedFields, nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, true,
                redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public boolean hasRedefinedSuperclass() {
        return hasRedefinedSuperclass;
    }

    public Configuration<T> withRedefinedSubclass(Class<? extends T> value) {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                includedFields, nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                value, usingGetClass, warningsToSuppress);
    }

    public Class<? extends T> getRedefinedSubclass() {
        return redefinedSubclass;
    }

    public Configuration<T> withUsingGetClass() {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                includedFields, nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, true, warningsToSuppress);
    }

    public boolean isUsingGetClass() {
        return usingGetClass;
    }

    public Configuration<T> withWarningsToSuppress(EnumSet<Warning> value) {
        return new Configuration<>(type, prefabValues, equalExamples, unequalExamples, actualFields, excludedFields,
                includedFields, nonnullFields, ignoredAnnotations, cachedHashCodeInitializer, hasRedefinedSuperclass,
                redefinedSubclass, usingGetClass, value);
    }

    public EnumSet<Warning> getWarningsToSuppress() {
        return EnumSet.copyOf(warningsToSuppress);
    }

    public ClassAccessor<T> createClassAccessor() {
        return ClassAccessor.of(type, prefabValues, ignoredAnnotations, warningsToSuppress.contains(Warning.ANNOTATION));
    }

    private Set<String> invertIncludedFields() {
        Set<String> ignoredFields = new HashSet<>();
        for (String name: actualFields) {
            if (!includedFields.contains(name)) {
                ignoredFields.add(name);
            }
        }
        return ignoredFields;
    }
}
