package equalsverifier.prefabvalues.factories;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabvalues.FactoryCache;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.prefabvalues.factoryproviders.FactoryProvider;
import equalsverifier.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static equalsverifier.reflection.Util.classes;
import static equalsverifier.reflection.Util.objects;

public class ExternalFactory<T> implements PrefabValueFactory<T> {
    private static final String EXTERNAL_FACTORIES_PACKAGE = "equalsverifier.prefabvalues.factoryproviders.";

    private final String factoryName;
    private FactoryCache factoryCache;

    public ExternalFactory(String factoryName) {
        this.factoryName = EXTERNAL_FACTORIES_PACKAGE + factoryName;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        if (factoryCache == null) {
            ConditionalInstantiator ci = new ConditionalInstantiator(factoryName);
            FactoryProvider provider = ci.instantiate(classes(), objects());
            factoryCache = provider.getFactoryCache();
        }

        PrefabValueFactory<T> factory = factoryCache.get(tag.getType());
        return factory.createValues(tag, prefabAbstract, typeStack);
    }
}
