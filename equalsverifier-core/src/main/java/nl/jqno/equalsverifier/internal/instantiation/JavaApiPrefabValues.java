package nl.jqno.equalsverifier.internal.instantiation;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.collection;
import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.map;
import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.simple;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.EnumMapFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.EnumSetFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinLazy;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;
import nl.jqno.equalsverifier.internal.versionspecific.ScopedValuesHelper;
import nl.jqno.equalsverifier.internal.versionspecific.SequencedCollectionsHelper;

/**
 * Creates instances of classes for use in a {@link VintageValueProvider} object.
 *
 * <p>
 * Contains hand-made instances of well-known Java API classes that cannot be instantiated dynamically because of an
 * internal infinite recursion of types, or other issues.
 */
@SuppressWarnings("JdkObsolete")
public final class JavaApiPrefabValues {

    private static final Comparator<Object> OBJECT_COMPARATOR = Comparator.comparingInt(Object::hashCode);

    private final FactoryCache factoryCache;

    /** Private constructor. Use {@link #build()}. */
    private JavaApiPrefabValues(FactoryCache factoryCache) {
        this.factoryCache = factoryCache;
    }

    /**
     * Creates a FactoryCache pre-populated with instances of Java API classes that cannot be instantiated dynamically.
     *
     * @return A pre-populated {@link FactoryCache}.
     */
    public static FactoryCache build() {
        FactoryCache result = new FactoryCache();
        new JavaApiPrefabValues(result).addJavaClasses();
        return result;
    }

    private void addJavaClasses() {
        addNonCollectionClasses();
        addCollection();
        addLists();
        addMaps();
        addSets();
        addQueues();
        SequencedCollectionsHelper.add(factoryCache);
        ScopedValuesHelper.add(factoryCache);
        addAtomicClasses();
        addKotlinClasses();
    }

    private void addNonCollectionClasses() {
        addFactory(CompletableFuture.class, simple(ignored -> new CompletableFuture<>(), CompletableFuture::new));
        addFactory(Optional.class, simple(Optional::of, Optional::empty));
        addFactory(Supplier.class, simple(a -> () -> a, () -> () -> null));
        addFactory(ThreadLocal.class, simple(a -> ThreadLocal.withInitial(() -> a), null));
    }

    @SuppressWarnings("unchecked")
    private void addCollection() {
        addFactory(Iterable.class, simple(a -> List.of(a), ArrayList::new));
        addFactory(Collection.class, collection(ArrayList::new));
    }

    @SuppressWarnings("unchecked")
    private void addLists() {
        addFactory(List.class, collection(ArrayList::new));
        addFactory(CopyOnWriteArrayList.class, collection(CopyOnWriteArrayList::new));
        addFactory(LinkedList.class, collection(LinkedList::new));
        addFactory(ArrayList.class, collection(ArrayList::new));
        addFactory(Vector.class, collection(Vector::new));
        addFactory(Stack.class, collection(Stack::new));
    }

    @SuppressWarnings("unchecked")
    private void addMaps() {
        addFactory(Map.class, map(HashMap::new));
        addFactory(SortedMap.class, map(() -> new TreeMap<>(OBJECT_COMPARATOR)));
        addFactory(NavigableMap.class, map(() -> new TreeMap<>(OBJECT_COMPARATOR)));
        addFactory(ConcurrentNavigableMap.class, map(() -> new ConcurrentSkipListMap<>(OBJECT_COMPARATOR)));
        addFactory(ConcurrentHashMap.class, map(ConcurrentHashMap::new));
        addFactory(HashMap.class, map(HashMap::new));
        addFactory(Hashtable.class, map(Hashtable::new));
        addFactory(LinkedHashMap.class, map(LinkedHashMap::new));
        addFactory(Properties.class, map(Properties::new));
        addFactory(TreeMap.class, map(() -> new TreeMap<>(OBJECT_COMPARATOR)));
        addFactory(WeakHashMap.class, map(WeakHashMap::new));
        addFactory(EnumMap.class, new EnumMapFactory<>(EnumMap::new));
    }

    @SuppressWarnings("unchecked")
    private void addSets() {
        addFactory(Set.class, collection(HashSet::new));
        addFactory(SortedSet.class, collection(() -> new TreeSet<>(OBJECT_COMPARATOR)));
        addFactory(NavigableSet.class, collection(() -> new TreeSet<>(OBJECT_COMPARATOR)));
        addFactory(CopyOnWriteArraySet.class, collection(CopyOnWriteArraySet::new));
        addFactory(HashSet.class, collection(HashSet::new));
        addFactory(TreeSet.class, collection(() -> new TreeSet<>(OBJECT_COMPARATOR)));
        addFactory(EnumSet.class, new EnumSetFactory<>(c -> EnumSet.copyOf(c)));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addQueues() {
        addFactory(Queue.class, collection(() -> new ArrayBlockingQueue<>(1)));
        addFactory(BlockingQueue.class, collection(() -> new ArrayBlockingQueue<>(1)));
        addFactory(Deque.class, collection(() -> new ArrayDeque<>(1)));
        addFactory(BlockingDeque.class, collection(() -> new LinkedBlockingDeque<>(1)));
        addFactory(ArrayBlockingQueue.class, collection(() -> new ArrayBlockingQueue<>(1)));
        addFactory(ConcurrentLinkedQueue.class, collection(ConcurrentLinkedQueue::new));
        addFactory(DelayQueue.class, collection(DelayQueue::new));
        addFactory(LinkedBlockingQueue.class, collection(() -> new LinkedBlockingQueue(1)));
        addFactory(PriorityBlockingQueue.class, collection(() -> new PriorityBlockingQueue<>(1, OBJECT_COMPARATOR)));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addAtomicClasses() {
        addFactory(AtomicMarkableReference.class, simple(r -> new AtomicMarkableReference(r, true), null));
        addFactory(AtomicReference.class, simple(AtomicReference::new, null));
        addFactory(AtomicStampedReference.class, simple(r -> new AtomicStampedReference(r, 0), null));
        addFactory(AtomicReferenceArray.class, (tag, pv, stack) -> {
            TypeTag genericTag = tag.genericTypes().get(0);
            Tuple<?> tup = pv.provideOrThrow(genericTag, Attributes.empty());
            return tup.map(v -> new AtomicReferenceArray<>(new Object[] { v }));
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addKotlinClasses() {
        if (KotlinScreen.LAZY != null) {
            addFactory(KotlinScreen.LAZY, (tag, pv, stack) -> {
                TypeTag genericTag = tag.genericTypes().get(0);
                Tuple tup = pv.provideOrThrow(genericTag, Attributes.empty());
                return tup.map(v -> KotlinLazy.lazy(v));
            });
        }
    }

    private <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        factoryCache.put(type, factory);
    }
}
