package nl.jqno.equalsverifier.internal.instantiation;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.*;
import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.*;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.versionspecific.SequencedCollectionsHelper;

/**
 * Creates instances of classes for use in a {@link VintageValueProvider} object.
 *
 * <p>
 * Contains hand-made instances of well-known Java API classes that cannot be instantiated dynamically because of an
 * internal infinite recursion of types, or other issues.
 */
@SuppressFBWarnings(
        value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
        justification = "That would be dozens of separate classes")
public final class JavaApiPrefabValues {

    private static final ExternalFactory<?> AWT_FACTORY = new ExternalFactory<>("AwtFactoryProvider");
    private static final ExternalFactory<?> JAVAX_FACTORY = new ExternalFactory<>("JavaxFactoryProvider");
    private static final ExternalFactory<?> RMI_FACTORY = new ExternalFactory<>("RmiFactoryProvider");

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
        addCommonClasses();
        addUncommonClasses();
        addCollection();
        addLists();
        addMaps();
        addSets();
        addQueues();
        SequencedCollectionsHelper.add(factoryCache);
        addAtomicClasses();
        addAncientJavaApiClasses();
        addJavaxApiClasses();
    }

    // CHECKSTYLE OFF: ExecutableStatementCount
    private void addCommonClasses() {
        addFactory(CompletableFuture.class, simple(ignored -> new CompletableFuture<>(), CompletableFuture::new));
        addFactory(Optional.class, simple(Optional::of, Optional::empty));
        addFactory(Supplier.class, simple(a -> () -> a, () -> () -> null));
    }

    private void addUncommonClasses() {
        addFactory(ThreadLocal.class, simple(a -> ThreadLocal.withInitial(() -> a), null));

        // Constructing java.sql.* classes reflectively, because they reside in a different module
        // which causes trouble when running EqualsVerifier on the modulepath.
        ConditionalInstantiator sqlDate = new ConditionalInstantiator("java.sql.Date");
        ConditionalInstantiator sqlTime = new ConditionalInstantiator("java.sql.Time");
        ConditionalInstantiator sqlTimestamp = new ConditionalInstantiator("java.sql.Timestamp");
        addValues(
            sqlDate.resolve(),
            sqlDate.instantiate(classes(long.class), objects(1337)),
            sqlDate.instantiate(classes(long.class), objects(42)),
            sqlDate.instantiate(classes(long.class), objects(1337)));
        addValues(
            sqlTime.resolve(),
            sqlTime.instantiate(classes(long.class), objects(1337)),
            sqlTime.instantiate(classes(long.class), objects(42)),
            sqlTime.instantiate(classes(long.class), objects(1337)));
        addValues(
            sqlTimestamp.resolve(),
            sqlTimestamp.instantiate(classes(long.class), objects(1337)),
            sqlTimestamp.instantiate(classes(long.class), objects(42)),
            sqlTimestamp.instantiate(classes(long.class), objects(1337)));

        // Constructing InetAddress reflectively, because it might throw an awkward exception
        // otherwise.
        ConditionalInstantiator inetAddress = new ConditionalInstantiator("java.net.InetAddress");
        ConditionalInstantiator inet4Address = new ConditionalInstantiator("java.net.Inet4Address");
        ConditionalInstantiator inet6Address = new ConditionalInstantiator("java.net.Inet6Address");
        addValues(
            inetAddress.resolve(),
            inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")),
            inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.42")),
            inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")));
        addValues(
            inet4Address.resolve(),
            inet4Address.callFactory("getByName", classes(String.class), objects("127.0.0.1")),
            inet4Address.callFactory("getByName", classes(String.class), objects("127.0.0.42")),
            inet4Address.callFactory("getByName", classes(String.class), objects("127.0.0.1")));
        addValues(
            inet6Address.resolve(),
            inet6Address.callFactory("getByName", classes(String.class), objects("::1")),
            inet6Address.callFactory("getByName", classes(String.class), objects("::")),
            inet6Address.callFactory("getByName", classes(String.class), objects("::1")));
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
        addValues(
            BitSet.class,
            BitSet.valueOf(new byte[] { 0 }),
            BitSet.valueOf(new byte[] { 1 }),
            BitSet.valueOf(new byte[] { 0 }));
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
        addValues(SynchronousQueue.class, new SynchronousQueue<>(), new SynchronousQueue<>(), new SynchronousQueue<>());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addAtomicClasses() {
        addFactory(AtomicMarkableReference.class, simple(r -> new AtomicMarkableReference(r, true), null));
        addFactory(AtomicReference.class, simple(AtomicReference::new, null));
        addFactory(AtomicStampedReference.class, simple(r -> new AtomicStampedReference(r, 0), null));
        addFactory(AtomicReferenceArray.class, (tag, pv, stack) -> {
            TypeTag y = tag.genericTypes().get(0);
            Object[] red = new Object[] { pv.giveRed(y) };
            Object[] blue = new Object[] { pv.giveBlue(y) };
            Object[] redCopy = new Object[] { pv.giveRedCopy(y) };
            return new Tuple(new AtomicReferenceArray(red),
                    new AtomicReferenceArray(blue),
                    new AtomicReferenceArray(redCopy));
        });
    }

    private void addAncientJavaApiClasses() {
        addLazyFactory("java.awt.Color", AWT_FACTORY);
        addLazyFactory("java.awt.color.ColorSpace", AWT_FACTORY);
        addLazyFactory("java.awt.color.ICC_ColorSpace", AWT_FACTORY);
        addLazyFactory("java.awt.color.ICC_Profile", AWT_FACTORY);
        addLazyFactory("java.awt.Font", AWT_FACTORY);
        addLazyFactory("java.awt.Image", AWT_FACTORY);

        addLazyFactory("java.rmi.dgc.VMID", RMI_FACTORY);
        addLazyFactory("java.rmi.server.UID", RMI_FACTORY);
    }

    private void addJavaxApiClasses() {
        addLazyFactory("javax.naming.Reference", JAVAX_FACTORY);
        addLazyFactory("javax.swing.tree.DefaultMutableTreeNode", JAVAX_FACTORY);
    }

    private <T> void addValues(Class<T> type, T red, T blue, T redCopy) {
        factoryCache.put(type, values(red, blue, redCopy));
    }

    private <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        factoryCache.put(type, factory);
    }

    private <T> void addLazyFactory(String typeName, PrefabValueFactory<T> factory) {
        factoryCache.put(typeName, factory);
    }
}
