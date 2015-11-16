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
import nl.jqno.equalsverifier.internal.ConditionalPrefabValueBuilder;
import nl.jqno.equalsverifier.internal.prefabvalues.*;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.GuavaCollectionFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.JavaFxCollectionFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.JavaFxPropertyFactory;

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
import static nl.jqno.equalsverifier.internal.prefabvalues.factories.GuavaCollectionFactory.Kind.COLLECTION;
import static nl.jqno.equalsverifier.internal.prefabvalues.factories.GuavaCollectionFactory.Kind.MAP;

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
    private static final Comparator<Object> OBJECT_COMPARATOR = new Comparator<Object>() {
        @Override public int compare(Object o1, Object o2) { return Integer.compare(o1.hashCode(), o2.hashCode()); }
    };

    private PrefabValues prefabValues;

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
        addGoogleGuavaClasses();
        addJodaTimeClasses();
    }

    private void addPrimitiveClasses() {
        prefabValues.addFactory(boolean.class, true, false);
        prefabValues.addFactory(byte.class, (byte)1, (byte)2);
        prefabValues.addFactory(char.class, 'a', 'b');
        prefabValues.addFactory(double.class, 0.5D, 1.0D);
        prefabValues.addFactory(float.class, 0.5F, 1.0F);
        prefabValues.addFactory(int.class, 1, 2);
        prefabValues.addFactory(long.class, 1L, 2L);
        prefabValues.addFactory(short.class, (short)1, (short)2);

        prefabValues.addFactory(Boolean.class, true, false);
        prefabValues.addFactory(Byte.class, (byte)1, (byte)2);
        prefabValues.addFactory(Character.class, 'a', 'b');
        prefabValues.addFactory(Double.class, 0.5D, 1.0D);
        prefabValues.addFactory(Float.class, 0.5F, 1.0F);
        prefabValues.addFactory(Integer.class, 1, 2);
        prefabValues.addFactory(Long.class, 1L, 2L);
        prefabValues.addFactory(Short.class, (short)1, (short)2);

        prefabValues.addFactory(Object.class, new Object(), new Object());
        prefabValues.addFactory(Class.class, Class.class, Object.class);
        prefabValues.addFactory(String.class, "one", "two");
    }

    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Just need an instance, not for actual use.")
    private void addClasses() {
        prefabValues.addFactory(BigDecimal.class, BigDecimal.ZERO, BigDecimal.ONE);
        prefabValues.addFactory(BigInteger.class, BigInteger.ZERO, BigInteger.ONE);
        prefabValues.addFactory(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
        prefabValues.addFactory(Date.class, new Date(0), new Date(1));
        prefabValues.addFactory(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance());
        prefabValues.addFactory(File.class, new File(""), new File("/"));
        prefabValues.addFactory(Formatter.class, new Formatter(), new Formatter());
        prefabValues.addFactory(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
        prefabValues.addFactory(Locale.class, new Locale("nl"), new Locale("hu"));
        prefabValues.addFactory(Pattern.class, Pattern.compile("one"), Pattern.compile("two"));
        prefabValues.addFactory(Reference.class, new Reference("one"), new Reference("two"));
        prefabValues.addFactory(SimpleDateFormat.class, new SimpleDateFormat("yMd"), new SimpleDateFormat("dMy"));
        prefabValues.addFactory(Scanner.class, new Scanner("one"), new Scanner("two"));
        prefabValues.addFactory(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"));
        prefabValues.addFactory(Throwable.class, new Throwable(), new Throwable());
        prefabValues.addFactory(UUID.class, new UUID(0, -1), new UUID(1, 0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addCollection() {
        prefabValues.addFactory(Collection.class, new CollectionFactory<Collection>() {
            @Override public Collection createEmpty() { return new ArrayList<>(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addLists() {
        prefabValues.addFactory(List.class, new CollectionFactory<List>() {
            @Override public List createEmpty() { return new ArrayList<>(); }
        });
        prefabValues.addFactory(CopyOnWriteArrayList.class, new CollectionFactory<CopyOnWriteArrayList>() {
            @Override public CopyOnWriteArrayList createEmpty() { return new CopyOnWriteArrayList<>(); }
        });
        prefabValues.addFactory(LinkedList.class, new CollectionFactory<LinkedList>() {
            @Override public LinkedList createEmpty() { return new LinkedList<>(); }
        });
        prefabValues.addFactory(ArrayList.class, new CollectionFactory<ArrayList>() {
            @Override public ArrayList createEmpty() { return new ArrayList<>(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addMaps() {
        prefabValues.addFactory(Map.class, new MapFactory<Map>() {
            @Override public Map createEmpty() { return new HashMap<>(); }
        });
        prefabValues.addFactory(SortedMap.class, new MapFactory<SortedMap>() {
            @Override public SortedMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(NavigableMap.class, new MapFactory<NavigableMap>() {
            @Override public NavigableMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(ConcurrentNavigableMap.class, new MapFactory<ConcurrentNavigableMap>() {
            @Override public ConcurrentNavigableMap createEmpty() { return new ConcurrentSkipListMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(ConcurrentHashMap.class, new MapFactory<ConcurrentHashMap>() {
            @Override public ConcurrentHashMap createEmpty() { return new ConcurrentHashMap<>(); }
        });
        prefabValues.addFactory(HashMap.class, new MapFactory<HashMap>() {
            @Override public HashMap createEmpty() { return new HashMap<>(); }
        });
        prefabValues.addFactory(Hashtable.class, new MapFactory<Hashtable>() {
            @Override public Hashtable createEmpty() { return new Hashtable<>(); }
        });
        prefabValues.addFactory(LinkedHashMap.class, new MapFactory<LinkedHashMap>() {
            @Override public LinkedHashMap createEmpty() { return new LinkedHashMap<>(); }
        });
        prefabValues.addFactory(Properties.class, new MapFactory<Properties>() {
            @Override public Properties createEmpty() { return new Properties(); }
        });
        prefabValues.addFactory(TreeMap.class, new MapFactory<TreeMap>() {
            @Override public TreeMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(WeakHashMap.class, new MapFactory<WeakHashMap>() {
            @Override public WeakHashMap createEmpty() { return new WeakHashMap<>(); }
        });

        prefabValues.addFactory(EnumMap.class, Dummy.RED.map(), Dummy.BLACK.map());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addSets() {
        prefabValues.addFactory(Set.class, new CollectionFactory<Set>() {
            @Override public Set createEmpty() { return new HashSet(); }
        });
        prefabValues.addFactory(SortedSet.class, new CollectionFactory<SortedSet>() {
            @Override public SortedSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(NavigableSet.class, new CollectionFactory<NavigableSet>() {
            @Override public NavigableSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(CopyOnWriteArraySet.class, new CollectionFactory<CopyOnWriteArraySet>() {
            @Override public CopyOnWriteArraySet createEmpty() { return new CopyOnWriteArraySet<>(); }
        });
        prefabValues.addFactory(TreeSet.class, new CollectionFactory<TreeSet>() {
            @Override public TreeSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });

        prefabValues.addFactory(EnumSet.class, EnumSet.of(Dummy.RED), EnumSet.of(Dummy.BLACK));

        BitSet redBitSet = new BitSet();
        BitSet blackBitSet = new BitSet();
        blackBitSet.set(0);
        prefabValues.addFactory(BitSet.class, redBitSet, blackBitSet);
    }

    @SuppressWarnings("rawtypes")
    private void addQueues() {
        prefabValues.addFactory(Queue.class, new CollectionFactory<Queue>() {
            @Override public Queue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        prefabValues.addFactory(BlockingQueue.class, new CollectionFactory<BlockingQueue>() {
            @Override public BlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        prefabValues.addFactory(Deque.class, new CollectionFactory<Deque>() {
            @Override public Deque createEmpty() { return new ArrayDeque<>(1); }
        });
        prefabValues.addFactory(BlockingDeque.class, new CollectionFactory<BlockingDeque>() {
            @Override public BlockingDeque createEmpty() { return new LinkedBlockingDeque<>(1); }
        });
        prefabValues.addFactory(ArrayBlockingQueue.class, new CollectionFactory<ArrayBlockingQueue>() {
            @Override public ArrayBlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        prefabValues.addFactory(ConcurrentLinkedQueue.class, new CollectionFactory<ConcurrentLinkedQueue>() {
            @Override public ConcurrentLinkedQueue createEmpty() { return new ConcurrentLinkedQueue<>(); }
        });
        prefabValues.addFactory(DelayQueue.class, new PrefabValueFactory<DelayQueue>() {
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
        prefabValues.addFactory(LinkedBlockingQueue.class, new CollectionFactory<LinkedBlockingQueue>() {
            @Override public LinkedBlockingQueue createEmpty() { return new LinkedBlockingQueue<>(1); }
        });
        prefabValues.addFactory(PriorityBlockingQueue.class, new CollectionFactory<PriorityBlockingQueue>() {
            @SuppressWarnings("unchecked")
            @Override public PriorityBlockingQueue createEmpty() { return new PriorityBlockingQueue<>(1, OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(SynchronousQueue.class, new SynchronousQueue<>(), new SynchronousQueue<>());
    }

    private void addJava8ApiClasses() {
        ConditionalPrefabValueBuilder.of("java.time.ZoneId")
                .callFactory("of", classes(String.class), objects("+1"))
                .callFactory("of", classes(String.class), objects("-10"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("java.time.format.DateTimeFormatter")
                .withConstant("ISO_TIME")
                .withConstant("ISO_DATE")
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("java.util.concurrent.CompletableFuture")
                .instantiate(classes(), objects())
                .instantiate(classes(), objects())
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("java.util.concurrent.locks.StampedLock")
                .instantiate(classes(), objects())
                .instantiate(classes(), objects())
                .addTo(prefabValues);
    }

    @SuppressWarnings("unchecked")
    private void addJavaFxClasses() {
        prefabValues.addFactory(forName("javafx.collections.ObservableList"),
                new JavaFxCollectionFactory("ObservableList", List.class, "observableList"));
        prefabValues.addFactory(forName("javafx.collections.ObservableMap"),
                new JavaFxCollectionFactory("ObservableMap", Map.class, "observableMap"));
        prefabValues.addFactory(forName("javafx.collections.ObservableSet"),
                new JavaFxCollectionFactory("ObservableSet", Set.class, "observableSet"));
        prefabValues.addFactory(forName("javafx.beans.property.BooleanProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleBooleanProperty", boolean.class));
        prefabValues.addFactory(forName("javafx.beans.property.DoubkeProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleDoubkeProperty", double.class));
        prefabValues.addFactory(forName("javafx.beans.property.FloatProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleFloatProperty", float.class));
        prefabValues.addFactory(forName("javafx.beans.property.IntegerProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleIntegerProperty", int.class));
        prefabValues.addFactory(forName("javafx.beans.property.ListProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleListProperty", forName("javafx.collections.ObservableList")));
        prefabValues.addFactory(forName("javafx.beans.property.LongProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleLongProperty", long.class));
        prefabValues.addFactory(forName("javafx.beans.property.MapProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleMapProperty", forName("javafx.collections.ObservableMap")));
        prefabValues.addFactory(forName("javafx.beans.property.ObjectProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleObjectProperty", Object.class));
        prefabValues.addFactory(forName("javafx.beans.property.SetProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleSetProperty", forName("javafx.collections.ObservableSet")));
        prefabValues.addFactory(forName("javafx.beans.property.StringProperty"),
                new JavaFxPropertyFactory("javafx.beans.property.SimpleStringProperty", String.class));
    }

    @SuppressWarnings("unchecked")
    private void addGoogleGuavaClasses() {
        prefabValues.addFactory(forName("com.google.common.collect.ImmutableList"),
                new GuavaCollectionFactory("ImmutableList", COLLECTION));
        prefabValues.addFactory(forName("com.google.common.collect.ImmutableMap"),
                new GuavaCollectionFactory("ImmutableMap", MAP));
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSet")
                .callFactory("of", classes(Object.class), objects("red"))
                .callFactory("of", classes(Object.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSortedMap")
                .callFactory("of", classes(Comparable.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Comparable.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSortedSet")
                .callFactory("of", classes(Comparable.class), objects("red"))
                .callFactory("of", classes(Comparable.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableMultiset")
                .callFactory("of", classes(Object.class), objects("red"))
                .callFactory("of", classes(Object.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSortedMultiset")
                .callFactory("of", classes(Comparable.class), objects("red"))
                .callFactory("of", classes(Comparable.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableListMultimap")
                .callFactory("of", classes(Object.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Object.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSetMultimap")
                .callFactory("of", classes(Object.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Object.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableBiMap")
                .callFactory("of", classes(Object.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Object.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableTable")
                .callFactory("of", classes(Object.class, Object.class, Object.class), objects("red", "X", "value"))
                .callFactory("of", classes(Object.class, Object.class, Object.class), objects("black", "X", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.Range")
                .callFactory("open", classes(Comparable.class, Comparable.class), objects(1, 2))
                .callFactory("open", classes(Comparable.class, Comparable.class), objects(3, 4))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.base.Optional")
                .callFactory("of", classes(Object.class), objects("red"))
                .callFactory("of", classes(Object.class), objects("black"))
                .addTo(prefabValues);
    }

    private void addJodaTimeClasses() {
        ConditionalPrefabValueBuilder.of("org.joda.time.Chronology")
                .withConcreteClass("org.joda.time.chrono.GregorianChronology")
                .callFactory("getInstanceUTC", classes(), objects())
                .withConcreteClass("org.joda.time.chrono.ISOChronology")
                .callFactory("getInstanceUTC", classes(), objects())
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.DateTimeZone")
                .callFactory("forOffsetHours", classes(int.class), objects(+1))
                .callFactory("forOffsetHours", classes(int.class), objects(-10))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.PeriodType")
                .callFactory("days", classes(), objects())
                .callFactory("hours", classes(), objects())
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.YearMonth")
                .instantiate(classes(int.class, int.class), objects(2009, 6))
                .instantiate(classes(int.class, int.class), objects(2014, 7))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.MonthDay")
                .instantiate(classes(int.class, int.class), objects(6, 1))
                .instantiate(classes(int.class, int.class), objects(7, 26))
                .addTo(prefabValues);
    }

    private enum Dummy {
        RED, BLACK;

        public EnumMap<Dummy, String> map() {
            EnumMap<Dummy, String> result = new EnumMap<>(Dummy.class);
            result.put(this, toString());
            return result;
        }
    }
}
