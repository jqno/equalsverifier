package nl.jqno.equalsverifier.internal.prefabvalues.factories.external;

import com.google.common.collect.*;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.AbstractReflectiveGenericFactory;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.collection;
import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.map;

public final class GuavaFactory {
    private static final Comparator<Object> OBJECT_COMPARATOR = Comparator.comparingInt(Object::hashCode);

    private GuavaFactory() {
        // Don't instantiate
    }

    public static FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        putMultisets(cache);
        putMultimaps(cache);
        putBiMaps(cache);

        return cache;
    }

    private static void putMultisets(FactoryCache cache) {
        cache.put(Multiset.class, collection(HashMultiset::create));
        cache.put(SortedMultiset.class, collection(() -> TreeMultiset.create(OBJECT_COMPARATOR)));
        cache.put(HashMultiset.class, collection(HashMultiset::create));
        cache.put(TreeMultiset.class, collection(() -> TreeMultiset.create(OBJECT_COMPARATOR)));
        cache.put(LinkedHashMultiset.class, collection(LinkedHashMultiset::create));
        cache.put(ConcurrentHashMultiset.class, collection(ConcurrentHashMultiset::create));
    }

    private static void putMultimaps(FactoryCache cache) {
        cache.put(Multimap.class, multimap(ArrayListMultimap::create));
        cache.put(ListMultimap.class, multimap(ArrayListMultimap::create));
        cache.put(SetMultimap.class, multimap(HashMultimap::create));
        cache.put(SortedSetMultimap.class, multimap(() -> TreeMultimap.create(OBJECT_COMPARATOR, OBJECT_COMPARATOR)));
        cache.put(ArrayListMultimap.class, multimap(ArrayListMultimap::create));
        cache.put(HashMultimap.class, multimap(HashMultimap::create));
        cache.put(LinkedListMultimap.class, multimap(LinkedListMultimap::create));
        cache.put(LinkedHashMultimap.class, multimap(LinkedHashMultimap::create));
        cache.put(TreeMultimap.class, multimap(() -> TreeMultimap.create(OBJECT_COMPARATOR, OBJECT_COMPARATOR)));
    }

    private static void putBiMaps(FactoryCache cache) {
        cache.put(BiMap.class, map(HashBiMap::create));
        cache.put(HashBiMap.class, map(HashBiMap::create));
    }

    private static <K, V, T extends Multimap<K, V>> MultimapFactory<K, V, T> multimap(Supplier<T> factory) {
        return new MultimapFactory<>(factory);
    }

    private static final class MultimapFactory<K, V, T extends Multimap<K, V>> extends AbstractReflectiveGenericFactory<T> {
        private final Supplier<T> factory;

        private MultimapFactory(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override
        public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
            LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
            TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);
            TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);

            T red = factory.get();
            T black = factory.get();
            T redCopy = factory.get();
            red.put(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
            black.put(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));
            redCopy.put(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));

            return Tuple.of(red, black, redCopy);

        }
    }
}

