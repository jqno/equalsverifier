package nl.jqno.equalsverifier.api;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.*;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.PrefabValuesApi;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueCaches;
import nl.jqno.equalsverifier.internal.reflection.PackageScanOptions;
import nl.jqno.equalsverifier.internal.reflection.PackageScanner;
import nl.jqno.equalsverifier.internal.util.FieldToPrefabValues;
import nl.jqno.equalsverifier.internal.util.ListBuilders;
import nl.jqno.equalsverifier.internal.util.Validations;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * Keeps a re-usable configuration for EqualsVerifier.
 *
 * @since 3.0
 */
public final class ConfiguredEqualsVerifier implements EqualsVerifierApi<Void> {

    private final EnumSet<Warning> warningsToSuppress;
    private final Set<Mode> modesToSet;
    private final UserPrefabValueCaches userPrefabs;
    private boolean usingGetClass;
    private Function<String, String> fieldnameToGetter;
    private final Objenesis objenesis = new ObjenesisStd();

    /**
     * Constructor.
     *
     * @since 3.0
     */
    public ConfiguredEqualsVerifier() {
        this(EnumSet.noneOf(Warning.class), new HashSet<>(), new UserPrefabValueCaches(), false, null);
    }

    /** Private constructor. For internal use only. */
    private ConfiguredEqualsVerifier(
            EnumSet<Warning> warningsToSuppress,
            Set<Mode> modes,
            UserPrefabValueCaches userPrefabs,
            boolean usingGetClass,
            Function<String, String> fieldnameToGetter) {
        this.warningsToSuppress = warningsToSuppress;
        this.modesToSet = modes;
        this.userPrefabs = userPrefabs;
        this.usingGetClass = usingGetClass;
        this.fieldnameToGetter = fieldnameToGetter;
    }

    /**
     * Returns a copy of the configuration.
     *
     * @return a copy of the configuration.
     *
     * @since 3.2
     */
    @CheckReturnValue
    public ConfiguredEqualsVerifier copy() {
        return new ConfiguredEqualsVerifier(EnumSet.copyOf(warningsToSuppress),
                new HashSet<>(modesToSet),
                userPrefabs.copy(),
                usingGetClass,
                fieldnameToGetter);
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public ConfiguredEqualsVerifier suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        return this;
    }

    /** {@inheritDoc}} */
    @Override
    public ConfiguredEqualsVerifier set(Mode... modes) {
        Collections.addAll(modesToSet, modes);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public <S> ConfiguredEqualsVerifier withPrefabValues(Class<S> otherType, S red, S blue) {
        PrefabValuesApi.addPrefabValues(userPrefabs, objenesis, otherType, red, blue);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public <S> ConfiguredEqualsVerifier withResettablePrefabValues(
            Class<S> otherType,
            Supplier<S> red,
            Supplier<S> blue) {
        PrefabValuesApi.addResettablePrefabValues(userPrefabs, objenesis, otherType, red, blue);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public <S> ConfiguredEqualsVerifier withGenericPrefabValues(Class<S> otherType, Func1<?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(userPrefabs, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public <S> ConfiguredEqualsVerifier withGenericPrefabValues(Class<S> otherType, Func2<?, ?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(userPrefabs, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @CheckReturnValue
    public ConfiguredEqualsVerifier usingGetClass() {
        usingGetClass = true;
        return this;
    }

    @Override
    @CheckReturnValue
    public ConfiguredEqualsVerifier withFieldnameToGetterConverter(Function<String, String> converter) {
        this.fieldnameToGetter = converter;
        return this;
    }

    /**
     * Factory method. For general use.
     *
     * @param <T>  The type.
     * @param type The class for which the {@code equals} method should be tested.
     * @return A fluent API for EqualsVerifier.
     *
     * @since 3.0
     */
    @CheckReturnValue
    public <T> SingleTypeEqualsVerifierApi<T> forClass(Class<T> type) {
        return new SingleTypeEqualsVerifierApi<>(type,
                EnumSet.copyOf(warningsToSuppress),
                new HashSet<>(modesToSet),
                userPrefabs.copy(),
                objenesis,
                usingGetClass,
                fieldnameToGetter);
    }

    /**
     * Factory method. For general use.
     *
     * @param red  An example of T.
     * @param blue An example of T where all fields have different values than {@code red}.
     * @param <T>  The type.
     * @return A fluent API for EqualsVerifier.
     *
     * @since 4.0
     */
    @CheckReturnValue
    @SuppressWarnings("unchecked")
    public <T> SingleTypeEqualsVerifierApi<T> forExamples(T red, T blue) {
        Validations.validateRedAndBlueExamples(red, blue);

        var type = (Class<T>) red.getClass();
        var api = forClass(type);
        FieldToPrefabValues.move(api, type, red, blue);
        return api;
    }

    /**
     * Factory method. For general use.
     *
     * @param classes An iterable containing the classes for which {@code equals} method should be tested.
     * @return A fluent API for EqualsVerifier.
     *
     * @since 3.3
     */
    @CheckReturnValue
    public MultipleTypeEqualsVerifierApi forClasses(Iterable<Class<?>> classes) {
        return new MultipleTypeEqualsVerifierApi(ListBuilders.fromIterable(classes), this);
    }

    /**
     * Factory method. For general use.
     *
     * @param first  A class for which the {@code equals} method should be tested.
     * @param second Another class for which the {@code equals} method should be tested.
     * @param more   More classes for which the {@code equals} method should be tested.
     * @return A fluent API for EqualsVerifier.
     *
     * @since 3.2
     */
    @CheckReturnValue
    public MultipleTypeEqualsVerifierApi forClasses(Class<?> first, Class<?> second, Class<?>... more) {
        return new MultipleTypeEqualsVerifierApi(ListBuilders.buildListOfAtLeastTwo(first, second, more), this);
    }

    /**
     * Factory method. For general use.
     *
     * <p>
     * Note that this operation may be slow. If the test is too slow, use {@link #forClasses(Class, Class, Class...)}
     * instead.
     *
     * @param packageName A package for which each class's {@code equals} should be tested.
     * @param options     Modifications to the standard package scanning behaviour.
     * @return A fluent API for EqualsVerifier.
     *
     * @since 3.2 / 3.19
     */
    @CheckReturnValue
    public MultipleTypeEqualsVerifierApi forPackage(String packageName, ScanOption... options) {
        PackageScanOptions opts = PackageScanOptions.process(options);
        List<Class<?>> classes = PackageScanner.getClassesIn(packageName, opts);
        Validations.validatePackageContainsClasses(packageName, classes);
        return new MultipleTypeEqualsVerifierApi(classes, this);
    }
}
