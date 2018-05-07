package nl.jqno.equalsverifier.internal.prefabvalues;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.*;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;
import java.util.regex.Pattern;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.*;
import static nl.jqno.equalsverifier.internal.reflection.Util.*;

/**
 * Creates instances of classes for use in a {@link PrefabValues} object.
 *
 * Contains hand-made instances of well-known Java API classes that cannot be
 * instantiated dynamically because of an internal infinite recursion of types,
 * or other issues.
 */
@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "That would be dozens of separate classes")
public final class JavaApiPrefabValues {
    private static final String JAVAFX_COLLECTIONS_PACKAGE = "javafx.collections.";
    private static final String JAVAFX_PROPERTY_PACKAGE = "javafx.beans.property.";
    private static final String GUAVA_PACKAGE = "com.google.common.collect.";
    private static final String JODA_PACKAGE = "org.joda.time.";
    private static final ExternalFactory<?> AWT_FACTORY = new ExternalFactory<>("AwtFactory");
    private static final ExternalFactory<?> JAVAX_FACTORY = new ExternalFactory<>("JavaxFactory");
    private static final ExternalFactory<?> GUAVA_FACTORY = new ExternalFactory<>("GuavaFactory");
    private static final ExternalFactory<?> JODA_FACTORY = new ExternalFactory<>("JodaFactory");

    private static final Comparator<Object> OBJECT_COMPARATOR = Comparator.comparingInt(Object::hashCode);

    private PrefabValues prefabValues;

    private enum Dummy { RED, BLACK }

    /**
     * Private constructor. Use {@link #addTo(PrefabValues)}.
     */
    private JavaApiPrefabValues(PrefabValues prefabValues) {
        this.prefabValues = prefabValues;
    }

    /**
     * Adds instances of Java API classes that cannot be instantiated
     * dynamically to {@code prefabValues}.
     *
     * @param prefabValues The instance of prefabValues that should
     *          contain the Java API instances.
     */
    public static void addTo(PrefabValues prefabValues) {
        new JavaApiPrefabValues(prefabValues).addJavaClasses();
    }

    private void addJavaClasses() {
        addPrimitiveClasses();
        addClasses();
        addCollection();
        addLists();
        addMaps();
        addSets();
        addQueues();
        addNioBuffers();
        addAwtClasses();
        addJava8ApiClasses();
        addJavaFxClasses();
        addJavaxApiClasses();
        addGoogleGuavaMultisetCollectionsClasses();
        addGoogleGuavaMultimapCollectionsClasses();
        addGoogleGuavaBiMapCollectionsClasses();
        addGoogleGuavaTableCollectionClasses();
        addGoogleGuavaRegularCollectionsClasses();
        addGoogleGuavaImmutableClasses();
        addNewGoogleGuavaClasses();
        addJodaTimeClasses();
    }

    @SuppressFBWarnings(
        value = {"DM_BOOLEAN_CTOR", "DM_NUMBER_CTOR", "DM_FP_NUMBER_CTOR", "DM_STRING_CTOR"},
        justification = "We really do need a separate instances with the same value")
    private void addPrimitiveClasses() {
        addValues(boolean.class, true, false, true);
        addValues(byte.class, (byte)1, (byte)2, (byte)1);
        addValues(char.class, 'a', 'b', 'a');
        addValues(double.class, 0.5D, 1.0D, 0.5D);
        addValues(float.class, 0.5F, 1.0F, 0.5F);
        addValues(int.class, 1, 2, 1);
        addValues(long.class, 1L, 2L, 1L);
        addValues(short.class, (short)1, (short)2, (short)1);

        addValues(Boolean.class, true, false, new Boolean(true));
        addValues(Byte.class, (byte)1, (byte)2, new Byte((byte)1));
        addValues(Character.class, 'a', 'b', new Character('a'));
        addValues(Double.class, 0.5D, 1.0D, new Double(0.5D));
        addValues(Float.class, 0.5F, 1.0F, new Float(0.5F));
        addValues(Integer.class, 1, 2, new Integer(1));
        addValues(Long.class, 1L, 2L, new Long(1L));
        addValues(Short.class, (short)1, (short)2, new Short((short)1));

        addValues(Object.class, new Object(), new Object(), new Object());
        addValues(Class.class, Class.class, Object.class, Class.class);
        addValues(String.class, "one", "two", new String("one"));
        addValues(Enum.class, Dummy.RED, Dummy.BLACK, Dummy.RED);
    }

