package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders.FactoryProvider;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

public class ExternalFactory<T> implements PrefabValueFactory<T> {
    private static final String EXTERNAL_FACTORIES_PACKAGE = "nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders.";

    private final String factoryName;
    private FactoryCache factoryCache;

    public ExternalFactory(String factoryName) {
        this.factoryName = EXTERNAL_FACTORIES_PACKAGE + factoryName;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        if (factoryCache == null) {
            ConditionalInstantiator ci = new ConditionalInstantiator(factoryName);
            FactoryProvider provider = (FactoryProvider)ci.instantiate(classes(), objects());
            factoryCache = provider.getFactoryCache();
        }

        PrefabValueFactory<T> factory = factoryCache.get(tag.getType());
        return factory.createValues(tag, prefabValues, typeStack);
    }
}
