package nl.jqno.equalsverifier.internal.versionspecific;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;

import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;

public final class ScopedValuesHelper {

    private ScopedValuesHelper() {}

    public static void add(FactoryCache factoryCache) {
        var red = ScopedValue.newInstance();
        factoryCache.put(ScopedValue.class, values(red, ScopedValue.newInstance(), red));
    }
}
