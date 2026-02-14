package nl.jqno.equalsverifier.integration.extended_contract;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.*;
import java.nio.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: ExecutableStatementCount
// CHECKSTYLE OFF: ParameterNumber
// CHECKSTYLE OFF: CyclomaticComplexity

@SuppressWarnings("UndefinedEquals")
class JavaApiClassesTest {

    @Test
    void succeed_whenClassContainsASuperCollection() {
        EqualsVerifier.forClass(SuperCollectionContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsAList() {
        EqualsVerifier.forClass(ListContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsASet() {
        EqualsVerifier.forClass(SetContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsAQueue() {
        EqualsVerifier.forClass(QueueContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsAMap() {
        EqualsVerifier.forClass(MapContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsANioBuffer() {
        EqualsVerifier.forClass(NioBufferContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsACommonJavaApiClass() {
        EqualsVerifier.forClass(CommonClassesContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsExceptions() {
        EqualsVerifier.forClass(ExceptionsContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsReflectionApiClass() {
        EqualsVerifier
                .forClass(ReflectionClassesContainer.class)
                // Because java.lang.reflect.Constructor's hashCode() is unhelpful
                .suppress(Warning.STRICT_HASHCODE)
                .verify();
    }

    @Test
    void succeed_whenClassContainsACommonJava8ApiClass() {
        EqualsVerifier.forClass(Java8ApiClassesContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsAnAtomicValue() {
        EqualsVerifier.forClass(AtomicClassesContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsAnAncientJavaApiClass() {
        EqualsVerifier.forClass(AncientJavaApiClassesContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsAThreadLocalField() {
        EqualsVerifier.forClass(ThreadLocalContainer.class).verify();
    }

    @Test
    void succeed_whenClassContainsStringBuilderThatCallsToStringInEquals() {
        EqualsVerifier.forClass(StringBuilderContainer.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void succeed_whenClassContainsClassesButDoesntUseThemInEquals() {
        EqualsVerifier
                .forClass(UnusedInEqualsButPresentInClassContainer.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    static final class SuperCollectionContainer {

        private final Iterable<String> iterable;
        private final Collection<String> collection;

        public SuperCollectionContainer(Iterable<String> iterable, Collection<String> collection) {
            this.iterable = iterable;
            this.collection = collection;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SuperCollectionContainer)) {
                return false;
            }
            SuperCollectionContainer other = (SuperCollectionContainer) obj;
            return Objects.equals(iterable, other.iterable) && Objects.equals(collection, other.collection);
        }

        @Override
        public int hashCode() {
            callIterator(iterable);
            callIterator(collection);
            return Objects.hash(iterable, collection);
        }
    }

    @SuppressWarnings("NonApiType")
    static final class ListContainer {

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
            if (!(obj instanceof ListContainer)) {
                return false;
            }
            ListContainer other = (ListContainer) obj;
            return Objects.equals(list, other.list)
                    && Objects.equals(copyOnWriteArrayList, other.copyOnWriteArrayList)
                    && Objects.equals(linkedList, other.linkedList)
                    && Objects.equals(arrayList, other.arrayList)
                    && Objects.equals(vector, other.vector)
                    && Objects.equals(stack, other.stack);
        }

        @Override
        public int hashCode() {
            callIterator(list);
            callIterator(copyOnWriteArrayList, linkedList, arrayList);
            callIterator(vector, stack);
            return Objects.hash(list, copyOnWriteArrayList, linkedList, arrayList, vector, stack);
        }
    }

    @SuppressWarnings("NonApiType")
    static final class SetContainer {

        private final Set<String> set;
        private final SortedSet<String> sortedSet;
        private final NavigableSet<String> navigableSet;
        private final CopyOnWriteArraySet<String> copyOnWriteArraySet;
        private final HashSet<String> hashSet;
        private final TreeSet<String> treeSet;
        private final EnumSet<TypeHelper.SimpleEnum> enumSet;

        public SetContainer(
                Set<String> set,
                SortedSet<String> sortedSet,
                NavigableSet<String> navigableSet,
                CopyOnWriteArraySet<String> copyOnWriteArraySet,
                HashSet<String> hashSet,
                TreeSet<String> treeSet,
                EnumSet<TypeHelper.SimpleEnum> enumSet) {
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
            if (!(obj instanceof SetContainer)) {
                return false;
            }
            SetContainer other = (SetContainer) obj;
            return Objects.equals(set, other.set)
                    && Objects.equals(sortedSet, other.sortedSet)
                    && Objects.equals(navigableSet, other.navigableSet)
                    && Objects.equals(copyOnWriteArraySet, other.copyOnWriteArraySet)
                    && Objects.equals(hashSet, other.hashSet)
                    && Objects.equals(treeSet, other.treeSet)
                    && Objects.equals(enumSet, other.enumSet);
        }

        @Override
        public int hashCode() {
            callIterator(set, sortedSet, navigableSet);
            callIterator(copyOnWriteArraySet, hashSet, treeSet);
            callIterator(enumSet);
            return Objects.hash(set, sortedSet, navigableSet, copyOnWriteArraySet, hashSet, treeSet, enumSet);
        }
    }

    static final class QueueContainer {

        private final ArrayBlockingQueue<String> arrayBlockingQueue;
        private final ArrayDeque<String> arrayDeque;
        private final BlockingDeque<String> blockingDeque;
        private final BlockingQueue<String> blockingQueue;
        private final ConcurrentLinkedDeque<String> concurrentLinkedDeque;
        private final ConcurrentLinkedQueue<String> concurrentLinkedQueue;
        private final DelayQueue<Delayed> delayQueue;
        private final Deque<String> deque;
        private final LinkedBlockingQueue<String> linkedBlockingQueue;
        private final LinkedTransferQueue<String> linkedTransferQueue;
        private final PriorityBlockingQueue<String> priorityBlockingQueue;
        private final PriorityQueue<String> priorityQueue;
        private final Queue<String> queue;
        private final SynchronousQueue<String> synchronousQueue;

        public QueueContainer(
                ArrayBlockingQueue<String> arrayBlockingQueue,
                ArrayDeque<String> arrayDeque,
                BlockingDeque<String> blockingDeque,
                BlockingQueue<String> blockingQueue,
                ConcurrentLinkedDeque<String> concurrentLinkedDeque,
                ConcurrentLinkedQueue<String> concurrentLinkedQueue,
                DelayQueue<Delayed> delayQueue,
                Deque<String> deque,
                LinkedBlockingQueue<String> linkedBlockingQueue,
                LinkedTransferQueue<String> linkedTransferQueue,
                PriorityBlockingQueue<String> priorityBlockingQueue,
                PriorityQueue<String> priorityQueue,
                Queue<String> queue,
                SynchronousQueue<String> synchronousQueue) {
            this.arrayBlockingQueue = arrayBlockingQueue;
            this.arrayDeque = arrayDeque;
            this.blockingDeque = blockingDeque;
            this.blockingQueue = blockingQueue;
            this.concurrentLinkedDeque = concurrentLinkedDeque;
            this.concurrentLinkedQueue = concurrentLinkedQueue;
            this.delayQueue = delayQueue;
            this.deque = deque;
            this.linkedBlockingQueue = linkedBlockingQueue;
            this.linkedTransferQueue = linkedTransferQueue;
            this.priorityBlockingQueue = priorityBlockingQueue;
            this.priorityQueue = priorityQueue;
            this.queue = queue;
            this.synchronousQueue = synchronousQueue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof QueueContainer)) {
                return false;
            }
            QueueContainer other = (QueueContainer) obj;
            return Objects.equals(arrayBlockingQueue, other.arrayBlockingQueue)
                    && Objects.equals(arrayDeque, other.arrayDeque)
                    && Objects.equals(blockingDeque, other.blockingDeque)
                    && Objects.equals(blockingQueue, other.blockingQueue)
                    && Objects.equals(concurrentLinkedDeque, other.concurrentLinkedDeque)
                    && Objects.equals(concurrentLinkedQueue, other.concurrentLinkedQueue)
                    && Objects.equals(delayQueue, other.delayQueue)
                    && Objects.equals(deque, other.deque)
                    && Objects.equals(linkedBlockingQueue, other.linkedBlockingQueue)
                    && Objects.equals(linkedTransferQueue, other.linkedTransferQueue)
                    && Objects.equals(priorityBlockingQueue, other.priorityBlockingQueue)
                    && Objects.equals(priorityQueue, other.priorityQueue)
                    && Objects.equals(queue, other.queue)
                    && Objects.equals(synchronousQueue, other.synchronousQueue);
        }

        @Override
        public int hashCode() {
            callIterator(queue, blockingQueue, deque, blockingDeque);
            callIterator(arrayBlockingQueue, concurrentLinkedQueue, delayQueue);
            callIterator(linkedBlockingQueue, priorityBlockingQueue);
            callIterator(synchronousQueue);
            return Objects
                    .hash(
                        arrayBlockingQueue,
                        arrayDeque,
                        blockingDeque,
                        blockingQueue,
                        concurrentLinkedDeque,
                        concurrentLinkedQueue,
                        delayQueue,
                        deque,
                        linkedBlockingQueue,
                        linkedTransferQueue,
                        priorityBlockingQueue,
                        priorityQueue,
                        queue,
                        synchronousQueue);
        }
    }

    @SuppressWarnings("NonApiType")
    static final class MapContainer {

        private final Map<String, String> map;
        private final SortedMap<String, String> sortedMap;
        private final NavigableMap<String, String> navigableMap;
        private final ConcurrentNavigableMap<String, String> concurrentNavigableMap;
        private final ConcurrentSkipListMap<String, String> concurrentSkipListMap;
        private final ConcurrentHashMap<String, String> concurrentHashMap;
        private final HashMap<String, String> hashMap;
        private final Hashtable<String, String> hashtable;
        private final LinkedHashMap<String, String> linkedHashMap;
        private final Properties properties;
        private final TreeMap<String, String> treeMap;
        private final WeakHashMap<String, String> weakHashMap;
        private final EnumMap<TypeHelper.SimpleEnum, String> enumMap;

        public MapContainer(
                Map<String, String> map,
                SortedMap<String, String> sortedMap,
                NavigableMap<String, String> navigableMap,
                ConcurrentNavigableMap<String, String> concurrentNavigableMap,
                ConcurrentSkipListMap<String, String> concurrentSkipListMap,
                ConcurrentHashMap<String, String> concurrentHashMap,
                HashMap<String, String> hashMap,
                Hashtable<String, String> hashtable,
                LinkedHashMap<String, String> linkedHashMap,
                Properties properties,
                TreeMap<String, String> treeMap,
                WeakHashMap<String, String> weakHashMap,
                EnumMap<TypeHelper.SimpleEnum, String> enumMap) {
            this.map = map;
            this.sortedMap = sortedMap;
            this.navigableMap = navigableMap;
            this.concurrentNavigableMap = concurrentNavigableMap;
            this.concurrentSkipListMap = concurrentSkipListMap;
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
            if (!(obj instanceof MapContainer)) {
                return false;
            }
            MapContainer other = (MapContainer) obj;
            return Objects.equals(map, other.map)
                    && Objects.equals(sortedMap, other.sortedMap)
                    && Objects.equals(navigableMap, other.navigableMap)
                    && Objects.equals(concurrentNavigableMap, other.concurrentNavigableMap)
                    && Objects.equals(concurrentSkipListMap, other.concurrentSkipListMap)
                    && Objects.equals(concurrentHashMap, other.concurrentHashMap)
                    && Objects.equals(hashMap, other.hashMap)
                    && Objects.equals(hashtable, other.hashtable)
                    && Objects.equals(linkedHashMap, other.linkedHashMap)
                    && Objects.equals(properties, other.properties)
                    && Objects.equals(treeMap, other.treeMap)
                    && Objects.equals(weakHashMap, other.weakHashMap)
                    && Objects.equals(enumMap, other.enumMap);
        }

        @Override
        public int hashCode() {
            callKeySet(map, sortedMap, navigableMap, concurrentNavigableMap);
            callKeySet(concurrentNavigableMap, concurrentHashMap);
            callKeySet(hashMap, hashtable, linkedHashMap);
            callKeySet(properties, treeMap, weakHashMap);
            callKeySet(enumMap);
            return Objects
                    .hash(
                        map,
                        sortedMap,
                        navigableMap,
                        concurrentNavigableMap,
                        concurrentSkipListMap,
                        concurrentHashMap,
                        hashMap,
                        hashtable,
                        linkedHashMap,
                        properties,
                        treeMap,
                        weakHashMap,
                        enumMap);
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
            if (!(obj instanceof NioBufferContainer)) {
                return false;
            }
            NioBufferContainer other = (NioBufferContainer) obj;
            return Objects.equals(buffer, other.buffer)
                    && Objects.equals(byteBuffer, other.byteBuffer)
                    && Objects.equals(charBuffer, other.charBuffer)
                    && Objects.equals(doubleBuffer, other.doubleBuffer)
                    && Objects.equals(floatBuffer, other.floatBuffer)
                    && Objects.equals(intBuffer, other.intBuffer)
                    && Objects.equals(longBuffer, other.longBuffer)
                    && Objects.equals(shortBuffer, other.shortBuffer);
        }

        @Override
        public int hashCode() {
            return Objects
                    .hash(
                        buffer,
                        byteBuffer,
                        charBuffer,
                        doubleBuffer,
                        floatBuffer,
                        intBuffer,
                        longBuffer,
                        shortBuffer);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class CommonClassesContainer {

        private final Object object;
        private final String string;
        private final Number number;
        private final Integer integer;
        private final BitSet bitset;
        private final Calendar calendar;
        private final Date date;
        private final File file;
        private final GregorianCalendar gregorianCalendar;
        private final Pattern pattern;
        private final DecimalFormat decimalFormat;
        private final NumberFormat numberFormat;
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
        private final Formatter formatter;
        private final Locale locale;
        private final Scanner scanner;
        private final Charset charset;
        private final Semaphore semaphore;
        private final ReentrantLock reentrantLock;
        private final HexFormat hexFormat;
        private final PrintStream printStream;
        private final BigInteger bigInteger;
        private final TimeZone timeZone;

        public CommonClassesContainer(
                Object object,
                String string,
                Number number,
                Integer integer,
                BitSet bitset,
                Calendar calendar,
                Date date,
                File file,
                GregorianCalendar gregorianCalendar,
                Pattern pattern,
                DecimalFormat decimalFormat,
                NumberFormat numberFormat,
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
                EventObject eventObject,
                Formatter formatter,
                Locale locale,
                Scanner scanner,
                Charset charset,
                Semaphore semaphore,
                ReentrantLock reentrantLock,
                HexFormat hexFormat,
                PrintStream printStream,
                BigInteger bigInteger,
                TimeZone timeZone) {
            this.object = object;
            this.string = string;
            this.number = number;
            this.integer = integer;
            this.bitset = bitset;
            this.calendar = calendar;
            this.date = date;
            this.file = file;
            this.gregorianCalendar = gregorianCalendar;
            this.pattern = pattern;
            this.decimalFormat = decimalFormat;
            this.numberFormat = numberFormat;
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
            this.formatter = formatter;
            this.locale = locale;
            this.scanner = scanner;
            this.charset = charset;
            this.semaphore = semaphore;
            this.reentrantLock = reentrantLock;
            this.hexFormat = hexFormat;
            this.printStream = printStream;
            this.bigInteger = bigInteger;
            this.timeZone = timeZone;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CommonClassesContainer)) {
                return false;
            }
            CommonClassesContainer other = (CommonClassesContainer) obj;
            return Objects.equals(object, other.object)
                    && Objects.equals(string, other.string)
                    && Objects.equals(number, other.number)
                    && Objects.equals(integer, other.integer)
                    && Objects.equals(bitset, other.bitset)
                    && Objects.equals(calendar, other.calendar)
                    && Objects.equals(date, other.date)
                    && Objects.equals(file, other.file)
                    && Objects.equals(gregorianCalendar, other.gregorianCalendar)
                    && Objects.equals(pattern, other.pattern)
                    && Objects.equals(decimalFormat, other.decimalFormat)
                    && Objects.equals(numberFormat, other.numberFormat)
                    && Objects.equals(simpleDateFormat, other.simpleDateFormat)
                    && Objects.equals(uri, other.uri)
                    && Objects.equals(url, other.url)
                    && Objects.equals(uuid, other.uuid)
                    && Objects.equals(inetAddress, other.inetAddress)
                    && Objects.equals(inet4Address, other.inet4Address)
                    && Objects.equals(inet6Address, other.inet6Address)
                    && Objects.equals(inetSocketAddress, other.inetSocketAddress)
                    && Objects.equals(thread, other.thread)
                    && Objects.equals(sqlDate, other.sqlDate)
                    && Objects.equals(sqlTime, other.sqlTime)
                    && Objects.equals(sqlTimestamp, other.sqlTimestamp)
                    && Objects.equals(currency, other.currency)
                    && Objects.equals(eventObject, other.eventObject)
                    && Objects.equals(formatter, other.formatter)
                    && Objects.equals(locale, other.locale)
                    && Objects.equals(scanner, other.scanner)
                    && Objects.equals(charset, other.charset)
                    && Objects.equals(semaphore, other.semaphore)
                    && Objects.equals(reentrantLock, other.reentrantLock)
                    && Objects.equals(hexFormat, other.hexFormat)
                    && Objects.equals(printStream, other.printStream)
                    && Objects.equals(bigInteger, other.bigInteger)
                    && Objects.equals(timeZone, other.timeZone);
        }

        @Override
        public int hashCode() {
            return Objects
                    .hash(
                        object,
                        string,
                        number,
                        integer,
                        bitset,
                        calendar,
                        date,
                        file,
                        gregorianCalendar,
                        pattern,
                        decimalFormat,
                        numberFormat,
                        simpleDateFormat,
                        uri,
                        url,
                        uuid,
                        inetAddress,
                        inet4Address,
                        inet6Address,
                        inetSocketAddress,
                        thread,
                        sqlDate,
                        sqlTime,
                        sqlTimestamp,
                        currency,
                        eventObject,
                        formatter,
                        locale,
                        scanner,
                        charset,
                        semaphore,
                        reentrantLock,
                        hexFormat,
                        printStream,
                        bigInteger,
                        timeZone);
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
        private final Clock clock;
        private final Duration duration;
        private final Instant instant;
        private final MonthDay monthDay;
        private final OffsetDateTime offsetDateTime;
        private final OffsetTime offsetTime;
        private final Period period;
        private final Year year;
        private final YearMonth yearMonth;
        private final DoubleSummaryStatistics doubleSummaryStatistics;
        private final IntSummaryStatistics intSummaryStatistics;
        private final LongSummaryStatistics longSummaryStatistics;

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
                Clock clock,
                Duration duration,
                Instant instant,
                MonthDay monthDay,
                OffsetDateTime offsetDateTime,
                OffsetTime offsetTime,
                Period period,
                Year year,
                YearMonth yearMonth,
                DoubleSummaryStatistics doubleSummaryStatistics,
                IntSummaryStatistics intSummaryStatistics,
                LongSummaryStatistics longSummaryStatistics) {
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
            this.clock = clock;
            this.duration = duration;
            this.instant = instant;
            this.monthDay = monthDay;
            this.offsetDateTime = offsetDateTime;
            this.offsetTime = offsetTime;
            this.period = period;
            this.year = year;
            this.yearMonth = yearMonth;
            this.doubleSummaryStatistics = doubleSummaryStatistics;
            this.intSummaryStatistics = intSummaryStatistics;
            this.longSummaryStatistics = longSummaryStatistics;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Java8ApiClassesContainer)) {
                return false;
            }
            Java8ApiClassesContainer other = (Java8ApiClassesContainer) obj;
            return Objects.equals(optional, other.optional)
                    && Objects.equals(optionalDouble, other.optionalDouble)
                    && Objects.equals(optionalInt, other.optionalInt)
                    && Objects.equals(optionalLong, other.optionalLong)
                    && Objects.equals(localDate, other.localDate)
                    && Objects.equals(localTime, other.localTime)
                    && Objects.equals(localDateTime, other.localDateTime)
                    && Objects.equals(zoneId, other.zoneId)
                    && Objects.equals(zoneOffset, other.zoneOffset)
                    && Objects.equals(zonedDateTime, other.zonedDateTime)
                    && Objects.equals(dateTimeFormatter, other.dateTimeFormatter)
                    && Objects.equals(completableFuture, other.completableFuture)
                    && Objects.equals(stampedLock, other.stampedLock)
                    && Objects.equals(supplier, other.supplier)
                    && Objects.equals(clock, other.clock)
                    && Objects.equals(duration, other.duration)
                    && Objects.equals(instant, other.instant)
                    && Objects.equals(monthDay, other.monthDay)
                    && Objects.equals(offsetDateTime, other.offsetDateTime)
                    && Objects.equals(offsetTime, other.offsetTime)
                    && Objects.equals(period, other.period)
                    && Objects.equals(year, other.year)
                    && Objects.equals(yearMonth, other.yearMonth)
                    && Objects.equals(doubleSummaryStatistics, other.doubleSummaryStatistics)
                    && Objects.equals(intSummaryStatistics, other.intSummaryStatistics)
                    && Objects.equals(longSummaryStatistics, other.longSummaryStatistics);
        }

        @Override
        public int hashCode() {
            return Objects
                    .hash(
                        optional,
                        optionalDouble,
                        optionalInt,
                        optionalLong,
                        localDate,
                        localTime,
                        localDateTime,
                        zoneId,
                        zoneOffset,
                        zonedDateTime,
                        dateTimeFormatter,
                        completableFuture,
                        stampedLock,
                        supplier,
                        clock,
                        duration,
                        instant,
                        monthDay,
                        offsetDateTime,
                        offsetTime,
                        period,
                        year,
                        yearMonth,
                        doubleSummaryStatistics,
                        intSummaryStatistics,
                        longSummaryStatistics);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class ExceptionsContainer {

        private final Throwable throwable;
        private final Exception exception;
        private final RuntimeException runtimeException;

        public ExceptionsContainer(Throwable throwable, Exception exception, RuntimeException runtimeException) {
            this.throwable = throwable;
            this.exception = exception;
            this.runtimeException = runtimeException;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ExceptionsContainer)) {
                return false;
            }
            ExceptionsContainer other = (ExceptionsContainer) obj;
            return Objects.equals(throwable, other.throwable)
                    && Objects.equals(exception, other.exception)
                    && Objects.equals(runtimeException, other.runtimeException);
        }

        @Override
        public int hashCode() {
            return Objects.hash(throwable, exception, runtimeException);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class ReflectionClassesContainer {

        private final Class<?> type;
        private final Method method;
        private final Field field;
        private final Constructor<?> constructor;

        public ReflectionClassesContainer(Class<?> type, Method method, Field field, Constructor<?> constructor) {
            this.type = type;
            this.method = method;
            this.field = field;
            this.constructor = constructor;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ReflectionClassesContainer)) {
                return false;
            }
            ReflectionClassesContainer other = (ReflectionClassesContainer) obj;
            return Objects.equals(type, other.type)
                    && Objects.equals(method, other.method)
                    && Objects.equals(field, other.field)
                    && Objects.equals(constructor, other.constructor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, method, field, constructor);
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
        private final DoubleAdder doubleAdder;
        private final DoubleAccumulator doubleAccumulator;
        private final LongAdder longAdder;
        private final LongAccumulator longAccumulator;

        public AtomicClassesContainer(
                AtomicBoolean atomicBoolean,
                AtomicInteger atomicInteger,
                AtomicIntegerArray atomicIntegerArray,
                AtomicLong atomicLong,
                AtomicLongArray atomicLongArray,
                AtomicMarkableReference<?> atomicMarkableReference,
                AtomicReference<?> atomicReference,
                AtomicReferenceArray<?> atomicReferenceArray,
                AtomicStampedReference<?> atomicStampedReference,
                DoubleAdder doubleAdder,
                DoubleAccumulator doubleAccumulator,
                LongAdder longAdder,
                LongAccumulator longAccumulator) {
            this.atomicBoolean = atomicBoolean;
            this.atomicInteger = atomicInteger;
            this.atomicIntegerArray = atomicIntegerArray;
            this.atomicLong = atomicLong;
            this.atomicLongArray = atomicLongArray;
            this.atomicMarkableReference = atomicMarkableReference;
            this.atomicReference = atomicReference;
            this.atomicReferenceArray = atomicReferenceArray;
            this.atomicStampedReference = atomicStampedReference;
            this.doubleAdder = doubleAdder;
            this.doubleAccumulator = doubleAccumulator;
            this.longAdder = longAdder;
            this.longAccumulator = longAccumulator;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AtomicClassesContainer)) {
                return false;
            }
            AtomicClassesContainer other = (AtomicClassesContainer) obj;
            return Objects.equals(atomicBoolean, other.atomicBoolean)
                    && Objects.equals(atomicInteger, other.atomicInteger)
                    && Objects.equals(atomicIntegerArray, other.atomicIntegerArray)
                    && Objects.equals(atomicLong, other.atomicLong)
                    && Objects.equals(atomicLongArray, other.atomicLongArray)
                    && Objects.equals(atomicMarkableReference, other.atomicMarkableReference)
                    && Objects.equals(atomicReference, other.atomicReference)
                    && Objects.equals(atomicReferenceArray, other.atomicReferenceArray)
                    && Objects.equals(atomicStampedReference, other.atomicStampedReference)
                    && Objects.equals(doubleAdder, other.doubleAdder)
                    && Objects.equals(doubleAccumulator, other.doubleAccumulator)
                    && Objects.equals(longAdder, other.longAdder)
                    && Objects.equals(longAccumulator, other.longAccumulator);
        }

        @Override
        public int hashCode() {
            return Objects
                    .hash(
                        atomicBoolean,
                        atomicInteger,
                        atomicIntegerArray,
                        atomicLong,
                        atomicLongArray,
                        atomicMarkableReference,
                        atomicReference,
                        atomicReferenceArray,
                        atomicStampedReference,
                        doubleAdder,
                        doubleAccumulator,
                        longAdder,
                        longAccumulator);
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class AncientJavaApiClassesContainer {

        private final java.awt.color.ColorSpace awtColorSpace;
        private final java.awt.color.ICC_ColorSpace iccColorSpace;
        private final java.awt.color.ICC_Profile iccProfile;
        private final java.awt.Font font;
        private final java.awt.Image image;
        private final java.rmi.dgc.VMID vmid;
        private final java.rmi.server.UID uid;

        public AncientJavaApiClassesContainer(
                java.awt.color.ColorSpace awtColorSpace,
                java.awt.color.ICC_ColorSpace iccColorSpace,
                java.awt.color.ICC_Profile iccProfile,
                java.awt.Font font,
                java.awt.Image image,
                java.rmi.dgc.VMID vmid,
                java.rmi.server.UID uid) {
            this.awtColorSpace = awtColorSpace;
            this.iccColorSpace = iccColorSpace;
            this.iccProfile = iccProfile;
            this.font = font;
            this.image = image;
            this.vmid = vmid;
            this.uid = uid;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AncientJavaApiClassesContainer)) {
                return false;
            }
            AncientJavaApiClassesContainer other = (AncientJavaApiClassesContainer) obj;
            return Objects.equals(awtColorSpace, other.awtColorSpace)
                    && Objects.equals(iccColorSpace, other.iccColorSpace)
                    && Objects.equals(iccProfile, other.iccProfile)
                    && Objects.equals(font, other.font)
                    && Objects.equals(image, other.image)
                    && Objects.equals(vmid, other.vmid)
                    && Objects.equals(uid, other.uid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(awtColorSpace, iccColorSpace, iccProfile, font, image, vmid, uid);
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
            if (instance == null || instance.get() == null) {
                return 0;
            }
            return instance.get().hashCode();
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
            return Objects.hashCode(stringBuilder.toString());
        }
    }

    static final class UnusedInEqualsButPresentInClassContainer {

        @SuppressWarnings("unused")
        private final PropertyChangeSupport pcs;

        public UnusedInEqualsButPresentInClassContainer(PropertyChangeSupport pcs) {
            this.pcs = pcs;
        }
    }

    @SuppressWarnings("CheckReturnValue")
    private static void callIterator(Iterable<?>... collections) {
        for (Iterable<?> c : collections) {
            if (c != null) {
                c.iterator();
            }
        }
    }

    @SuppressWarnings("CheckReturnValue")
    private static void callKeySet(Map<?, ?>... maps) {
        for (Map<?, ?> m : maps) {
            if (m != null) {
                m.keySet();
            }
        }
    }
}
