package nl.jqno.equalsverifier.internal.versionspecific;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.collection;
import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.map;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;

public final class SequencedCollectionsHelper {

    private SequencedCollectionsHelper() {}

    public static void add(FactoryCache factoryCache) {
        factoryCache.put(SequencedCollection.class, collection(ArrayList::new));
        factoryCache.put(SequencedSet.class, collection(LinkedHashSet::new));
        factoryCache.put(SequencedMap.class, map(LinkedHashMap::new));
    }
}
