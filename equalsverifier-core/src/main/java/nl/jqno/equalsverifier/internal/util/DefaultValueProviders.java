package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.reflection.instantiation.ChainedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import org.objenesis.Objenesis;

public final class DefaultValueProviders {

    private DefaultValueProviders() {}

    public static ValueProvider create(FactoryCache factoryCache, Objenesis objenesis) {
        ChainedValueProvider mainChain = new ChainedValueProvider();
        ChainedValueProvider vintageChain = new ChainedValueProvider();

        ValueProvider vintage = new VintageValueProvider(vintageChain, factoryCache, objenesis);

        mainChain.register(vintage);

        return mainChain;
    }
}
