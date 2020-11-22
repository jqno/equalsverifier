package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: ParameterNumber

public class JavaApiClassesTest {
    @Test
    public void succeed_whenClassContainsASuperCollection() {
        EqualsVerifier.forClass(SuperCollectionContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsAList() {
        EqualsVerifier.forClass(ListContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsASet() {
        EqualsVerifier.forClass(SetContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsAQueue() {
        EqualsVerifier.forClass(QueueContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsAMap() {
        EqualsVerifier.forClass(MapContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsANioBuffer() {
        EqualsVerifier.forClass(NioBufferContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsACommonJavaApiClass() {
        EqualsVerifier.forClass(CommonClassesContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsReflectionApiClass() {
        EqualsVerifier.forClass(ReflectionClassesContainer.class)
                // Because java.lang.reflect.Constructor's hashCode() is unhelpful
                .suppress(Warning.STRICT_HASHCODE)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsACommonJava8ApiClass() {
        EqualsVerifier.forClass(Java8ApiClassesContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsAnAtomicValue() {
        EqualsVerifier.forClass(AtomicClassesContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsAnAncientJavaApiClass() {
        EqualsVerifier.forClass(AncientJavaApiClassesContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsAThreadLocalField() {
        EqualsVerifier.forClass(ThreadLocalContainer.class).verify();
    }

    @Test
    public void succeed_whenClassContainsStringBuilderThatCallsToStringInEquals() {
        EqualsVerifier.forClass(StringBuilderContainer.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenClassContainsClassesButDoesntUseThemInEquals() {
        EqualsVerifier.forClass(UnusedInEqualsButPresentInClassContainer.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.ALL_FIELDS_SHOULD_BE_USED)
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
            this.iterable = iterable;
            this.collection = collection;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return doHashCode();
        }

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

        public ListContainer(
                List<String> list,
                CopyOnWriteArrayList<String> copyOnWriteArrayList,
                LinkedList<String> linkedList,
                ArrayList<String> arrayList,
                Vector<String> vector,
                Stack<String> stack) {
            this.list = list;
            this.copyOnWriteArrayList = copyOnWriteArrayList;
            this.linkedList = linkedList;
            this.arrayList = arrayList;
            this.vector = vector;
            this.stack = stack;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return doHashCode();
        }

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

        public SetContainer(
                Set<String> set,
                SortedSet<String> sortedSet,
                NavigableSet<String> navigableSet,
                CopyOnWriteArraySet<String> copyOnWriteArraySet,
                HashSet<String> hashSet,
                TreeSet<String> treeSet,
                EnumSet<TypeHelper.Enum> enumSet) {
            this.set = set;
            this.sortedSet = sortedSet;
            this.navigableSet = navigableSet;
            this.copyOnWriteArraySet = copyOnWriteArraySet;
            this.hashSet = hashSet;
            this.treeSet = treeSet;
            this.enumSet = enumSet;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return doHashCode();
        }

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

        public QueueContainer(
                Queue<String> queue,
                BlockingQueue<String> blockingQueue,
                Deque<String> deque,
                BlockingDeque<String> blockingDeque,
                ArrayBlockingQueue<String> arrayBlockingQueue,
                ConcurrentLinkedQueue<String> concurrentLinkedQueue,
                DelayQueue<Delayed> delayQueue,
                LinkedBlockingQueue<String> linkedBlockingQueue,
                PriorityBlockingQueue<String> priorityBlockingQueue,
                SynchronousQueue<String> synchronousQueue) {
            this.queue = queue;
            this.blockingQueue = blockingQueue;
            this.deque = deque;
            this.blockingDeque = blockingDeque;
            this.arrayBlockingQueue = arrayBlockingQueue;
            this.concurrentLinkedQueue = concurrentLinkedQueue;
            this.delayQueue = delayQueue;
            this.linkedBlockingQueue = linkedBlockingQueue;
            this.priorityBlockingQueue = priorityBlockingQueue;
            this.synchronousQueue = synchronousQueue;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return doHashCode();
        }

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

        public MapContainer(
                Map<String, String> map,
                SortedMap<String, String> sortedMap,
                NavigableMap<String, String> navigableMap,
                ConcurrentNavigableMap<String, String> concurrentNavigableMap,
                ConcurrentHashMap<String, String> concurrentHashMap,
                HashMap<String, String> hashMap,
                Hashtable<String, String> hashtable,
                LinkedHashMap<String, String> linkedHashMap,
                Properties properties,
                TreeMap<String, String> treeMap,
                WeakHashMap<String, String> weakHashMap,
                EnumMap<TypeHelper.Enum, String> enumMap) {
            this.map = map;
            this.sortedMap = sortedMap;
            this.navigableMap = navigableMap;
            this.concurrentNavigableMap = concurrentNavigableMap;
            this.concurrentHashMap = concurrentHashMap;
            this.hashMap = hashMap;
            this.hashtable = hashtable;
            this.linkedHashMap = linkedHashMap;
            this.properties = properties;
            this.treeMap = treeMap;
            this.weakHashMap = weakHashMap;
            this.enumMap = enumMap;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return doHashCode();
        }

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
    static final class NioBufferContainer {
        private final Buffer buffer;
        private final ByteBuffer byteBuffer;
        private final CharBuffer charBuffer;
        private final DoubleBuffer doubleBuffer;
        private final FloatBuffer floatBuffer;
        private final IntBuffer intBuffer;
        private final LongBuffer longBuffer;
        private final ShortBuffer shortBuffer;

        public NioBufferContainer(
                Buffer buffer,
                ByteBuffer byteBuffer,
                CharBuffer charBuffer,
                DoubleBuffer doubleBuffer,
                FloatBuffer floatBuffer,
                IntBuffer intBuffer,
                LongBuffer longBuffer,
                ShortBuffer shortBuffer) {
            this.buffer = buffer;
            this.byteBuffer = byteBuffer;
            this.charBuffer = charBuffer;
            this.doubleBuffer = doubleBuffer;
            this.floatBuffer = floatBuffer;
            this.intBuffer = intBuffer;
            this.longBuffer = longBuffer;
            this.shortBuffer = shortBuffer;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class CommonClassesContainer {
        private final String string;
        private final Integer integer;
        private final BitSet bitset;
        private final Calendar calendar;
        private final Date date;
        private final File file;
        private final GregorianCalendar gregorianCalendar;
        private final Pattern pattern;
        private final SimpleDateFormat simpleDateFormat;
        private final URI uri;
        private final URL url;
        private final UUID uuid;
        private final InetAddress inetAddress;
        private final Inet4Address inet4Address;
        private final Inet6Address inet6Address;
        private final InetSocketAddress inetSocketAddress;
        private final Thread thread;
        private final java.sql.Date sqlDate;
        private final java.sql.Time sqlTime;
        private final java.sql.Timestamp sqlTimestamp;
        private final Currency currency;
        private final EventObject eventObject;

        public CommonClassesContainer(
                String string,
                Integer integer,
                BitSet bitset,
                Calendar calendar,
                Date date,
                File file,
                GregorianCalendar gregorianCalendar,
                Pattern pattern,
                SimpleDateFormat simpleDateFormat,
                URI uri,
                URL url,
                UUID uuid,
                InetAddress inetAddress,
                Inet4Address inet4Address,
                Inet6Address inet6Address,
                InetSocketAddress inetSocketAddress,
                Thread thread,
                java.sql.Date sqlDate,
                java.sql.Time sqlTime,
                java.sql.Timestamp sqlTimestamp,
                Currency currency,
                EventObject eventObject) {
            this.string = string;
            this.integer = integer;
            this.bitset = bitset;
            this.calendar = calendar;
            this.date = date;
            this.file = file;
            this.gregorianCalendar = gregorianCalendar;
            this.pattern = pattern;
            this.simpleDateFormat = simpleDateFormat;
            this.uri = uri;
            this.url = url;
            this.uuid = uuid;
            this.inetAddress = inetAddress;
            this.inet4Address = inet4Address;
            this.inet6Address = inet6Address;
            this.inetSocketAddress = inetSocketAddress;
            this.thread = thread;
            this.sqlDate = sqlDate;
            this.sqlTime = sqlTime;
            this.sqlTimestamp = sqlTimestamp;
            this.currency = currency;
            this.eventObject = eventObject;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class Java8ApiClassesContainer {
        private final Optional<?> optional;
        private final OptionalDouble optionalDouble;
        private final OptionalInt optionalInt;
        private final OptionalLong optionalLong;
        private final LocalDate localDate;
        private final LocalTime localTime;
        private final LocalDateTime localDateTime;
        private final ZoneId zoneId;
        private final ZoneOffset zoneOffset;
        private final ZonedDateTime zonedDateTime;
        private final DateTimeFormatter dateTimeFormatter;
        private final CompletableFuture<?> completableFuture;
        private final StampedLock stampedLock;
        private final Supplier<?> supplier;
        private final Duration duration;
        private final Instant instant;
        private final MonthDay monthDay;
        private final OffsetDateTime offsetDateTime;
        private final OffsetTime offsetTime;
        private final Period period;
        private final Year year;
        private final YearMonth yearMonth;

        public Java8ApiClassesContainer(
                Optional<?> optional,
                OptionalDouble optionalDouble,
                OptionalInt optionalInt,
                OptionalLong optionalLong,
                LocalDate localDate,
                LocalTime localTime,
                LocalDateTime localDateTime,
                ZoneId zoneId,
                ZoneOffset zoneOffset,
                ZonedDateTime zonedDateTime,
                DateTimeFormatter dateTimeFormatter,
                CompletableFuture<?> completableFuture,
                StampedLock stampedLock,
                Supplier<?> supplier,
                Duration duration,
                Instant instant,
                MonthDay monthDay,
                OffsetDateTime offsetDateTime,
                OffsetTime offsetTime,
                Period period,
                Year year,
                YearMonth yearMonth) {
            this.optional = optional;
            this.optionalDouble = optionalDouble;
            this.optionalInt = optionalInt;
            this.optionalLong = optionalLong;
            this.localDate = localDate;
            this.localTime = localTime;
            this.localDateTime = localDateTime;
            this.zoneId = zoneId;
            this.zoneOffset = zoneOffset;
            this.zonedDateTime = zonedDateTime;
            this.dateTimeFormatter = dateTimeFormatter;
            this.completableFuture = completableFuture;
            this.stampedLock = stampedLock;
            this.supplier = supplier;
            this.duration = duration;
            this.instant = instant;
            this.monthDay = monthDay;
            this.offsetDateTime = offsetDateTime;
            this.offsetTime = offsetTime;
            this.period = period;
            this.year = year;
            this.yearMonth = yearMonth;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class ReflectionClassesContainer {
        private final Class<?> type;
        private final Method method;
        private final Field field;
        private final Constructor<?> constructor;

        public ReflectionClassesContainer(
                Class<?> type, Method method, Field field, Constructor<?> constructor) {
            this.type = type;
            this.method = method;
            this.field = field;
            this.constructor = constructor;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class AtomicClassesContainer {
        private final AtomicBoolean atomicBoolean;
        private final AtomicInteger atomicInteger;
        private final AtomicIntegerArray atomicIntegerArray;
        private final AtomicLong atomicLong;
        private final AtomicLongArray atomicLongArray;
        private final AtomicMarkableReference<?> atomicMarkableReference;
        private final AtomicReference<?> atomicReference;
        private final AtomicReferenceArray<?> atomicReferenceArray;
        private final AtomicStampedReference<?> atomicStampedReference;

        public AtomicClassesContainer(
                AtomicBoolean atomicBoolean,
                AtomicInteger atomicInteger,
                AtomicIntegerArray atomicIntegerArray,
                AtomicLong atomicLong,
                AtomicLongArray atomicLongArray,
                AtomicMarkableReference<?> atomicMarkableReference,
                AtomicReference<?> atomicReference,
                AtomicReferenceArray<?> atomicReferenceArray,
                AtomicStampedReference<?> atomicStampedReference) {
            this.atomicBoolean = atomicBoolean;
            this.atomicInteger = atomicInteger;
            this.atomicIntegerArray = atomicIntegerArray;
            this.atomicLong = atomicLong;
            this.atomicLongArray = atomicLongArray;
            this.atomicMarkableReference = atomicMarkableReference;
            this.atomicReference = atomicReference;
            this.atomicReferenceArray = atomicReferenceArray;
            this.atomicStampedReference = atomicStampedReference;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class AncientJavaApiClassesContainer {
        private final java.awt.color.ColorSpace awtColorSpace;
        private final java.awt.color.ICC_ColorSpace iccColorSpace;
        private final java.awt.color.ICC_Profile iccProfile;
        private final java.awt.Font font;
        private final java.rmi.dgc.VMID vmid;
        private final java.rmi.server.UID uid;

        public AncientJavaApiClassesContainer(
                java.awt.color.ColorSpace awtColorSpace,
                java.awt.color.ICC_ColorSpace iccColorSpace,
                java.awt.color.ICC_Profile iccProfile,
                java.awt.Font font,
                java.rmi.dgc.VMID vmid,
                java.rmi.server.UID uid) {
            this.awtColorSpace = awtColorSpace;
            this.iccColorSpace = iccColorSpace;
            this.iccProfile = iccProfile;
            this.font = font;
            this.vmid = vmid;
            this.uid = uid;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
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
            ThreadLocalContainer other = (ThreadLocalContainer) obj;
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

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class StringBuilderContainer {
        private final StringBuilder stringBuilder;

        public StringBuilderContainer(StringBuilder stringBuilder) {
            this.stringBuilder = stringBuilder;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StringBuilderContainer)) {
                return false;
            }
            StringBuilderContainer other = (StringBuilderContainer) obj;
            return Objects.equals(stringBuilder.toString(), other.stringBuilder.toString());
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class UnusedInEqualsButPresentInClassContainer {
        private final PropertyChangeSupport pcs;

        public UnusedInEqualsButPresentInClassContainer(PropertyChangeSupport pcs) {
            this.pcs = pcs;
        }
    }
}
