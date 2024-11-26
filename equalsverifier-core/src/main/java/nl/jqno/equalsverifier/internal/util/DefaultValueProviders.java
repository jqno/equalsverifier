package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.reflection.instantiation.CachedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ChainedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.vintage.VintageValueProvider;
import org.objenesis.Objenesis;

public final class DefaultValueProviders {

    private DefaultValueProviders() {}

    public static ValueProvider create(FactoryCache factoryCache, Objenesis objenesis) {
        CachedValueProvider cache = new CachedValueProvider();

        ChainedValueProvider mainChain = new ChainedValueProvider(cache);
        ChainedValueProvider vintageChain = new ChainedValueProvider(cache);

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
