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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import net.sf.cglib.proxy.NoOp;

/**
 * Contains prefabricated instances of classes that are hard to instantiate
 * or scramble by {@link Instantiator}.
 * 
 * @author Jan Ouwens
 */
class PrefabValues {
	/**
	 * @return A map of prefabricated object instances.
	 */
	@SuppressWarnings("unchecked")
	public static Map<Class<?>, List<?>> get() {
		Builder b = new Builder();
		
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
		
		b.add(Date.class, new Date(0), new Date(1));
		b.add(Pattern.class, Pattern.compile("one"), Pattern.compile("two"));
		
		b.add(EnumMap.class, Dummy.FIRST.map(), Dummy.SECOND.map());
		b.add(ConcurrentHashMap.class, map(new ConcurrentHashMap(), new ConcurrentHashMap()));
		b.add(HashMap.class, map(new HashMap(), new HashMap()));
		b.add(Hashtable.class, new Hashtable(), new Hashtable());
		b.add(LinkedHashMap.class, map(new LinkedHashMap(), new LinkedHashMap()));
		b.add(Properties.class, map(new Properties(), new Properties()));
		b.add(TreeMap.class, map(new TreeMap(), new TreeMap()));
		b.add(WeakHashMap.class, map(new WeakHashMap(), new WeakHashMap()));
		
		b.add(NoOp.class, new NoOp(){}, new NoOp(){}); // Necessary for CGLib.
		return b.get();
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
