package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.util.PrefabValuesApi;

import java.util.Collections;
import java.util.EnumSet;

public final class ConfiguredEqualsVerifierApi {
    private final EnumSet<Warning> warningsToSuppress = EnumSet.noneOf(Warning.class);
    private final FactoryCache factoryCache = new FactoryCache();
    private boolean usingGetClass = false;

    public ConfiguredEqualsVerifierApi suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        return this;
    }

    public <S> ConfiguredEqualsVerifierApi withPrefabValues(Class<S> otherType, S red, S black) {
        PrefabValuesApi.addPrefabValues(factoryCache, otherType, red, black);
        return this;
    }

    public <S> ConfiguredEqualsVerifierApi withGenericPrefabValues(Class<S> otherType, Func1<?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    public <S> ConfiguredEqualsVerifierApi withGenericPrefabValues(Class<S> otherType, Func2<?, ?, S> factory) {
        PrefabValuesApi.addGenericPrefabValues(factoryCache, otherType, factory);
        return this;
    }

    public ConfiguredEqualsVerifierApi usingGetClass() {
        usingGetClass = true;
        return this;
    }

    public <T> EqualsVerifierApi<T> forClass(Class<T> type) {
        return new EqualsVerifierApi<>(type, EnumSet.copyOf(warningsToSuppress), factoryCache, usingGetClass);
    }
}
