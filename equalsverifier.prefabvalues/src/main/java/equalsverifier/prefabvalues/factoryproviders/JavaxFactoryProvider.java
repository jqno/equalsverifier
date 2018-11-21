package equalsverifier.prefabvalues.factoryproviders;

import equalsverifier.prefabvalues.FactoryCache;

import javax.naming.Reference;

import static equalsverifier.prefabvalues.factories.Factories.values;

public final class JavaxFactoryProvider implements FactoryProvider {

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        cache.put(Reference.class, values(new Reference("one"), new Reference("two"), new Reference("one")));

        return cache;
    }
}
