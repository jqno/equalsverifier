package nl.jqno.equalsverifier;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.api.EqualsVerifierApi;
import nl.jqno.equalsverifier.api.MultipleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.internal.reflection.PackageScanner;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.util.ListBuilders;
import nl.jqno.equalsverifier.internal.util.PrefabValuesApi;
import nl.jqno.equalsverifier.internal.util.Validations;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public final class ConfiguredEqualsVerifier implements EqualsVerifierApi<Void> {

    private final EnumSet<Warning> warningsToSuppress;
    private final FactoryCache factoryCache;
    private boolean usingGetClass;
    private Function<String, String> fieldnameToGetter;
    private final Objenesis objenesis = new ObjenesisStd();

    /** Constructor. */
    public ConfiguredEqualsVerifier() {
        this(EnumSet.noneOf(Warning.class), new FactoryCache(), false, null);
    }

    /** Private constructor. For internal use only. */
    private ConfiguredEqualsVerifier(
        EnumSet<Warning> warningsToSuppress,
        FactoryCache factoryCache,
        boolean usingGetClass,
        Function<String, String> fieldnameToGetter
    ) {
        this.warningsToSuppress = warningsToSuppress;
        this.factoryCache = factoryCache;
        this.usingGetClass = usingGetClass;
        this.fieldnameToGetter = fieldnameToGetter;
    }

    /**
     * Returns a copy of the configuration.
     *
     * @return a copy of the configuration.
     */
    public ConfiguredEqualsVerifier copy() {
        return new ConfiguredEqualsVerifier(
            EnumSet.copyOf(warningsToSuppress),
            new FactoryCache().merge(factoryCache),
            usingGetClass,
            fieldnameToGetter
        );
    }

    /** {@inheritDoc} */
    @Override
    public ConfiguredEqualsVerifier suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> ConfiguredEqualsVerifier withPrefabValues(Class<S> otherType, S red, S blue) {
        PrefabValuesApi.addPrefabValues(factoryCache, objenesis, otherType, red, blue);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> ConfiguredEqualsVerifier withGenericPrefabValues(
        Class<S> otherType,
        Func1<?, S> factory
    ) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> ConfiguredEqualsVerifier withGenericPrefabValues(
        Class<S> otherType,
        Func2<?, ?, S> factory
    ) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ConfiguredEqualsVerifier usingGetClass() {
        usingGetClass = true;
        return this;
    }

    @Override
    public ConfiguredEqualsVerifier withFieldnameToGetterConverter(
        Function<String, String> converter
    ) {
        this.fieldnameToGetter = converter;
        return this;
    }

    /**
     * {@inheritDoc}
     * @deprecated No longer needed; this happens automatically.
     */
    @Deprecated
    @Override
    public ConfiguredEqualsVerifier withResetCaches() {
        return this;
    }

    /**
     * Factory method. For general use.
     *
     * @param <T> The type.
     * @param type The class for which the {@code equals} method should be tested.
     * @return A fluent API for EqualsVerifier.
     */
    public <T> SingleTypeEqualsVerifierApi<T> forClass(Class<T> type) {
        return new SingleTypeEqualsVerifierApi<>(
            type,
            EnumSet.copyOf(warningsToSuppress),
            new FactoryCache().merge(factoryCache),
            objenesis,
            usingGetClass,
            fieldnameToGetter
        );
    }

    /**
     * Factory method. For general use.
     *
     * @param classes An iterable containing the classes for which {@code equals} method should be
     *     tested.
     * @return A fluent API for EqualsVerifier.
     */
    public MultipleTypeEqualsVerifierApi forClasses(Iterable<Class<?>> classes) {
        return new MultipleTypeEqualsVerifierApi(ListBuilders.fromIterable(classes), this);
    }

    /**
     * Factory method. For general use.
     *
     * @param first A class for which the {@code equals} method should be tested.
     * @param second Another class for which the {@code equals} method should be tested.
     * @param more More classes for which the {@code equals} method should be tested.
     * @return A fluent API for EqualsVerifier.
     */
    public MultipleTypeEqualsVerifierApi forClasses(
        Class<?> first,
        Class<?> second,
        Class<?>... more
    ) {
        return new MultipleTypeEqualsVerifierApi(
            ListBuilders.buildListOfAtLeastTwo(first, second, more),
            this
        );
    }

    /**
     * Factory method. For general use.
     *
     * <p>Note that this operation may be slow. If the test is too slow, use {@link
     * #forClasses(Class, Class, Class...)} instead.
     *
     * @param packageName A package for which each class's {@code equals} should be tested.
     * @return A fluent API for EqualsVerifier.
     */
    public MultipleTypeEqualsVerifierApi forPackage(String packageName) {
        return forPackage(packageName, false);
    }

    /**
     * Factory method. For general use.
     *
     * <p>Note that this operation may be slow. If the test is too slow, use {@link
     * #forClasses(Class, Class, Class...)} instead.
     *
     * @param packageName A package for which each class's {@code equals} should be tested.
     * @param scanRecursively true to scan all sub-packages
     * @return A fluent API for EqualsVerifier.
     */
    public MultipleTypeEqualsVerifierApi forPackage(String packageName, boolean scanRecursively) {
        List<Class<?>> classes = PackageScanner.getClassesIn(packageName, null, scanRecursively);
        Validations.validatePackageContainsClasses(packageName, classes);
        return new MultipleTypeEqualsVerifierApi(classes, this);
    }

    /**
     * Factory method. For general use.
     *
     * <p>Note that this operation may be slow. If the test is too slow, use {@link
     * #forClasses(Class, Class, Class...)} instead.
     *
     * <p>Also note that if {@code mustExtend} is given, and it exists within {@code packageName},
     * it will NOT be included.
     *
     * @param packageName A package for which each class's {@code equals} should be tested.
     * @param mustExtend if not null, returns only classes that extend or implement this class.
     * @return A fluent API for EqualsVerifier.
     */
    public MultipleTypeEqualsVerifierApi forPackage(String packageName, Class<?> mustExtend) {
        List<Class<?>> classes = PackageScanner.getClassesIn(packageName, mustExtend, true);
        Validations.validatePackageContainsClasses(packageName, classes);
        return new MultipleTypeEqualsVerifierApi(classes, this);
    }
}
