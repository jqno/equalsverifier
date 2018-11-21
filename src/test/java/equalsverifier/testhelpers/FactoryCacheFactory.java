package equalsverifier.testhelpers;

import equalsverifier.prefabvalues.FactoryCache;

import static equalsverifier.prefabvalues.factories.Factories.values;

public final class FactoryCacheFactory {
    private FactoryCacheFactory() {}

    public static FactoryCache withPrimitiveFactories() {
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(boolean.class, values(true, false, true));
        factoryCache.put(byte.class, values((byte)1, (byte)2, (byte)1));
        factoryCache.put(char.class, values('a', 'b', 'a'));
        factoryCache.put(double.class, values(0.5D, 1.0D, 0.5D));
        factoryCache.put(float.class, values(0.5F, 1.0F, 0.5F));
        factoryCache.put(int.class, values(1, 2, 1));
        factoryCache.put(long.class, values(1L, 2L, 1L));
        factoryCache.put(short.class, values((short)1, (short)2, (short)1));
        return factoryCache;
    }
}
