package nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.*;

import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;
import java.util.*;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.AbstractGenericFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.EnumMapFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.EnumSetFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories;

public final class GuavaFactoryProvider implements FactoryProvider {
    private static final Comparator<Object> OBJECT_COMPARATOR =
            Comparator.comparingInt(Object::hashCode);

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        putMultisets(cache);
        putMultimaps(cache);
        putBiMaps(cache);
        putTables(cache);
        putRegularCollections(cache);
        putImmutableClasses(cache);
        putNewTypes(cache);

        return cache;
    }

    @SuppressWarnings("unchecked")
    private void putMultisets(FactoryCache cache) {
        cache.put(Multiset.class, collection(HashMultiset::create));
        cache.put(SortedMultiset.class, collection(() -> TreeMultiset.create(OBJECT_COMPARATOR)));
        cache.put(HashMultiset.class, collection(HashMultiset::create));
        cache.put(TreeMultiset.class, collection(() -> TreeMultiset.create(OBJECT_COMPARATOR)));
        cache.put(LinkedHashMultiset.class, collection(LinkedHashMultiset::create));
        cache.put(ConcurrentHashMultiset.class, collection(ConcurrentHashMultiset::create));
        cache.put(EnumMultiset.class, new EnumSetFactory<>(EnumMultiset::create));
        cache.put(ImmutableMultiset.class, copy(Set.class, ImmutableMultiset::copyOf));
        cache.put(
                ImmutableSortedMultiset.class,
                copy(SortedMultiset.class, ImmutableSortedMultiset::copyOfSorted));
    }

    @SuppressWarnings("unchecked")
    private void putMultimaps(FactoryCache cache) {
        cache.put(Multimap.class, multimap(ArrayListMultimap::create));
        cache.put(ListMultimap.class, multimap(ArrayListMultimap::create));
        cache.put(SetMultimap.class, multimap(HashMultimap::create));
        cache.put(
                SortedSetMultimap.class,
                multimap(() -> TreeMultimap.create(OBJECT_COMPARATOR, OBJECT_COMPARATOR)));
        cache.put(ArrayListMultimap.class, multimap(ArrayListMultimap::create));
        cache.put(HashMultimap.class, multimap(HashMultimap::create));
        cache.put(LinkedListMultimap.class, multimap(LinkedListMultimap::create));
        cache.put(LinkedHashMultimap.class, multimap(LinkedHashMultimap::create));
        cache.put(
                TreeMultimap.class,
                multimap(() -> TreeMultimap.create(OBJECT_COMPARATOR, OBJECT_COMPARATOR)));
        cache.put(ImmutableListMultimap.class, copy(Multimap.class, ImmutableListMultimap::copyOf));
        cache.put(ImmutableSetMultimap.class, copy(Multimap.class, ImmutableSetMultimap::copyOf));
        cache.put(ImmutableMultimap.class, copy(Multimap.class, ImmutableListMultimap::copyOf));
    }

    @SuppressWarnings("unchecked")
    private void putBiMaps(FactoryCache cache) {
        cache.put(BiMap.class, map(HashBiMap::create));
        cache.put(HashBiMap.class, map(HashBiMap::create));
        cache.put(EnumHashBiMap.class, copy(EnumMap.class, EnumHashBiMap::create));
        cache.put(ImmutableBiMap.class, copy(Map.class, ImmutableBiMap::copyOf));
        cache.put(EnumBiMap.class, new EnumMapFactory<>(EnumBiMap::create));
    }

    @SuppressWarnings("unchecked")
    private void putTables(FactoryCache cache) {
        cache.put(Table.class, table(HashBasedTable::create));
        cache.put(HashBasedTable.class, table(HashBasedTable::create));
        cache.put(
                TreeBasedTable.class,
                table(() -> TreeBasedTable.create(OBJECT_COMPARATOR, OBJECT_COMPARATOR)));
        cache.put(ArrayTable.class, copy(Table.class, ArrayTable::create));
        cache.put(ImmutableTable.class, copy(Table.class, ImmutableTable::copyOf));
    }

    @SuppressWarnings("unchecked")
    private void putRegularCollections(FactoryCache cache) {
        cache.put(EvictingQueue.class, collection(() -> EvictingQueue.create(10)));
        cache.put(
                MinMaxPriorityQueue.class,
                collection(() -> MinMaxPriorityQueue.orderedBy(OBJECT_COMPARATOR).create()));
        cache.put(ImmutableRangeSet.class, copy(Range.class, ImmutableRangeSet::of));
        cache.put(TreeRangeSet.class, copy(ImmutableRangeSet.class, TreeRangeSet::create));
        cache.put(RangeSet.class, copy(ImmutableRangeSet.class, TreeRangeSet::create));
    }

    @SuppressWarnings("unchecked")
    private void putImmutableClasses(FactoryCache cache) {
        cache.put(ImmutableCollection.class, copy(Collection.class, ImmutableList::copyOf));
        cache.put(ImmutableList.class, copy(Collection.class, ImmutableList::copyOf));
        cache.put(ImmutableMap.class, copy(Map.class, ImmutableMap::copyOf));
        cache.put(ImmutableSet.class, copy(Collection.class, ImmutableSet::copyOf));
        cache.put(ImmutableSortedMap.class, copy(Map.class, ImmutableSortedMap::copyOf));
        cache.put(ImmutableSortedSet.class, copy(Set.class, ImmutableSortedSet::copyOf));
    }

    @SuppressWarnings("unchecked")
    private void putNewTypes(FactoryCache cache) {
        cache.put(
                Range.class, Factories.<Comparable<?>, Range<?>>simple(Range::atLeast, Range::all));
        cache.put(
                com.google.common.base.Optional.class,
                simple(
                        com.google.common.base.Optional::of,
                        com.google.common.base.Optional::absent));
        cache.put(
                TypeToken.class,
                values(
                        TypeToken.of(Object.class),
                        TypeToken.of(String.class),
                        TypeToken.of(Object.class)));
    }

    private <K, V, T extends Multimap<K, V>> MultimapFactory<K, V, T> multimap(
            Supplier<T> factory) {
        return new MultimapFactory<>(factory);
    }

    private <C, R, V, T extends Table<C, R, V>> TableFactory<C, R, V, T> table(
            Supplier<T> factory) {
        return new TableFactory<>(factory);
    }

    private static final class MultimapFactory<K, V, T extends Multimap<K, V>>
            extends AbstractGenericFactory<T> {
        private final Supplier<T> factory;

        private MultimapFactory(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override
        public Tuple<T> createValues(
                TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
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

    private static final class TableFactory<C, R, V, T extends Table<C, R, V>>
            extends AbstractGenericFactory<T> {
        private final Supplier<T> factory;

        private TableFactory(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override
        public Tuple<T> createValues(
                TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
            LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
            TypeTag columnTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);
            TypeTag rowTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);
            TypeTag valueTag = determineAndCacheActualTypeTag(2, tag, prefabValues, clone);

            T red = factory.get();
            T black = factory.get();
            T redCopy = factory.get();
            red.put(
                    prefabValues.giveRed(columnTag),
                    prefabValues.giveRed(rowTag),
                    prefabValues.giveBlack(valueTag));
            black.put(
                    prefabValues.giveBlack(columnTag),
                    prefabValues.giveBlack(rowTag),
                    prefabValues.giveBlack(valueTag));
            redCopy.put(
                    prefabValues.giveRed(columnTag),
                    prefabValues.giveRed(rowTag),
                    prefabValues.giveBlack(valueTag));

            return Tuple.of(red, black, redCopy);
        }
    }
}
