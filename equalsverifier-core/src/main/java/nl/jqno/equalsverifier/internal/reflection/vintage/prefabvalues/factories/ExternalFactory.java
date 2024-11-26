package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factoryproviders.FactoryProvider;

public class ExternalFactory<T> implements PrefabValueFactory<T> {

    private static final String EXTERNAL_FACTORIES_PACKAGE =
        "nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factoryproviders.";

    private final String factoryName;
    private FactoryCache factoryCache;

    public ExternalFactory(String factoryName) {
        this.factoryName = EXTERNAL_FACTORIES_PACKAGE + factoryName;
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        if (factoryCache == null) {
            ConditionalInstantiator ci = new ConditionalInstantiator(factoryName);
            FactoryProvider provider = ci.instantiate(classes(), objects());
            factoryCache = provider.getFactoryCache();
        }

        PrefabValueFactory<T> factory = factoryCache.get(tag.getType());
        return factory.createValues(tag, valueProvider, attributes);
    }
}
