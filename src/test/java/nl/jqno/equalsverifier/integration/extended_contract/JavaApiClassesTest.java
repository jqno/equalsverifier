package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class JavaApiClassesTest extends IntegrationTestBase {
    @Test
    public void succeed_whenClassContainsASuperCollection() {
        EqualsVerifier.forClass(SuperCollectionContainer.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsAList() {
        EqualsVerifier.forClass(ListContainer.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsASet() {
        EqualsVerifier.forClass(SetContainer.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsAQueue() {
        EqualsVerifier.forClass(QueueContainer.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsAMap() {
        EqualsVerifier.forClass(MapContainer.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsACommonJavaApiType() {
        EqualsVerifier.forClass(CommonClassesContainer.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsAThreadLocalField() {
        EqualsVerifier.forClass(ThreadLocalContainer.class)
                .verify();
    }


    abstract static class CollectionContainer {

        protected abstract void callAbstractMethodsOnInterface();

        protected int doHashCode() {
            callAbstractMethodsOnInterface();
            return defaultHashCode(this);
        }

        protected void callIterator(Iterable<?>... collections) {
            for (Iterable<?> c : collections) {
                if (c != null) {
                    c.iterator();
                }
            }
        }

        protected void callKeySet(Map<?, ?>... maps) {
            for (Map<?, ?> m : maps) {
                if (m != null) {
                    m.keySet();
                }
            }
        }
    }

    static final class SuperCollectionContainer extends CollectionContainer {
        private final Iterable<String> iterable;
        private final Collection<String> collection;

        public SuperCollectionContainer(Iterable<String> iterable, Collection<String> collection) {
            this.iterable = iterable; this.collection = collection;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return doHashCode(); }

        @Override
        protected void callAbstractMethodsOnInterface() {
            callIterator(iterable);
            callIterator(collection);
        }
    }

    static final class ListContainer extends CollectionContainer {
        private final List<String> list;
        private final CopyOnWriteArrayList<String> copyOnWriteArrayList;
        private final LinkedList<String> linkedList;
        private final ArrayList<String> arrayList;
        private final Vector<String> vector;
        private final Stack<String> stack;

        public ListContainer(List<String> list, CopyOnWriteArrayList<String> copyOnWriteArrayList, LinkedList<String> linkedList,
                ArrayList<String> arrayList, Vector<String> vector, Stack<String> stack) {
            this.list = list;
            this.copyOnWriteArrayList = copyOnWriteArrayList; this.linkedList = linkedList; this.arrayList = arrayList;
            this.vector = vector; this.stack = stack;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return doHashCode(); }

        @Override
        protected void callAbstractMethodsOnInterface() {
            callIterator(list);
            callIterator(copyOnWriteArrayList, linkedList, arrayList);
            callIterator(vector, stack);
        }
    }

    static final class SetContainer extends CollectionContainer {
        private final Set<String> set;
        private final SortedSet<String> sortedSet;
        private final NavigableSet<String> navigableSet;
        private final CopyOnWriteArraySet<String> copyOnWriteArraySet;
        private final HashSet<String> hashSet;
        private final TreeSet<String> treeSet;
        private final EnumSet<TypeHelper.Enum> enumSet;

        public SetContainer(Set<String> set, SortedSet<String> sortedSet, NavigableSet<String> navigableSet,
                            CopyOnWriteArraySet<String> copyOnWriteArraySet, HashSet<String> hashSet, TreeSet<String> treeSet,
                            EnumSet<TypeHelper.Enum> enumSet) {
            this.set = set; this.sortedSet = sortedSet; this.navigableSet = navigableSet;
            this.copyOnWriteArraySet = copyOnWriteArraySet; this.hashSet = hashSet; this.treeSet = treeSet;
            this.enumSet = enumSet;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return doHashCode(); }

        @Override
        protected void callAbstractMethodsOnInterface() {
            callIterator(set, sortedSet, navigableSet);
            callIterator(copyOnWriteArraySet, hashSet, treeSet);
            callIterator(enumSet);
        }
    }

    static final class QueueContainer extends CollectionContainer {
        private final Queue<String> queue;
        private final BlockingQueue<String> blockingQueue;
        private final Deque<String> deque;
        private final BlockingDeque<String> blockingDeque;
        private final ArrayBlockingQueue<String> arrayBlockingQueue;
        private final ConcurrentLinkedQueue<String> concurrentLinkedQueue;
        private final DelayQueue<Delayed> delayQueue;
        private final LinkedBlockingQueue<String> linkedBlockingQueue;
        private final PriorityBlockingQueue<String> priorityBlockingQueue;
        private final SynchronousQueue<String> synchronousQueue;

        // CHECKSTYLE: ignore ParameterNumber for 1 line.
        public QueueContainer(Queue<String> queue, BlockingQueue<String> blockingQueue, Deque<String> deque, BlockingDeque<String> blockingDeque,
                ArrayBlockingQueue<String> arrayBlockingQueue, ConcurrentLinkedQueue<String> concurrentLinkedQueue, DelayQueue<Delayed> delayQueue,
                LinkedBlockingQueue<String> linkedBlockingQueue, PriorityBlockingQueue<String> priorityBlockingQueue,
                SynchronousQueue<String> synchronousQueue) {
            this.queue = queue; this.blockingQueue = blockingQueue; this.deque = deque; this.blockingDeque = blockingDeque;
            this.arrayBlockingQueue = arrayBlockingQueue; this.concurrentLinkedQueue = concurrentLinkedQueue; this.delayQueue = delayQueue;
            this.linkedBlockingQueue = linkedBlockingQueue; this.priorityBlockingQueue = priorityBlockingQueue;
            this.synchronousQueue = synchronousQueue;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return doHashCode(); }

        @Override
        protected void callAbstractMethodsOnInterface() {
            callIterator(queue, blockingQueue, deque, blockingDeque);
            callIterator(arrayBlockingQueue, concurrentLinkedQueue, delayQueue);
            callIterator(linkedBlockingQueue, priorityBlockingQueue);
            callIterator(synchronousQueue);
        }
    }

    static final class MapContainer extends CollectionContainer {
        private final Map<String, String> map;
        private final SortedMap<String, String> sortedMap;
        private final NavigableMap<String, String> navigableMap;
        private final ConcurrentNavigableMap<String, String> concurrentNavigableMap;
        private final ConcurrentHashMap<String, String> concurrentHashMap;
        private final HashMap<String, String> hashMap;
        private final Hashtable<String, String> hashtable;
        private final LinkedHashMap<String, String> linkedHashMap;
        private final Properties properties;
        private final TreeMap<String, String> treeMap;
        private final WeakHashMap<String, String> weakHashMap;
        private final EnumMap<TypeHelper.Enum, String> enumMap;

        // CHECKSTYLE: ignore ParameterNumber for 1 line.
        public MapContainer(Map<String, String> map, SortedMap<String, String> sortedMap, NavigableMap<String, String> navigableMap,
                ConcurrentNavigableMap<String, String> concurrentNavigableMap, ConcurrentHashMap<String, String> concurrentHashMap,
                HashMap<String, String> hashMap, Hashtable<String, String> hashtable, LinkedHashMap<String, String> linkedHashMap,
                Properties properties, TreeMap<String, String> treeMap, WeakHashMap<String, String> weakHashMap,
                EnumMap<TypeHelper.Enum, String> enumMap) {
            this.map = map; this.sortedMap = sortedMap; this.navigableMap = navigableMap;
            this.concurrentNavigableMap = concurrentNavigableMap; this.concurrentHashMap = concurrentHashMap;
            this.hashMap = hashMap; this.hashtable = hashtable; this.linkedHashMap = linkedHashMap;
            this.properties = properties; this.treeMap = treeMap; this.weakHashMap = weakHashMap;
            this.enumMap = enumMap;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return doHashCode(); }

        @Override
        protected void callAbstractMethodsOnInterface() {
            callKeySet(map, sortedMap, navigableMap, concurrentNavigableMap);
            callKeySet(concurrentNavigableMap, concurrentHashMap);
            callKeySet(hashMap, hashtable, linkedHashMap);
            callKeySet(properties, treeMap, weakHashMap);
            callKeySet(enumMap);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class CommonClassesContainer {
        private final String string;
        private final Integer integer;
        private final Class<?> type;
        private final BitSet bitset;
        private final Calendar calendar;
        private final Date date;
        private final File file;
        private final GregorianCalendar gregorianCalendar;
        private final Pattern pattern;
        private final SimpleDateFormat simpleDateFormat;
        private final UUID uuid;
        private final InetAddress inetAddress;
        private final Thread thread;
        private final java.awt.color.ColorSpace awtColorSpace;
        private final java.awt.color.ICC_ColorSpace iccColorSpace;
        private final java.awt.color.ICC_Profile iccProfile;

        // CHECKSTYLE: ignore ParameterNumber for 1 line.
        public CommonClassesContainer(String string, Integer integer, Class<?> type, BitSet bitset,
                Calendar calendar, Date date, File file, GregorianCalendar gregorianCalendar, Pattern pattern,
                SimpleDateFormat simpleDateFormat, UUID uuid, InetAddress inetAddress, Thread thread,
                java.awt.color.ColorSpace awtColorSpace, java.awt.color.ICC_ColorSpace iccColorSpace, java.awt.color.ICC_Profile iccProfile) {
            this.string = string; this.integer = integer; this.type = type; this.bitset = bitset;
            this.calendar = calendar; this.date = date; this.file = file; this.gregorianCalendar = gregorianCalendar; this.pattern = pattern;
            this.simpleDateFormat = simpleDateFormat; this.uuid = uuid; this.inetAddress = inetAddress; this.thread = thread;
            this.awtColorSpace = awtColorSpace; this.iccColorSpace = iccColorSpace; this.iccProfile = iccProfile;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class ThreadLocalContainer {
        public static final ThreadLocal<Integer> RED_INSTANCE = createInstance(10);
        private final ThreadLocal<Integer> instance = RED_INSTANCE;

        private static ThreadLocal<Integer> createInstance(final int initialValue) {
            return new ThreadLocal<Integer>() {
                @Override
                protected Integer initialValue() {
                    return initialValue;
                }
            };
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreadLocalContainer)) {
                return false;
            }
            ThreadLocalContainer other = (ThreadLocalContainer)obj;
            if (instance == null) {
                return other.instance == null;
            }
            if (other.instance == null) {
                return false;
            }
            if (instance.get() == null) {
                return other.instance.get() == null;
            }
            return instance.get().equals(other.instance.get());
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
