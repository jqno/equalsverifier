package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

public class ExternalFactory<T> implements PrefabValueFactory<T> {
    private static final String EXTERNAL_FACTORIES_PACKAGE = "nl.jqno.equalsverifier.internal.prefabvalues.factories.external.";

    private final String factoryName;
    private FactoryCache factoryCache;

    public ExternalFactory(String factoryName) {
        this.factoryName = EXTERNAL_FACTORIES_PACKAGE + factoryName;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        if (factoryCache == null) {
            ConditionalInstantiator ci = new ConditionalInstantiator(factoryName);
            factoryCache = (FactoryCache)ci.callFactory("getFactoryCache", classes(), objects());
        }

        PrefabValueFactory<T> factory = factoryCache.get(tag.getType());
        return factory.createValues(tag, prefabValues, typeStack);
    }
}
