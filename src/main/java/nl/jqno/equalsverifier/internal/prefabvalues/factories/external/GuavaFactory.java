package nl.jqno.equalsverifier.internal.prefabvalues.factories.external;

import com.google.common.collect.*;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;

import java.util.Comparator;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.collection;

public final class GuavaFactory {
    private static final Comparator<Object> OBJECT_COMPARATOR = Comparator.comparingInt(Object::hashCode);

    private GuavaFactory() {
        // Don't instantiate
    }

    public static FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        cache.put(Multiset.class, collection(HashMultiset::create));
        cache.put(SortedMultiset.class, collection(() -> TreeMultiset.create(OBJECT_COMPARATOR)));
        cache.put(HashMultiset.class, collection(HashMultiset::create));
        cache.put(TreeMultiset.class, collection(() -> TreeMultiset.create(OBJECT_COMPARATOR)));
        cache.put(LinkedHashMultiset.class, collection(LinkedHashMultiset::create));
        cache.put(ConcurrentHashMultiset.class, collection(ConcurrentHashMultiset::create));

        return cache;
    }
}
