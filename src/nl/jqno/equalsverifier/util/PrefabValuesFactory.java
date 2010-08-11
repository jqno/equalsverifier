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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
 * Contains prefabricated instances of Java API classes that are hard to
 * instantiate or scramble by {@link InstantiatorFacade}.
 * 
 * @author Jan Ouwens
 */
class PrefabValuesFactory {
	private PrefabValues b;
	
	/**
	 * @return An instance of {@link PrefabValues}, filled with instances
	 * 			of Java API classes that are hard to instantiate or scramble by
	 * 			{@link InstantiatorFacade}.
	 */
	public static PrefabValues get() {
		return new PrefabValuesFactory().build();
	}
	
	private PrefabValuesFactory() {
		b = new PrefabValues();
	}
	
	private PrefabValues build() {
		addPrimitiveClasses();
		addClasses();
		addLists();
		addMaps();
		addSets();
		addQueues();
		addClassesNecessaryForCgLib();
		return b;
	}
	
	private void addPrimitiveClasses() {
		b.put(Boolean.class, true, false);
		b.put(Byte.class, (byte)1, (byte)2);
		b.put(Character.class, 'a', 'b');
		b.put(Double.class, 0.5D, 1.0D);
		b.put(Float.class, 0.5F, 1.0F);
		b.put(Integer.class, 1, 2);
		b.put(Long.class, 1L, 2L);
		b.put(Short.class, (short)1, (short)2);
		b.put(Object.class, new Object(), new Object());
		b.put(Class.class, Class.class, Object.class);
		b.put(String.class, "one", "two");
	}

	private void addClasses() {
		b.put(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
		b.put(Date.class, new Date(0), new Date(1));
		b.put(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance());
		b.put(Formatter.class, new Formatter(), new Formatter());
		b.put(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
		b.put(Locale.class, new Locale("nl"), new Locale("hu"));
		b.put(Pattern.class, Pattern.compile("one"), Pattern.compile("two"));
		b.put(Scanner.class, new Scanner("one"), new Scanner("two"));
		b.put(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"));
	}

	@SuppressWarnings("unchecked")
	private void addLists() {
		addCollection(CopyOnWriteArrayList.class, new CopyOnWriteArrayList(), new CopyOnWriteArrayList());
		addCollection(LinkedList.class, new LinkedList(), new LinkedList());
	}

	@SuppressWarnings("unchecked")
	private void addMaps() {
		b.put(EnumMap.class, Dummy.FIRST.map(), Dummy.SECOND.map());
		addMap(ConcurrentHashMap.class, new ConcurrentHashMap(), new ConcurrentHashMap());
		addMap(HashMap.class, new HashMap(), new HashMap());
		addMap(Hashtable.class, new Hashtable(), new Hashtable());
		addMap(LinkedHashMap.class, new LinkedHashMap(), new LinkedHashMap());
		addMap(Properties.class, new Properties(), new Properties());
		addMap(TreeMap.class, new TreeMap(), new TreeMap());
		addMap(WeakHashMap.class, new WeakHashMap(), new WeakHashMap());
	}

	@SuppressWarnings("unchecked")
	private void addSets() {
		addCollection(CopyOnWriteArraySet.class, new CopyOnWriteArraySet(), new CopyOnWriteArraySet());
		addCollection(TreeSet.class, new TreeSet(), new TreeSet());
		b.put(EnumSet.class, EnumSet.of(Dummy.FIRST), EnumSet.of(Dummy.SECOND));
	}
	
	@SuppressWarnings("unchecked")
	private void addQueues() {
		b.put(ArrayBlockingQueue.class, new ArrayBlockingQueue(1), new ArrayBlockingQueue(1));
		b.put(ConcurrentLinkedQueue.class, new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue());
		b.put(DelayQueue.class, new DelayQueue(), new DelayQueue());
		b.put(LinkedBlockingQueue.class, new LinkedBlockingQueue(), new LinkedBlockingQueue());
		b.put(PriorityBlockingQueue.class, new PriorityBlockingQueue(), new PriorityBlockingQueue());
		b.put(SynchronousQueue.class, new SynchronousQueue(), new SynchronousQueue());
		
	}

	private void addClassesNecessaryForCgLib() {
		b.put(NoOp.class, new NoOp(){}, new NoOp(){});
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Collection> void addCollection(Class<T> klass, T first, T second) {
		first.add("first");
		second.add("second");
		b.put(klass, first, second);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Map> void addMap(Class<T> klass, T first, T second) {
		first.put("first_key", "first_value");
		second.put("second_key", "second_key");
		b.put(klass, first, second);
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
