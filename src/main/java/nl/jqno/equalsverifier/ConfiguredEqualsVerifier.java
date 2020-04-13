package nl.jqno.equalsverifier;

import java.util.Collections;
import java.util.EnumSet;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.api.EqualsVerifierApi;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.util.PrefabValuesApi;

public final class ConfiguredEqualsVerifier implements EqualsVerifierApi<Void> {
    private final EnumSet<Warning> warningsToSuppress = EnumSet.noneOf(Warning.class);
    private final FactoryCache factoryCache = new FactoryCache();
    private boolean usingGetClass = false;

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
}