    @SuppressFBWarnings(
        value = {"DMI_HARDCODED_ABSOLUTE_FILENAME", "DM_USELESS_THREAD"},
        justification = "We just need an instance of File and Thread; they're not for actual use.")
    private void addClasses() {
        addValues(BigDecimal.class, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO);
        addValues(BigInteger.class, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO);
        addValues(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5), new GregorianCalendar(2010, 7, 4));
        addValues(Date.class, new Date(0), new Date(1), new Date(0));
        addValues(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance(), DateFormat.getTimeInstance());
        addValues(File.class, new File(""), new File("/"), new File(""));
        addValues(Formatter.class, new Formatter(), new Formatter(), new Formatter());
        addValues(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5), new GregorianCalendar(2010, 7, 4));
        addValues(Locale.class, new Locale("nl"), new Locale("hu"), new Locale("nl"));
        addValues(Pattern.class, Pattern.compile("one"), Pattern.compile("two"), Pattern.compile("one"));
        addValues(SimpleDateFormat.class, new SimpleDateFormat("yMd"), new SimpleDateFormat("dMy"), new SimpleDateFormat("yMd"));
        addValues(Scanner.class, new Scanner("one"), new Scanner("two"), new Scanner("one"));
        addValues(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"), TimeZone.getTimeZone("GMT+1"));
        addValues(Thread.class, new Thread("one"), new Thread("two"), new Thread("one"));
        addValues(Throwable.class, new Throwable(), new Throwable(), new Throwable());
        addValues(UUID.class, new UUID(0, -1), new UUID(1, 0), new UUID(0, -1));
        addValues(java.sql.Date.class, new java.sql.Date(1337), new java.sql.Date(42), new java.sql.Date(1337));
        addValues(java.sql.Time.class, new java.sql.Time(1337), new java.sql.Time(42), new java.sql.Time(1337));
        addValues(java.sql.Timestamp.class, new java.sql.Timestamp(1337), new java.sql.Timestamp(42), new java.sql.Timestamp(1337));

        addFactory(ThreadLocal.class, arity1(a -> ThreadLocal.withInitial(() -> a), null));

        // Constructing InetAddress reflectively, because it might throw an awkward exception otherwise.
        ConditionalInstantiator inetAddress = new ConditionalInstantiator("java.net.InetAddress");
        ConditionalInstantiator inet4Address = new ConditionalInstantiator("java.net.Inet4Address");
        ConditionalInstantiator inet6Address = new ConditionalInstantiator("java.net.Inet6Address");
        addValues(inetAddress.resolve(),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.42")),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")));
        addValues(inet4Address.resolve(),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.42")),
                inetAddress.callFactory("getByName", classes(String.class), objects("127.0.0.1")));
        addValues(inet6Address.resolve(),
                inetAddress.callFactory("getByName", classes(String.class), objects("::1")),
                inetAddress.callFactory("getByName", classes(String.class), objects("::")),
                inetAddress.callFactory("getByName", classes(String.class), objects("::1")));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addCollection() {
        addFactory(Iterable.class, arity1(a -> {
            Collection coll = new ArrayList<>();
            coll.add(a);
            return coll;
        }, ArrayList::new));
        addFactory(Collection.class, collection(ArrayList::new));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addLists() {
        addFactory(List.class, collection(ArrayList::new));
        addFactory(CopyOnWriteArrayList.class, collection(CopyOnWriteArrayList::new));
        addFactory(LinkedList.class, collection(LinkedList::new));
        addFactory(ArrayList.class, collection(ArrayList::new));
        addFactory(Vector.class, collection(Vector::new));
        addFactory(Stack.class, collection(Stack::new));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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
        addFactory(EnumMap.class, new ReflectiveEnumMapFactory());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addSets() {
        addFactory(Set.class, collection(HashSet::new));
        addFactory(SortedSet.class, collection(() -> new TreeSet<>(OBJECT_COMPARATOR)));
        addFactory(NavigableSet.class, collection(() -> new TreeSet<>(OBJECT_COMPARATOR)));
        addFactory(CopyOnWriteArraySet.class, collection(CopyOnWriteArraySet::new));
        addFactory(HashSet.class, collection(HashSet::new));
        addFactory(TreeSet.class, collection(() -> new TreeSet<>(OBJECT_COMPARATOR)));
        addFactory(EnumSet.class, new ReflectiveEnumSetFactory());
        addValues(BitSet.class, BitSet.valueOf(new byte[]{0}), BitSet.valueOf(new byte[]{1}), BitSet.valueOf(new byte[]{0}));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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

    private void addNioBuffers() {
        addValues(Buffer.class,
                ByteBuffer.wrap(new byte[] { 0 }), ByteBuffer.wrap(new byte[] { 1 }), ByteBuffer.wrap(new byte[] { 0 }));
        addValues(ByteBuffer.class,
                ByteBuffer.wrap(new byte[] { 0 }), ByteBuffer.wrap(new byte[] { 1 }), ByteBuffer.wrap(new byte[] { 0 }));
        addValues(CharBuffer.class,
                CharBuffer.wrap("a"), CharBuffer.wrap("b"), CharBuffer.wrap("a"));
        addValues(DoubleBuffer.class,
                DoubleBuffer.wrap(new double[] { 0.0 }), DoubleBuffer.wrap(new double[] { 1.0 }), DoubleBuffer.wrap(new double[] { 0.0 }));
        addValues(FloatBuffer.class,
                FloatBuffer.wrap(new float[] { 0.0f }), FloatBuffer.wrap(new float[] { 1.0f }), FloatBuffer.wrap(new float[] { 0.0f }));
        addValues(IntBuffer.class,
                IntBuffer.wrap(new int[] { 0 }), IntBuffer.wrap(new int[] { 1 }), IntBuffer.wrap(new int[] { 0 }));
        addValues(LongBuffer.class,
                LongBuffer.wrap(new long[] { 0 }), LongBuffer.wrap(new long[] { 1 }), LongBuffer.wrap(new long[] { 0 }));
        addValues(ShortBuffer.class,
                ShortBuffer.wrap(new short[] { 0 }), ShortBuffer.wrap(new short[] { 1 }), ShortBuffer.wrap(new short[] { 0 }));
    }

    private void addAwtClasses() {
        addLazyFactory("java.awt.Color", AWT_FACTORY);
        addLazyFactory("java.awt.color.ColorSpace", AWT_FACTORY);
        addLazyFactory("java.awt.color.ICC_ColorSpace", AWT_FACTORY);
        addLazyFactory("java.awt.color.ICC_Profile", AWT_FACTORY);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addJava8ApiClasses() {
        addFactory(Optional.class, arity1(Optional::of, Optional::empty));
        addFactory(CompletableFuture.class, arity1(ignored -> new CompletableFuture<>(), CompletableFuture::new));

        addValues(LocalDateTime.class, LocalDateTime.MIN, LocalDateTime.MAX, LocalDateTime.MIN);
        addValues(LocalDate.class, LocalDate.MIN, LocalDate.MAX, LocalDate.MIN);
        addValues(LocalTime.class, LocalTime.MIN, LocalTime.MAX, LocalDate.MIN);
        addValues(ZoneId.class, ZoneId.of("+1"), ZoneId.of("-10"), ZoneId.of("+1"));
        addValues(ZoneOffset.class, ZoneOffset.ofHours(1), ZoneOffset.ofHours(-1), ZoneOffset.ofHours(1));
        addValues(DateTimeFormatter.class, DateTimeFormatter.ISO_TIME, DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_TIME);
        addValues(StampedLock.class, new StampedLock(), new StampedLock(), new StampedLock());
        addValues(ZonedDateTime.class,
            ZonedDateTime.parse("2017-12-13T10:15:30+01:00"),
            ZonedDateTime.parse("2016-11-12T09:14:29-01:00"),
            ZonedDateTime.parse("2017-12-13T10:15:30+01:00"));
    }

    private void addJavaFxClasses() {
        addJavaFxCollection("ObservableList", List.class, "observableList");
        addJavaFxCollection("ObservableMap", Map.class, "observableMap");
        addJavaFxCollection("ObservableSet", Set.class, "observableSet");
        addJavaFxProperty("BooleanProperty", "SimpleBooleanProperty", boolean.class);
        addJavaFxProperty("DoubleProperty", "SimpleDoubleProperty", double.class);
        addJavaFxProperty("FloatProperty", "SimpleFloatProperty", float.class);
        addJavaFxProperty("IntegerProperty", "SimpleIntegerProperty", int.class);
        addJavaFxProperty("ListProperty", "SimpleListProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableList"));
        addJavaFxProperty("LongProperty", "SimpleLongProperty", long.class);
        addJavaFxProperty("MapProperty", "SimpleMapProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableMap"));
        addJavaFxProperty("ObjectProperty", "SimpleObjectProperty", Object.class);
        addJavaFxProperty("SetProperty", "SimpleSetProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableSet"));
        addJavaFxProperty("StringProperty", "SimpleStringProperty", String.class);
    }

    private void addJavaxApiClasses() {
        addLazyFactory("javax.naming.Reference", JAVAX_FACTORY);
    }

    private void addGoogleGuavaMultisetCollectionsClasses() {
        addLazyFactory(GUAVA_PACKAGE + "Multiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "SortedMultiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "HashMultiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "TreeMultiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "LinkedHashMultiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ConcurrentHashMultiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "EnumMultiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableMultiset", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableSortedMultiset", GUAVA_FACTORY);
    }

    private void addGoogleGuavaMultimapCollectionsClasses() {
        addLazyFactory(GUAVA_PACKAGE + "Multimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ListMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "SetMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "SortedSetMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ArrayListMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "HashMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "LinkedListMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "LinkedHashMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "TreeMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableListMultimap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableSetMultimap", GUAVA_FACTORY);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addGoogleGuavaBiMapCollectionsClasses() {
        addLazyFactory(GUAVA_PACKAGE + "BiMap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "HashBiMap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "EnumHashBiMap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableBiMap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "EnumBiMap", GUAVA_FACTORY);
    }

    private void addGoogleGuavaTableCollectionClasses() {
        addLazyFactory(GUAVA_PACKAGE + "Table", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "HashBasedTable", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "TreeBasedTable", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ArrayTable", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableTable", GUAVA_FACTORY);
    }

    private void addGoogleGuavaRegularCollectionsClasses() {
        addLazyFactory(GUAVA_PACKAGE + "EvictingQueue", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "MinMaxPriorityQueue", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableRangeSet", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "TreeRangeSet", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "RangeSet", GUAVA_FACTORY);
    }

    private void addGoogleGuavaImmutableClasses() {
        addLazyFactory(GUAVA_PACKAGE + "ImmutableCollection", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableList", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableMap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableSet", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableSortedMap", GUAVA_FACTORY);
        addLazyFactory(GUAVA_PACKAGE + "ImmutableSortedSet", GUAVA_FACTORY);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addNewGoogleGuavaClasses() {
        addLazyFactory(GUAVA_PACKAGE + "Range", GUAVA_FACTORY);
        addLazyFactory("com.google.common.base.Optional", GUAVA_FACTORY);
    }

    private void addJodaTimeClasses() {
        addLazyFactory(JODA_PACKAGE + "Chronology", JODA_FACTORY);
        addLazyFactory(JODA_PACKAGE + "DateTimeZone", JODA_FACTORY);
        addLazyFactory(JODA_PACKAGE + "PeriodType", JODA_FACTORY);
        addLazyFactory(JODA_PACKAGE + "YearMonth", JODA_FACTORY);
        addLazyFactory(JODA_PACKAGE + "MonthDay", JODA_FACTORY);
    }

    @SuppressWarnings("unchecked")
    private <T> void addValues(Class<T> type, Object red, Object black, Object redCopy) {
        prefabValues.addFactory(type, (T)red, (T)black, (T)redCopy);
    }

    private <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        prefabValues.addFactory(type, factory);
    }

    private <T> void addLazyFactory(String typeName, PrefabValueFactory<T> factory) {
        prefabValues.addLazyFactory(typeName, factory);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addJavaFxCollection(String name, Class<?> copyFrom, String factoryMethod) {
        String className = JAVAFX_COLLECTIONS_PACKAGE + name;
        addFactory(classForName(className),
                new ReflectiveCollectionCopyFactory(className, copyFrom, JAVAFX_COLLECTIONS_PACKAGE + "FXCollections", factoryMethod));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addJavaFxProperty(String declaredType, String actualType, Class<?> propertyType) {
        addFactory(classForName(JAVAFX_PROPERTY_PACKAGE + declaredType),
                new ReflectiveJavaFxPropertyFactory(JAVAFX_PROPERTY_PACKAGE + actualType, propertyType));
    }
}
