/*
 * Copyright 2010-2015 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.ConditionalInstantiator;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.*;

import javax.naming.Reference;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.*;

/**
 * Creates instances of classes for use in a {@link PrefabValues} object.
 *
 * Contains hand-made instances of well-known Java API classes that cannot be
 * instantiated dynamically because of an internal infinite recursion of types,
 * or other issues.
 *
 * @author Jan Ouwens
 */
@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "That would be dozens of separate classes")
public final class JavaApiPrefabValues {
    private static final String JAVAFX_COLLECTIONS_PACKAGE = "javafx.collections.";
    private static final String JAVAFX_PROPERTY_PACKAGE = "javafx.beans.property.";
    private static final String GUAVA_PACKAGE = "com.google.common.collect.";
    private static final String JODA_PACKAGE = "org.joda.time.";

    private static final Comparator<Object> OBJECT_COMPARATOR = new Comparator<Object>() {
        @Override public int compare(Object o1, Object o2) { return Integer.compare(o1.hashCode(), o2.hashCode()); }
    };

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
        addJava8ApiClasses();
        addJavaFxClasses();
        addGoogleGuavaMultisetCollectionsClasses();
        addGoogleGuavaMultimapCollectionsClasses();
        addGoogleGuavaBiMapCollectionsClasses();
        addGoogleGuavaTableCollectionClasses();
        addGoogleGuavaRegularCollectionsClasses();
        addGoogleGuavaImmutableClasses();
        addNewGoogleGuavaClasses();
        addJodaTimeClasses();
    }

    private void addPrimitiveClasses() {
        addValues(boolean.class, true, false);
        addValues(byte.class, (byte)1, (byte)2);
        addValues(char.class, 'a', 'b');
        addValues(double.class, 0.5D, 1.0D);
        addValues(float.class, 0.5F, 1.0F);
        addValues(int.class, 1, 2);
        addValues(long.class, 1L, 2L);
        addValues(short.class, (short)1, (short)2);

        addValues(Boolean.class, true, false);
        addValues(Byte.class, (byte)1, (byte)2);
        addValues(Character.class, 'a', 'b');
        addValues(Double.class, 0.5D, 1.0D);
        addValues(Float.class, 0.5F, 1.0F);
        addValues(Integer.class, 1, 2);
        addValues(Long.class, 1L, 2L);
        addValues(Short.class, (short)1, (short)2);

        addValues(Object.class, new Object(), new Object());
        addValues(Class.class, Class.class, Object.class);
        addValues(String.class, "one", "two");
        addValues(Enum.class, Dummy.RED, Dummy.BLACK);
    }

    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Just need an instance, not for actual use.")
    private void addClasses() {
        addValues(BigDecimal.class, BigDecimal.ZERO, BigDecimal.ONE);
        addValues(BigInteger.class, BigInteger.ZERO, BigInteger.ONE);
        addValues(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
        addValues(Date.class, new Date(0), new Date(1));
        addValues(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance());
        addValues(File.class, new File(""), new File("/"));
        addValues(Formatter.class, new Formatter(), new Formatter());
        addValues(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
        addValues(Locale.class, new Locale("nl"), new Locale("hu"));
        addValues(Pattern.class, Pattern.compile("one"), Pattern.compile("two"));
        addValues(Reference.class, new Reference("one"), new Reference("two"));
        addValues(SimpleDateFormat.class, new SimpleDateFormat("yMd"), new SimpleDateFormat("dMy"));
        addValues(Scanner.class, new Scanner("one"), new Scanner("two"));
        addValues(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"));
        addValues(Throwable.class, new Throwable(), new Throwable());
        addValues(UUID.class, new UUID(0, -1), new UUID(1, 0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addCollection() {
        addFactory(Iterable.class, new CollectionFactory() {
            @Override public Collection createEmpty() { return new ArrayList<>(); }
        });
        addFactory(Collection.class, new CollectionFactory<Collection>() {
            @Override public Collection createEmpty() { return new ArrayList<>(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addLists() {
        addFactory(List.class, new CollectionFactory<List>() {
            @Override public List createEmpty() { return new ArrayList<>(); }
        });
        addFactory(CopyOnWriteArrayList.class, new CollectionFactory<CopyOnWriteArrayList>() {
            @Override public CopyOnWriteArrayList createEmpty() { return new CopyOnWriteArrayList<>(); }
        });
        addFactory(LinkedList.class, new CollectionFactory<LinkedList>() {
            @Override public LinkedList createEmpty() { return new LinkedList<>(); }
        });
        addFactory(ArrayList.class, new CollectionFactory<ArrayList>() {
            @Override public ArrayList createEmpty() { return new ArrayList<>(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addMaps() {
        addFactory(Map.class, new MapFactory<Map>() {
            @Override public Map createEmpty() { return new HashMap<>(); }
        });
        addFactory(SortedMap.class, new MapFactory<SortedMap>() {
            @Override public SortedMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(NavigableMap.class, new MapFactory<NavigableMap>() {
            @Override public NavigableMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(ConcurrentNavigableMap.class, new MapFactory<ConcurrentNavigableMap>() {
            @Override public ConcurrentNavigableMap createEmpty() { return new ConcurrentSkipListMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(ConcurrentHashMap.class, new MapFactory<ConcurrentHashMap>() {
            @Override public ConcurrentHashMap createEmpty() { return new ConcurrentHashMap<>(); }
        });
        addFactory(HashMap.class, new MapFactory<HashMap>() {
            @Override public HashMap createEmpty() { return new HashMap<>(); }
        });
        addFactory(Hashtable.class, new MapFactory<Hashtable>() {
            @Override public Hashtable createEmpty() { return new Hashtable<>(); }
        });
        addFactory(LinkedHashMap.class, new MapFactory<LinkedHashMap>() {
            @Override public LinkedHashMap createEmpty() { return new LinkedHashMap<>(); }
        });
        addFactory(Properties.class, new MapFactory<Properties>() {
            @Override public Properties createEmpty() { return new Properties(); }
        });
        addFactory(TreeMap.class, new MapFactory<TreeMap>() {
            @Override public TreeMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        addFactory(WeakHashMap.class, new MapFactory<WeakHashMap>() {
            @Override public WeakHashMap createEmpty() { return new WeakHashMap<>(); }
        });
        addFactory(EnumMap.class, new ReflectiveEnumMapFactory());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addSets() {
        addFactory(Set.class, new CollectionFactory<Set>() {
            @Override public Set createEmpty() { return new HashSet(); }
        });
        addFactory(SortedSet.class, new CollectionFactory<SortedSet>() {
            @Override public SortedSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        addFactory(NavigableSet.class, new CollectionFactory<NavigableSet>() {
            @Override public NavigableSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        addFactory(CopyOnWriteArraySet.class, new CollectionFactory<CopyOnWriteArraySet>() {
            @Override public CopyOnWriteArraySet createEmpty() { return new CopyOnWriteArraySet<>(); }
        });
        addFactory(TreeSet.class, new CollectionFactory<TreeSet>() {
            @Override public TreeSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        addFactory(EnumSet.class, new ReflectiveEnumSetFactory());

        BitSet redBitSet = new BitSet();
        BitSet blackBitSet = new BitSet();
        blackBitSet.set(0);
        addValues(BitSet.class, redBitSet, blackBitSet);
    }

    @SuppressWarnings("rawtypes")
    private void addQueues() {
        addFactory(Queue.class, new CollectionFactory<Queue>() {
            @Override public Queue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        addFactory(BlockingQueue.class, new CollectionFactory<BlockingQueue>() {
            @Override public BlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        addFactory(Deque.class, new CollectionFactory<Deque>() {
            @Override public Deque createEmpty() { return new ArrayDeque<>(1); }
        });
        addFactory(BlockingDeque.class, new CollectionFactory<BlockingDeque>() {
            @Override public BlockingDeque createEmpty() { return new LinkedBlockingDeque<>(1); }
        });
        addFactory(ArrayBlockingQueue.class, new CollectionFactory<ArrayBlockingQueue>() {
            @Override public ArrayBlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        addFactory(ConcurrentLinkedQueue.class, new CollectionFactory<ConcurrentLinkedQueue>() {
            @Override public ConcurrentLinkedQueue createEmpty() { return new ConcurrentLinkedQueue<>(); }
        });
        addFactory(DelayQueue.class, new PrefabValueFactory<DelayQueue>() {
            @SuppressWarnings("unchecked")
            @Override
            public Tuple<DelayQueue> createValues(TypeTag tag, PrefabValues pf, LinkedHashSet<TypeTag> typeStack) {
                TypeTag delayed = new TypeTag(Delayed.class);
                DelayQueue red = new DelayQueue<>();
                red.add(pf.<Delayed>giveRed(delayed));
                DelayQueue black = new DelayQueue<>();
                black.add(pf.<Delayed>giveBlack(delayed));
                return new Tuple<>(red, black);
            }
        });
        addFactory(LinkedBlockingQueue.class, new CollectionFactory<LinkedBlockingQueue>() {
            @Override public LinkedBlockingQueue createEmpty() { return new LinkedBlockingQueue<>(1); }
        });
        addFactory(PriorityBlockingQueue.class, new CollectionFactory<PriorityBlockingQueue>() {
            @SuppressWarnings("unchecked")
            @Override public PriorityBlockingQueue createEmpty() { return new PriorityBlockingQueue<>(1, OBJECT_COMPARATOR); }
        });
        addValues(SynchronousQueue.class, new SynchronousQueue<>(), new SynchronousQueue<>());
    }

    @SuppressWarnings("unchecked")
    private void addJava8ApiClasses() {
        String optional = "java.util.Optional";
        addFactory(forName(optional), new ReflectiveGenericContainerFactory(optional));

        ConditionalInstantiator zoneId = new ConditionalInstantiator("java.time.ZoneId");
        addValues(zoneId.resolve(),
                zoneId.callFactory("of", classes(String.class), objects("+1")),
                zoneId.callFactory("of", classes(String.class), objects("-10")));
        ConditionalInstantiator dateTimeFormatter = new ConditionalInstantiator("java.time.format.DateTimeFormatter");
        addValues(dateTimeFormatter.resolve(),
                dateTimeFormatter.returnConstant("ISO_TIME"),
                dateTimeFormatter.returnConstant("ISO_DATE"));
        ConditionalInstantiator completableFuture = new ConditionalInstantiator("java.util.concurrent.CompletableFuture");
        addValues(completableFuture.resolve(),
                completableFuture.instantiate(classes(), objects()),
                completableFuture.instantiate(classes(), objects()));
        ConditionalInstantiator stampedLock = new ConditionalInstantiator("java.util.concurrent.locks.StampedLock");
        addValues(stampedLock.resolve(),
                stampedLock.instantiate(classes(), objects()),
                stampedLock.instantiate(classes(), objects()));
    }

    private void addJavaFxClasses() {
        addJavaFxCollection("ObservableList", List.class, "observableList");
        addJavaFxCollection("ObservableMap", Map.class, "observableMap");
        addJavaFxCollection("ObservableSet", Set.class, "observableSet");
        addJavaFxProperty("BooleanProperty", "SimpleBooleanProperty", boolean.class);
        addJavaFxProperty("DoubleProperty", "SimpleDoubleProperty", double.class);
        addJavaFxProperty("FloatProperty", "SimpleFloatProperty", float.class);
        addJavaFxProperty("IntegerProperty", "SimpleIntegerProperty", int.class);
        addJavaFxProperty("ListProperty", "SimpleListProperty", forName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableList"));
        addJavaFxProperty("LongProperty", "SimpleLongProperty", long.class);
        addJavaFxProperty("MapProperty", "SimpleMapProperty", forName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableMap"));
        addJavaFxProperty("ObjectProperty", "SimpleObjectProperty", Object.class);
        addJavaFxProperty("SetProperty", "SimpleSetProperty", forName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableSet"));
        addJavaFxProperty("StringProperty", "SimpleStringProperty", String.class);
    }

    private void addGoogleGuavaMultisetCollectionsClasses() {
        addNewGuavaCollection("Multiset", "HashMultiset");
        addNewGuavaCollection("SortedMultiset", "TreeMultiset", Comparator.class, OBJECT_COMPARATOR);
        addNewGuavaCollection("HashMultiset", "HashMultiset");
        addNewGuavaCollection("TreeMultiset", "TreeMultiset", Comparator.class, OBJECT_COMPARATOR);
        addNewGuavaCollection("LinkedHashMultiset", "LinkedHashMultiset");
        addNewGuavaCollection("ConcurrentHashMultiset", "ConcurrentHashMultiset");
        addCopiedGuavaCollection("EnumMultiset", Iterable.class, EnumSet.class, "create");
        addCopiedGuavaCollection("ImmutableMultiset", Iterable.class);
        addCopiedGuavaCollection("ImmutableSortedMultiset", forName(GUAVA_PACKAGE + "SortedMultiset"), "copyOfSorted");
    }

    private void addGoogleGuavaMultimapCollectionsClasses() {
        addNewGuavaMap("Multimap", "ArrayListMultimap");
        addNewGuavaMap("ListMultimap", "ArrayListMultimap");
        addNewGuavaMap("SetMultimap", "HashMultimap");
        addNewGuavaMap("SortedSetMultimap", "TreeMultimap", OBJECT_COMPARATOR);
        addNewGuavaMap("ArrayListMultiMap", "ArrayListMultimap");
        addNewGuavaMap("HashMultimap", "HashMultimap");
        addNewGuavaMap("LinkedListMultimap", "LinkedListMultimap");
        addNewGuavaMap("LinkedHashMultimap", "LinkedHashMultimap");
        addNewGuavaMap("TreeMultimap", "TreeMultimap", OBJECT_COMPARATOR);
        addCopiedGuavaCollection("ImmutableListMultimap", forName(GUAVA_PACKAGE + "Multimap"));
        addCopiedGuavaCollection("ImmutableSetMultimap", forName(GUAVA_PACKAGE + "Multimap"));

        ConditionalInstantiator ci = new ConditionalInstantiator(GUAVA_PACKAGE + "Multimap");
        addCopiedGuavaCollection("ImmutableMultimap", "ImmutableListMultimap", ci.resolve(), ci.resolve(), "copyOf");
    }

    @SuppressWarnings("unchecked")
    private void addGoogleGuavaBiMapCollectionsClasses() {
        addNewGuavaMap("BiMap", "HashBiMap");
        addNewGuavaMap("HashBiMap", "HashBiMap");
        addCopiedGuavaCollection("EnumHashBiMap", Map.class, EnumMap.class, "create");
        addCopiedGuavaCollection("ImmutableBiMap", Map.class);
        addFactory(forName(GUAVA_PACKAGE + "EnumBiMap"), new ReflectiveGuavaEnumBiMapFactory());
    }

    private void addGoogleGuavaTableCollectionClasses() {
        addNewGuavaTable("Table", "HashBasedTable");
        addNewGuavaTable("HashBasedTable", "HashBasedTable");
        addNewGuavaTable("TreeBasedTable", "TreeBasedTable", OBJECT_COMPARATOR);
        addCopiedGuavaCollection("ArrayTable", forName(GUAVA_PACKAGE + "Table"), "create");
        addCopiedGuavaCollection("ImmutableTable", forName(GUAVA_PACKAGE + "Table"));
    }

    private void addGoogleGuavaRegularCollectionsClasses() {
        addNewGuavaCollection("EvictingQueue", "EvictingQueue", int.class, 10);
        addNewGuavaCollection("MinMaxPriorityQueue", "MinMaxPriorityQueue");
    }

    private void addGoogleGuavaImmutableClasses() {
        addCopiedGuavaCollection("ImmutableCollection", "ImmutableList", Collection.class, Collection.class, "copyOf");
        addCopiedGuavaCollection("ImmutableList", Collection.class);
        addCopiedGuavaCollection("ImmutableMap", Map.class);
        addCopiedGuavaCollection("ImmutableSet", Collection.class);
        addCopiedGuavaCollection("ImmutableSortedMap", Map.class);
        addCopiedGuavaCollection("ImmutableSortedSet", Collection.class);
    }

    @SuppressWarnings("unchecked")
    private void addNewGoogleGuavaClasses() {
        ConditionalInstantiator range = new ConditionalInstantiator(GUAVA_PACKAGE + "Range");
        addValues(range.resolve(),
                range.callFactory("open", classes(Comparable.class, Comparable.class), objects(1, 2)),
                range.callFactory("open", classes(Comparable.class, Comparable.class), objects(3, 4)));

        String optional = "com.google.common.base.Optional";
        addFactory(forName(optional), new ReflectiveGenericContainerFactory(optional));
    }

    private void addJodaTimeClasses() {
        ConditionalInstantiator chronology = new ConditionalInstantiator(JODA_PACKAGE + "Chronology");
        addValues(chronology.resolve(),
                chronology.callFactory(JODA_PACKAGE + "chrono.GregorianChronology", "getInstanceUTC", classes(), objects()),
                chronology.callFactory(JODA_PACKAGE + "chrono.ISOChronology", "getInstanceUTC", classes(), objects()));
        ConditionalInstantiator dateTimeZone = new ConditionalInstantiator(JODA_PACKAGE + "DateTimeZone");
        addValues(dateTimeZone.resolve(),
                dateTimeZone.callFactory("forOffsetHours", classes(int.class), objects(+1)),
                dateTimeZone.callFactory("forOffsetHours", classes(int.class), objects(-10)));
        ConditionalInstantiator periodType = new ConditionalInstantiator(JODA_PACKAGE + "PeriodType");
        addValues(periodType.resolve(),
                periodType.callFactory("days", classes(), objects()),
                periodType.callFactory("hours", classes(), objects()));
        ConditionalInstantiator yearMonth = new ConditionalInstantiator(JODA_PACKAGE + "YearMonth");
        addValues(yearMonth.resolve(),
                yearMonth.instantiate(classes(int.class, int.class), objects(2009, 6)),
                yearMonth.instantiate(classes(int.class, int.class), objects(2014, 7)));
        ConditionalInstantiator monthDay = new ConditionalInstantiator(JODA_PACKAGE + "MonthDay");
        addValues(monthDay.resolve(),
                monthDay.instantiate(classes(int.class, int.class), objects(6, 1)),
                monthDay.instantiate(classes(int.class, int.class), objects(6, 26)));
    }

    @SuppressWarnings("unchecked")
    private <T> void addValues(Class<T> type, Object red, Object black) {
        prefabValues.addFactory(type, (T)red, (T)black);
    }

    private <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        prefabValues.addFactory(type, factory);
    }

    @SuppressWarnings("unchecked")
    private void addJavaFxCollection(String name, Class<?> copyFrom, String factoryMethod) {
        String className = JAVAFX_COLLECTIONS_PACKAGE + name;
        addFactory(forName(className),
                new ReflectiveCollectionCopyFactory(className, copyFrom, JAVAFX_COLLECTIONS_PACKAGE + "FXCollections", factoryMethod));
    }

    @SuppressWarnings("unchecked")
    private void addJavaFxProperty(String declaredType, String actualType, Class<?> propertyType) {
        addFactory(forName(JAVAFX_PROPERTY_PACKAGE + declaredType),
                new JavaFxPropertyFactory(JAVAFX_PROPERTY_PACKAGE + actualType, propertyType));
    }

    private <T> void addNewGuavaCollection(String declaredType, String actualType) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)forName(GUAVA_PACKAGE + declaredType);
        ReflectiveCollectionFactory<T> factory =
                ReflectiveCollectionFactory.callFactoryMethod(GUAVA_PACKAGE + actualType, "create");
        addFactory(type, factory);
    }

    private <T, U> void addNewGuavaCollection(String declaredType, String actualType, Class<U> parameterType, U parameterValue) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)forName(GUAVA_PACKAGE + declaredType);
        ReflectiveCollectionFactory<T> factory =
                ReflectiveCollectionFactory.callFactoryMethodWithParameter(GUAVA_PACKAGE + actualType, "create", parameterType, parameterValue);
        addFactory(type, factory);
    }

    private <T> void addNewGuavaMap(String declaredType, String actualType) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)forName(GUAVA_PACKAGE + declaredType);
        ReflectiveMapFactory<T> factory = ReflectiveMapFactory.callFactoryMethod(GUAVA_PACKAGE + actualType, "create");
        addFactory(type, factory);
    }

    private <T> void addNewGuavaMap(String declaredType, String actualType, Comparator<Object> comparator) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)forName(GUAVA_PACKAGE + declaredType);
        ReflectiveMapFactory<T> factory =
                ReflectiveMapFactory.callFactoryMethodWithComparator(GUAVA_PACKAGE + actualType, "create", comparator);
        addFactory(type, factory);
    }

    private <T> void addNewGuavaTable(String declaredType, String actualType, Comparator<Object> comparator) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)forName(GUAVA_PACKAGE + declaredType);
        ReflectiveGuavaTableFactory<T> factory =
                ReflectiveGuavaTableFactory.callFactoryMethodWithComparator(GUAVA_PACKAGE + actualType, "create", comparator);
        addFactory(type, factory);
    }

    private <T> void addNewGuavaTable(String declaredType, String actualType) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)forName(GUAVA_PACKAGE + declaredType);
        ReflectiveGuavaTableFactory<T> factory =
                ReflectiveGuavaTableFactory.callFactoryMethod(GUAVA_PACKAGE + actualType, "create");
        addFactory(type, factory);
    }

    private void addCopiedGuavaCollection(String name, Class<?> copyFrom) {
        addCopiedGuavaCollection(name, copyFrom, "copyOf");
    }

    private void addCopiedGuavaCollection(String name, Class<?> copyFrom, String copyMethodName) {
        addCopiedGuavaCollection(name, copyFrom, copyFrom, copyMethodName);
    }

    private void addCopiedGuavaCollection(String name, Class<?> declaredCopyFrom, Class<?> actualCopyFrom, String copyMethodName) {
        addCopiedGuavaCollection(name, name, declaredCopyFrom, actualCopyFrom, copyMethodName);
    }

    @SuppressWarnings("unchecked")
    private void addCopiedGuavaCollection(String declaredName, String actualName,
            Class<?> declaredCopyFrom, Class<?> actualCopyFrom, String copyMethodName) {

        String className = GUAVA_PACKAGE + actualName;
        addFactory(forName(GUAVA_PACKAGE + declaredName),
                new ReflectiveCollectionCopyFactory(className, declaredCopyFrom, actualCopyFrom, className, copyMethodName));
    }
}
