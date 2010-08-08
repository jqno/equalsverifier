/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Pattern;

import net.sf.cglib.proxy.NoOp;

/**
 * Contains prefabricated instances of classes that are hard to instantiate
 * or scramble by {@link InstantiatorFacade}.
 * 
 * @author Jan Ouwens
 */
class PrefabValues {
	/**
	 * @return A map of prefabricated object instances.
	 */
	public static Map<Class<?>, List<?>> get() {
		Builder b = new Builder();
		addPrimitiveClasses(b);
		addClasses(b);
		addLists(b);
		addMaps(b);
		addSets(b);
		addQueues(b);
		addClassesNecessaryForCgLib(b);
		return b.get();
	}

	private static void addPrimitiveClasses(Builder b) {
		b.add(Boolean.class, true, false);
		b.add(Byte.class, (byte)1, (byte)2);
		b.add(Character.class, 'a', 'b');
		b.add(Double.class, 0.5D, 1.0D);
		b.add(Float.class, 0.5F, 1.0F);
		b.add(Integer.class, 1, 2);
		b.add(Long.class, 1L, 2L);
		b.add(Short.class, (short)1, (short)2);
		b.add(Object.class, new Object(), new Object());
		b.add(Class.class, Class.class, Object.class);
		b.add(String.class, "one", "two");
	}

	private static void addClasses(Builder b) {
		b.add(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
		b.add(Date.class, new Date(0), new Date(1));
		b.add(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance());
		b.add(Formatter.class, new Formatter(), new Formatter());
		b.add(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
		b.add(Locale.class, new Locale("nl"), new Locale("hu"));
		b.add(Pattern.class, Pattern.compile("one"), Pattern.compile("two"));
		b.add(Scanner.class, new Scanner("one"), new Scanner("two"));
		b.add(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"));
	}

	@SuppressWarnings("unchecked")
	private static void addLists(Builder b) {
		b.add(CopyOnWriteArrayList.class, collection(new CopyOnWriteArrayList(), new CopyOnWriteArrayList()));
		b.add(LinkedList.class, collection(new LinkedList(), new LinkedList()));
	}

	@SuppressWarnings("unchecked")
	private static void addMaps(Builder b) {
		b.add(EnumMap.class, Dummy.FIRST.map(), Dummy.SECOND.map());
		b.add(ConcurrentHashMap.class, map(new ConcurrentHashMap(), new ConcurrentHashMap()));
		b.add(HashMap.class, map(new HashMap(), new HashMap()));
		b.add(Hashtable.class, new Hashtable(), new Hashtable());
		b.add(LinkedHashMap.class, map(new LinkedHashMap(), new LinkedHashMap()));
		b.add(Properties.class, map(new Properties(), new Properties()));
		b.add(TreeMap.class, map(new TreeMap(), new TreeMap()));
		b.add(WeakHashMap.class, map(new WeakHashMap(), new WeakHashMap()));
	}

	@SuppressWarnings("unchecked")
	private static void addSets(Builder b) {
		b.add(CopyOnWriteArraySet.class, collection(new CopyOnWriteArraySet(), new CopyOnWriteArraySet()));
		b.add(EnumSet.class, EnumSet.of(Dummy.FIRST), EnumSet.of(Dummy.SECOND));
		b.add(TreeSet.class, collection(new TreeSet(), new TreeSet()));
	}
	
	@SuppressWarnings("unchecked")
	private static void addQueues(Builder b) {
		b.add(ArrayBlockingQueue.class, new ArrayBlockingQueue(1), new ArrayBlockingQueue(1));
		b.add(ConcurrentLinkedQueue.class, new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue());
		b.add(DelayQueue.class, new DelayQueue(), new DelayQueue());
		b.add(LinkedBlockingQueue.class, new LinkedBlockingQueue(), new LinkedBlockingQueue());
		b.add(PriorityBlockingQueue.class, new PriorityBlockingQueue(), new PriorityBlockingQueue());
		b.add(SynchronousQueue.class, new SynchronousQueue(), new SynchronousQueue());
		
	}

	private static void addClassesNecessaryForCgLib(Builder b) {
		b.add(NoOp.class, new NoOp(){}, new NoOp(){});
	}
	
	private PrefabValues() {
		// Do not instantiate.
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Map> List<T> map(T first, T second) {
		first.put("first_key", "first_value");
		second.put("second_key", "second_value");
		return Arrays.asList(first, second);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Collection> List<T> collection(T first, T second) {
		first.add("first");
		second.add("second");
		return Arrays.asList(first, second);
	}
	
	private static class Builder {
		private Map<Class<?>, List<?>> map = new HashMap<Class<?>, List<?>>();
		
		@SuppressWarnings("unchecked")
		public <T> void add(Class<T> klass, T first, T second) {
			map.put(klass, Arrays.asList(first, second));
		}
		
		public <T> void add(Class<T> klass, List<T> items) {
			map.put(klass, items);
		}
		
		public Map<Class<?>, List<?>> get() {
			return Collections.unmodifiableMap(map);
		}
	}
	
	private enum Dummy { 
		FIRST, SECOND;
		
		public EnumMap<Dummy, String> map() {
			EnumMap<Dummy, String> result = new EnumMap<Dummy, String>(Dummy.class);
			result.put(this, toString());
			return result;
		}
	}
}
