/*
 * Copyright 2009-2015,2017 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.regex.Pattern;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class JavaApiClassesTest extends IntegrationTestBase {
    @Test
    public void succeed_whenClassContainsACollectionInterface() {
        EqualsVerifier.forClass(CollectionInterfacesContainer.class)
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
                .withPrefabValues(ThreadLocal.class, ThreadLocalContainer.RED_INSTANCE, ThreadLocalContainer.BLACK_INSTANCE)
                .verify();
    }

    static final class CollectionInterfacesContainer {
        private final Iterable<String> iterable;
        private final Collection<String> collection;
        private final List<String> list;
        private final Set<String> set;
        private final SortedSet<String> sortedSet;
        private final NavigableSet<String> navigableSet;
        private final Queue<String> queue;
        private final BlockingQueue<String> blockingQueue;
        private final Deque<String> deque;
        private final BlockingDeque<String> blockingDeque;
        private final Map<String, String> map;
        private final SortedMap<String, String> sortedMap;
        private final NavigableMap<String, String> navigableMap;
        private final ConcurrentNavigableMap<String, String> concurrentNavigableMap;
        private final Vector<String> vector;
        private final Stack<String> stack;

        // CHECKSTYLE: ignore ParameterNumber for 1 line.
        public CollectionInterfacesContainer(Iterable<String> iterable, Collection<String> collection, List<String> list,
                Set<String> set, SortedSet<String> sortedSet, NavigableSet<String> navigableSet,
                Queue<String> queue, BlockingQueue<String> blockingQueue, Deque<String> deque, BlockingDeque<String> blockingDeque,
                Map<String, String> map, SortedMap<String, String> sortedMap, NavigableMap<String, String> navigableMap,
                ConcurrentNavigableMap<String, String> concurrentNavigableMap, Vector<String> vector, Stack<String> stack) {
            this.iterable = iterable; this.collection = collection; this.list = list;
            this.set = set; this.sortedSet = sortedSet; this.navigableSet = navigableSet;
            this.queue = queue; this.blockingQueue = blockingQueue; this.deque = deque; this.blockingDeque = blockingDeque;
            this.map = map; this.sortedMap = sortedMap; this.navigableMap = navigableMap;
            this.concurrentNavigableMap = concurrentNavigableMap; this.vector = vector; this.stack = stack;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public int hashCode() {
            callAbstractMethodsOnInterface();
            return defaultHashCode(this);
        }

        private void callAbstractMethodsOnInterface() {
            callIterator(iterable);
            callIterator(collection);
            callIterator(list);
            callIterator(set, sortedSet, navigableSet);
            callIterator(queue, blockingQueue, deque, blockingDeque);
            callIterator(vector, stack);
            callKeySet(map, sortedMap, navigableMap, concurrentNavigableMap);
        }

        private void callIterator(Iterable<?>... collections) {
            for (Iterable<?> c : collections) {
                if (c != null) {
                    c.iterator();
                }
            }
        }

        private void callKeySet(Map<?, ?>... maps) {
            for (Map<?, ?> m : maps) {
                if (m != null) {
                    m.keySet();
                }
            }
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class CommonClassesContainer {
        private final String string;
        private final Integer integer;
        private final Class<?> type;
        private final ArrayList<String> arrayList;
        private final BitSet bitset;
        private final Calendar calendar;
        private final Date date;
        private final File file;
        private final GregorianCalendar gregorianCalendar;
        private final Pattern pattern;
        private final SimpleDateFormat simpleDateFormat;
        private final UUID uuid;
        private final InetAddress inetAddress;

        // CHECKSTYLE: ignore ParameterNumber for 1 line.
        public CommonClassesContainer(String string, Integer integer, Class<?> type, ArrayList<String> arrayList, BitSet bitset,
                Calendar calendar, Date date, File file, GregorianCalendar gregorianCalendar, Pattern pattern,
                SimpleDateFormat simpleDateFormat, UUID uuid, InetAddress inetAddress) {
            this.string = string; this.integer = integer; this.type = type; this.arrayList = arrayList; this.bitset = bitset;
            this.calendar = calendar; this.date = date; this.file = file; this.gregorianCalendar = gregorianCalendar; this.pattern = pattern;
            this.simpleDateFormat = simpleDateFormat; this.uuid = uuid; this.inetAddress = inetAddress;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class ThreadLocalContainer {
        public static final ThreadLocal<Integer> RED_INSTANCE = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 10;
            }
        };
        public static final ThreadLocal<Integer> BLACK_INSTANCE = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 20;
            }
        };
        private final ThreadLocal<Integer> instance = RED_INSTANCE;

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
