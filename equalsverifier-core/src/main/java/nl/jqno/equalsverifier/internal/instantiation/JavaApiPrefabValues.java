package nl.jqno.equalsverifier.internal.instantiation;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.*;
import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;
import java.util.regex.Pattern;

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
        addDateTimeClasses();
        addUncommonClasses();
        addCollection();
        addLists();
        addMaps();
        addSets();
        addQueues();
        SequencedCollectionsHelper.add(factoryCache);
        addExceptions();
        addReflectionClasses();
        addAtomicClasses();
        addAncientJavaApiClasses();
        addJavaxApiClasses();
    }

    // CHECKSTYLE OFF: ExecutableStatementCount
    @SuppressFBWarnings(
            value = "DMI_HARDCODED_ABSOLUTE_FILENAME",
            justification = "We just need an instance of File; they're not for actual use.")
    private void addCommonClasses() {
        addValues(File.class, new File(""), new File("/"), new File(""));
        addValues(Pattern.class, Pattern.compile("one"), Pattern.compile("two"), Pattern.compile("one"));
        addValues(StampedLock.class, new StampedLock(), new StampedLock(), new StampedLock());
        addValues(URI.class, URI.create("x"), URI.create("y"), URI.create("x"));
        addValues(PrintStream.class, System.out, System.err, System.out);

        rethrow(
            () -> addValues(
                URL.class,
                new URL("http://example.com"),
                new URL("http://localhost"),
                new URL("http://example.com")),
            e -> "Can't add prefab values for java.net.URL");

        addFactory(CompletableFuture.class, simple(ignored -> new CompletableFuture<>(), CompletableFuture::new));
        addFactory(Optional.class, simple(Optional::of, Optional::empty));
        addFactory(Supplier.class, simple(a -> () -> a, () -> () -> null));

        Semaphore redSemaphore = new Semaphore(1);
        Semaphore blueSemaphore = new Semaphore(1);
        addValues(Semaphore.class, redSemaphore, blueSemaphore, redSemaphore);
        ReentrantLock redReentrantLock = new ReentrantLock();
        ReentrantLock blueReentrantLock = new ReentrantLock();
        addValues(ReentrantLock.class, redReentrantLock, blueReentrantLock, redReentrantLock);
    }

    // CHECKSTYLE ON: ExecutableStatementCount

    private void addDateTimeClasses() {
        addValues(Clock.class, Clock.systemUTC(), Clock.system(ZoneId.of("-10")), Clock.systemUTC());
        addValues(
            DateFormat.class,
            DateFormat.getTimeInstance(),
            DateFormat.getDateInstance(),
            DateFormat.getTimeInstance());
        addValues(
            DateTimeFormatter.class,
            DateTimeFormatter.ISO_TIME,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_TIME);
        addValues(Duration.class, Duration.ZERO, Duration.ofDays(1L), Duration.ZERO);
        addValues(Instant.class, Instant.MIN, Instant.MAX, Instant.MIN);
        addValues(LocalDateTime.class, LocalDateTime.MIN, LocalDateTime.MAX, LocalDateTime.MIN);
        addValues(LocalDate.class, LocalDate.MIN, LocalDate.MAX, LocalDate.MIN);
        addValues(LocalTime.class, LocalTime.MIN, LocalTime.MAX, LocalTime.MIN);
        addValues(MonthDay.class, MonthDay.of(1, 1), MonthDay.of(12, 31), MonthDay.of(1, 1));
        addValues(OffsetDateTime.class, OffsetDateTime.MIN, OffsetDateTime.MAX, OffsetDateTime.MIN);
        addValues(OffsetTime.class, OffsetTime.MIN, OffsetTime.MAX, OffsetTime.MIN);
        addValues(Period.class, Period.ZERO, Period.of(1, 1, 1), Period.ZERO);
        addValues(DecimalFormat.class, new DecimalFormat("x0.0"), new DecimalFormat("y0.0"), new DecimalFormat("x0.0"));
        addValues(NumberFormat.class, new DecimalFormat("x0.0"), new DecimalFormat("y0.0"), new DecimalFormat("x0.0"));
        addValues(
            SimpleDateFormat.class,
            new SimpleDateFormat("yMd"),
            new SimpleDateFormat("dMy"),
            new SimpleDateFormat("yMd"));
        addValues(
            TimeZone.class,
            TimeZone.getTimeZone("GMT+1"),
            TimeZone.getTimeZone("GMT+2"),
            TimeZone.getTimeZone("GMT+1"));
        addValues(Year.class, Year.of(2000), Year.of(2010), Year.of(2000));
        addValues(YearMonth.class, YearMonth.of(2000, 1), YearMonth.of(2010, 12), YearMonth.of(2000, 1));
        addValues(ZoneId.class, ZoneId.of("+1"), ZoneId.of("-10"), ZoneId.of("+1"));
        addValues(ZoneOffset.class, ZoneOffset.ofHours(1), ZoneOffset.ofHours(-1), ZoneOffset.ofHours(1));
        addValues(
            ZonedDateTime.class,
            ZonedDateTime.parse("2017-12-13T10:15:30+01:00"),
            ZonedDateTime.parse("2016-11-12T09:14:29-01:00"),
            ZonedDateTime.parse("2017-12-13T10:15:30+01:00"));
    }

    private void addUncommonClasses() {
        addFactory(ThreadLocal.class, simple(a -> ThreadLocal.withInitial(() -> a), null));
        addValues(HexFormat.class, HexFormat.ofDelimiter(","), HexFormat.ofDelimiter("."), HexFormat.ofDelimiter(","));

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

        addValues(EventObject.class, new EventObject(1), new EventObject(2), new EventObject(1));
        addValues(
            InetSocketAddress.class,
            InetSocketAddress.createUnresolved("localhost", 8080),
            InetSocketAddress.createUnresolved("127.0.0.1", 8080),
            InetSocketAddress.createUnresolved("localhost", 8080));

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

    private void addExceptions() {
        Throwable redThrowable = new Throwable();
        Throwable blueThrowable = new Throwable();
        addValues(Throwable.class, redThrowable, blueThrowable, redThrowable);

        Exception redException = new Exception();
        Exception blueException = new Exception();
        addValues(Exception.class, redException, blueException, redException);

        RuntimeException redRuntimeException = new RuntimeException();
        RuntimeException blueRuntimeException = new RuntimeException();
        addValues(RuntimeException.class, redRuntimeException, blueRuntimeException, redRuntimeException);
    }

    @SuppressWarnings("unused")
    private static class JavaApiReflectionClassesContainer {

        @SuppressFBWarnings(value = "UUF_UNUSED_FIELD", justification = "These fields are accessed through reflection")
        Object a;

        @SuppressFBWarnings(value = "UUF_UNUSED_FIELD", justification = "These fields are accessed through reflection")
        Object b;

        public JavaApiReflectionClassesContainer() {}

        public JavaApiReflectionClassesContainer(Object o) {}

        void m1() {}

        void m2() {}
    }

    private void addReflectionClasses() {
        addValues(Class.class, Class.class, Object.class, Class.class);

        rethrow(() -> {
            Field f1 = JavaApiReflectionClassesContainer.class.getDeclaredField("a");
            Field f2 = JavaApiReflectionClassesContainer.class.getDeclaredField("b");
            addValues(Field.class, f1, f2, f1);
        }, e -> "Can't add prefab values for java.lang.reflect.Field");

        rethrow(() -> {
            Constructor<?> c1 = JavaApiReflectionClassesContainer.class.getDeclaredConstructor();
            Constructor<?> c2 = JavaApiReflectionClassesContainer.class.getDeclaredConstructor(Object.class);
            addValues(Constructor.class, c1, c2, c1);
        }, e -> "Can't add prefab values for java.lang.reflect.Constructor");

        rethrow(() -> {
            Method m1 = JavaApiReflectionClassesContainer.class.getDeclaredMethod("m1");
            Method m2 = JavaApiReflectionClassesContainer.class.getDeclaredMethod("m2");
            addValues(Method.class, m1, m2, m1);
        }, e -> "Can't add prefab values for java.lang.reflect.Method");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addAtomicClasses() {
        addValues(AtomicBoolean.class, new AtomicBoolean(true), new AtomicBoolean(false), new AtomicBoolean(true));
        addValues(AtomicInteger.class, new AtomicInteger(1), new AtomicInteger(2), new AtomicInteger(1));
        addValues(
            AtomicIntegerArray.class,
            new AtomicIntegerArray(new int[] { 1 }),
            new AtomicIntegerArray(new int[] { 2 }),
            new AtomicIntegerArray(new int[] { 1 }));
        addValues(AtomicLong.class, new AtomicLong(1L), new AtomicLong(2L), new AtomicLong(1L));
        addValues(
            AtomicLongArray.class,
            new AtomicLongArray(new long[] { 1L }),
            new AtomicLongArray(new long[] { 2L }),
            new AtomicLongArray(new long[] { 1L }));
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

        DoubleAdder redDoubleAdder = new DoubleAdder();
        DoubleAdder blueDoubleAdder = new DoubleAdder();
        addValues(DoubleAdder.class, redDoubleAdder, blueDoubleAdder, redDoubleAdder);
        DoubleAccumulator redDoubleAccumulator = new DoubleAccumulator((a, b) -> a + b, 0.0);
        DoubleAccumulator blueDoubleAccumulator = new DoubleAccumulator((a, b) -> a * b, 1.0);
        addValues(DoubleAccumulator.class, redDoubleAccumulator, blueDoubleAccumulator, redDoubleAccumulator);
        LongAdder redLongAdder = new LongAdder();
        LongAdder blueLongAdder = new LongAdder();
        addValues(LongAdder.class, redLongAdder, blueLongAdder, redLongAdder);
        LongAccumulator redLongAccumulator = new LongAccumulator((a, b) -> a + b, 0);
        LongAccumulator blueLongAccumulator = new LongAccumulator((a, b) -> a * b, 1);
        addValues(LongAccumulator.class, redLongAccumulator, blueLongAccumulator, redLongAccumulator);
    }

    private void addAncientJavaApiClasses() {
        addLazyFactory("java.awt.Color", AWT_FACTORY);
        addLazyFactory("java.awt.color.ColorSpace", AWT_FACTORY);
        addLazyFactory("java.awt.color.ICC_ColorSpace", AWT_FACTORY);
        addLazyFactory("java.awt.color.ICC_Profile", AWT_FACTORY);
        addLazyFactory("java.awt.Font", AWT_FACTORY);
        addLazyFactory("java.awt.Image", AWT_FACTORY);

        addFactory(
            PropertyChangeSupport.class,
            values(
                new PropertyChangeSupport("this"),
                new PropertyChangeSupport("that"),
                new PropertyChangeSupport("this")));

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
