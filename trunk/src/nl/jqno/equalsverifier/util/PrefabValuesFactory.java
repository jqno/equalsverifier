/*
 * Copyright 2010-2011 Jan Ouwens
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
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
import java.util.LinkedHashSet;
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
 * Creates instances of classes for use in a {@link PrefabValues} object.
 * 
 * Can create new instances of a given type and the types of its fields, and
 * contains hand-made instances of well-known Java API classes that cannot be
 * instantiated dynamically because of an internal infinite recursion of types.
 * 
 * @author Jan Ouwens
 */
public class PrefabValuesFactory {
	private PrefabValues prefabValues;
	
	/**
	 * @return An instance of {@link PrefabValues}, filled with instances
	 * 			of Java API classes that cannot be instantiated dynamically.
	 */
	public static PrefabValues withJavaClasses() {
		return new PrefabValuesFactory(new PrefabValues()).addJavaClasses();
	}
	
	/**
	 * Private constructor. Use {@link #withJavaClasses()} instead.
	 */
	PrefabValuesFactory(PrefabValues prefabValues) {
		this.prefabValues = prefabValues;
	}
	
	/**
	 * Creates instances for the specified type, and for the types of the
	 * fields contained within the specified type, recursively.
	 * 
	 * @param type The type to create prefabValues for.
	 * @throws RecursionException If recursion is detected.
	 */
	public void createFor(Class<?> type) {
		createFor(type, new LinkedHashSet<Class<?>>());
	}
	
	private void createFor(Class<?> type, LinkedHashSet<Class<?>> typeStack) {
		if (noNeedToCreatePrefabValues(type)) {
			return;
		}
		if (typeStack.contains(type)) {
			throw new RecursionException(typeStack);
		}
		
		if (type.isEnum()) {
			addEnumInstances(type);
		}
		else {
			@SuppressWarnings("unchecked")
			LinkedHashSet<Class<?>> clone = (LinkedHashSet<Class<?>>)typeStack.clone();
			clone.add(type);
			
			traverseFields(type, clone);
			createInstances(type);
		}
	}
	
	private boolean noNeedToCreatePrefabValues(Class<?> type) {
		return prefabValues.contains(type) || type.isPrimitive();
	}

	private <T> void addEnumInstances(Class<T> type) {
		T[] enumConstants = type.getEnumConstants();
		if (enumConstants.length < 2) {
			throw new InternalException("Enum " + type.getSimpleName() + " only has " + enumConstants.length + " element(s).");
		}
		prefabValues.put(type, enumConstants[0], enumConstants[1]);
	}
	
	private void traverseFields(Class<?> type, LinkedHashSet<Class<?>> typeStack) {
		for (Field field : new FieldIterable(type)) {
			int modifiers = field.getModifiers();
			boolean isStaticAndFinal = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
			if (!isStaticAndFinal) {
				Class<?> fieldType = field.getType();
				if (fieldType.isArray()) {
					createFor(fieldType.getComponentType(), typeStack);
				}
				else {
					createFor(fieldType, typeStack);
				}
			}
		}
	}

	private <T> void createInstances(Class<T> type) {
		ClassAccessor<T> accessor = ClassAccessor.of(type, prefabValues);
		T red = accessor.getRedObject();
		T black = accessor.getBlackObject();
		prefabValues.put(type, red, black);
	}
	
	private PrefabValues addJavaClasses() {
		addPrimitiveClasses();
		addClasses();
		addLists();
		addMaps();
		addSets();
		addQueues();
		addClassesNecessaryForCgLib();
		return prefabValues;
	}
	
	private void addPrimitiveClasses() {
		prefabValues.put(Boolean.class, true, false);
		prefabValues.put(Byte.class, (byte)1, (byte)2);
		prefabValues.put(Character.class, 'a', 'b');
		prefabValues.put(Double.class, 0.5D, 1.0D);
		prefabValues.put(Float.class, 0.5F, 1.0F);
		prefabValues.put(Integer.class, 1, 2);
		prefabValues.put(Long.class, 1L, 2L);
		prefabValues.put(Short.class, (short)1, (short)2);
		prefabValues.put(Object.class, new Object(), new Object());
		prefabValues.put(Class.class, Class.class, Object.class);
		prefabValues.put(String.class, "one", "two");
	}

