package nl.jqno.equalsverifier;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.api.EqualsVerifierApi;
import nl.jqno.equalsverifier.api.MultipleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.PackageScanner;
import nl.jqno.equalsverifier.internal.util.ListBuilders;
import nl.jqno.equalsverifier.internal.util.PrefabValuesApi;

public final class ConfiguredEqualsVerifier implements EqualsVerifierApi<Void> {
    private final EnumSet<Warning> warningsToSuppress;
    private final FactoryCache factoryCache;
    private boolean usingGetClass;

    /** Constructor. */
    public ConfiguredEqualsVerifier() {
        this(EnumSet.noneOf(Warning.class), new FactoryCache(), false);
    }

    /** Private constructor. For internal use only. */
    private ConfiguredEqualsVerifier(
            EnumSet<Warning> warningsToSuppress, FactoryCache factoryCache, boolean usingGetClass) {
        this.warningsToSuppress = warningsToSuppress;
        this.factoryCache = factoryCache;
        this.usingGetClass = usingGetClass;
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
                usingGetClass);
    }

    /** {@inheritDoc} */
    @Override
    public ConfiguredEqualsVerifier suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> ConfiguredEqualsVerifier withPrefabValues(Class<S> otherType, S red, S black) {
        PrefabValuesApi.addPrefabValues(factoryCache, otherType, red, black);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> ConfiguredEqualsVerifier withGenericPrefabValues(
            Class<S> otherType, Func1<?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <S> ConfiguredEqualsVerifier withGenericPrefabValues(
            Class<S> otherType, Func2<?, ?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ConfiguredEqualsVerifier usingGetClass() {
        usingGetClass = true;
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
                type, EnumSet.copyOf(warningsToSuppress), factoryCache, usingGetClass);
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
            Class<?> first, Class<?> second, Class<?>... more) {
        return new MultipleTypeEqualsVerifierApi(
                ListBuilders.buildListOfAtLeastTwo(first, second, more), this);
    }

    /**
     * Factory method. For general use.
     *
     * @param packageName A package for which each class's {@code equals} should be tested.
     * @return A fluent API for EqualsVerifier.
     */
    public MultipleTypeEqualsVerifierApi forPackage(String packageName) {
        List<Class<?>> classes = PackageScanner.getClassesIn(packageName);
        return new MultipleTypeEqualsVerifierApi(classes, new ConfiguredEqualsVerifier());
    }
}
