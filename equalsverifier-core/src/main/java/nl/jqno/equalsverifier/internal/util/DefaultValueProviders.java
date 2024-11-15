package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.reflection.instantiation.*;
import nl.jqno.equalsverifier.internal.reflection.instantiation.GenericPrefabValueProvider.GenericFactories;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import org.objenesis.Objenesis;

public final class DefaultValueProviders {

    private DefaultValueProviders() {}

    public static ValueProvider create(
        FactoryCache factoryCache,
        PrefabValueProvider prefabValueProvider,
        GenericFactories genericFactories,
        Objenesis objenesis
    ) {
        ChainedValueProvider mainChain = new ChainedValueProvider(prefabValueProvider);
        ChainedValueProvider vintageChain = new ChainedValueProvider(prefabValueProvider);

        ValueProvider vintage = new VintageValueProvider(vintageChain, factoryCache, objenesis);
        ValueProvider genericPrefab = new GenericPrefabValueProvider(genericFactories, mainChain);

        mainChain.register(genericPrefab, vintage);
        vintageChain.register(genericPrefab);

        return mainChain;
    }
}