	private void addClasses() {
		prefabValues.put(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
		prefabValues.put(Date.class, new Date(0), new Date(1));
		prefabValues.put(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance());
		prefabValues.put(Formatter.class, new Formatter(), new Formatter());
		prefabValues.put(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
		prefabValues.put(Locale.class, new Locale("nl"), new Locale("hu"));
		prefabValues.put(Pattern.class, Pattern.compile("one"), Pattern.compile("two"));
		prefabValues.put(Scanner.class, new Scanner("one"), new Scanner("two"));
		prefabValues.put(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"));
		prefabValues.put(BigDecimal.class, BigDecimal.ZERO, BigDecimal.ONE);
		prefabValues.put(BigInteger.class, BigInteger.ZERO, BigInteger.ONE);
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	private void addLists() {
		addCollection(CopyOnWriteArrayList.class, new CopyOnWriteArrayList(), new CopyOnWriteArrayList());
		addCollection(LinkedList.class, new LinkedList(), new LinkedList());
		addCollection(ArrayList.class, new ArrayList(), new ArrayList());
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	private void addMaps() {
		prefabValues.put(EnumMap.class, Dummy.RED.map(), Dummy.BLACK.map());
		addMap(ConcurrentHashMap.class, new ConcurrentHashMap(), new ConcurrentHashMap());
		addMap(HashMap.class, new HashMap(), new HashMap());
		addMap(Hashtable.class, new Hashtable(), new Hashtable());
		addMap(LinkedHashMap.class, new LinkedHashMap(), new LinkedHashMap());
		addMap(Properties.class, new Properties(), new Properties());
		addMap(TreeMap.class, new TreeMap(), new TreeMap());
		addMap(WeakHashMap.class, new WeakHashMap(), new WeakHashMap());
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	private void addSets() {
		addCollection(CopyOnWriteArraySet.class, new CopyOnWriteArraySet(), new CopyOnWriteArraySet());
		addCollection(TreeSet.class, new TreeSet(), new TreeSet());
		prefabValues.put(EnumSet.class, EnumSet.of(Dummy.RED), EnumSet.of(Dummy.BLACK));
	}
	
	@SuppressWarnings("rawtypes")
	private void addQueues() {
		prefabValues.put(ArrayBlockingQueue.class, new ArrayBlockingQueue(1), new ArrayBlockingQueue(1));
		prefabValues.put(ConcurrentLinkedQueue.class, new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue());
		prefabValues.put(DelayQueue.class, new DelayQueue(), new DelayQueue());
		prefabValues.put(LinkedBlockingQueue.class, new LinkedBlockingQueue(), new LinkedBlockingQueue());
		prefabValues.put(PriorityBlockingQueue.class, new PriorityBlockingQueue(), new PriorityBlockingQueue());
		prefabValues.put(SynchronousQueue.class, new SynchronousQueue(), new SynchronousQueue());
		
	}

	private void addClassesNecessaryForCgLib() {
		prefabValues.put(NoOp.class, new NoOp(){}, new NoOp(){});
	}
	
	private <T extends Collection<Object>> void addCollection(Class<T> type, T red, T black) {
		red.add("red");
		black.add("black");
		prefabValues.put(type, red, black);
	}
	
	private <T extends Map<Object, Object>> void addMap(Class<T> type, T red, T black) {
		red.put("red_key", "red_value");
		black.put("black_key", "black_value");
		prefabValues.put(type, red, black);
	}
	
	private enum Dummy { 
		RED, BLACK;
		
		public EnumMap<Dummy, String> map() {
			EnumMap<Dummy, String> result = new EnumMap<Dummy, String>(Dummy.class);
			result.put(this, toString());
			return result;
		}
	}
}
