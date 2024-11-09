package nl.jqno.equalsverifier.internal.reflection.instantiation;

import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.instantiation.GenericPrefabValueProvider.GenericFactories;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import org.objenesis.Objenesis;

/**
 * Factory methods for {@link ValueProvider}s.
 */
public final class DefaultValueProviders {

    private DefaultValueProviders() {}

    public static ValueProvider withVintage(
        PrefabValueProvider prefabValueProvider,
        GenericFactories genericFactories,
        Objenesis objenesis
    ) {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        ChainedValueProvider mainChain = new ChainedValueProvider(prefabValueProvider);
        ChainedValueProvider vintageChain = new ChainedValueProvider(prefabValueProvider);

        ValueProvider genericPrefab = new GenericPrefabValueProvider(genericFactories, mainChain);
        ValueProvider builtin = new BuiltinValueProvider(mainChain);
        ValueProvider array = new ArrayValueProvider(mainChain);
        ValueProvider enums = new EnumValueProvider();
        ValueProvider vintage = new VintageValueProvider(vintageChain, factoryCache, objenesis);

        mainChain.register(genericPrefab, builtin, array, enums, vintage);
        vintageChain.register(genericPrefab, builtin, array, enums);

        return mainChain;
    }

    public static ValueProvider withoutVintage(
        PrefabValueProvider prefabValueProvider,
        GenericFactories genericFactories
    ) {
        ChainedValueProvider mainChain = new ChainedValueProvider(prefabValueProvider);

        ValueProvider genericPrefab = new GenericPrefabValueProvider(genericFactories, mainChain);
        ValueProvider enums = new EnumValueProvider();

        mainChain.register(genericPrefab, enums);

        return mainChain;
    }
}
