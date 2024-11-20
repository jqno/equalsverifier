package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.reflection.instantiation.*;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import org.objenesis.Objenesis;

public final class DefaultValueProviders {

    private DefaultValueProviders() {}

    public static ValueProvider create(FactoryCache factoryCache, Objenesis objenesis) {
        ChainedValueProvider mainChain = new ChainedValueProvider();
        ChainedValueProvider vintageChain = new ChainedValueProvider();

        CachedValueProvider cache = new CachedValueProvider();
        ValueProvider vintage = new VintageValueProvider(
            vintageChain,
            cache,
            factoryCache,
            objenesis
        );

        mainChain.register(cache, vintage);
        vintageChain.register(cache);

        return mainChain;
    }
}
