package nl.jqno.equalsverifier.testhelpers;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;

public final class PrefabValuesFactory {
    private PrefabValuesFactory() {}

    public static PrefabValues withPrimitiveFactories() {
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(boolean.class, values(true, false, true));
        factoryCache.put(byte.class, values((byte)1, (byte)2, (byte)1));
        factoryCache.put(char.class, values('a', 'b', 'a'));
        factoryCache.put(double.class, values(0.5D, 1.0D, 0.5D));
        factoryCache.put(float.class, values(0.5F, 1.0F, 0.5F));
        factoryCache.put(int.class, values(1, 2, 1));
        factoryCache.put(long.class, values(1L, 2L, 1L));
        factoryCache.put(short.class, values((short)1, (short)2, (short)1));
        return new PrefabValues(factoryCache);
    }
}
