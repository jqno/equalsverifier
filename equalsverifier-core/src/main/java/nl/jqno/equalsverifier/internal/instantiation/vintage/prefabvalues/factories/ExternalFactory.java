package nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factoryproviders.FactoryProvider;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class ExternalFactory<T> implements PrefabValueFactory<T> {

    private static final String EXTERNAL_FACTORIES_PACKAGE =
        "nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factoryproviders.";

    private final String factoryName;
    private FactoryCache factoryCache;

    public ExternalFactory(String factoryName) {
        this.factoryName = EXTERNAL_FACTORIES_PACKAGE + factoryName;
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        LinkedHashSet<TypeTag> typeStack
    ) {
        if (factoryCache == null) {
            ConditionalInstantiator ci = new ConditionalInstantiator(factoryName);
            FactoryProvider provider = ci.instantiate(classes(), objects());
            factoryCache = provider.getFactoryCache();
        }

        PrefabValueFactory<T> factory = factoryCache.get(tag.getType());
        return factory.createValues(tag, valueProvider, typeStack);
    }
}
