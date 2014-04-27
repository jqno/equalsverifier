/*
 * Copyright 2009-2014 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class JavaApiClassesTest {
	@Test
	public void succeed_whenClassContainsACollectionInterface() {
		EqualsVerifier.forClass(CollectionInterfacesContainer.class).verify();
	}
	
	@Test
	public void succeed_whenClassContainsACommonJavaApiType() {
		EqualsVerifier.forClass(CommonClassesContainer.class).verify();
	}
	
	@Test
	public void succeed_whenClassContainsACompileTimeConstantInANonStaticField() {
		EqualsVerifier<CompileTimeConstantContainer> ev = EqualsVerifier.forClass(CompileTimeConstantContainer.class);
		assertFailure(ev, "Precondition: two objects are equal to each other");
	}
	
	@Test
	public void succeed_whenClassContainsAThreadLocalField() {
		EqualsVerifier.forClass(ThreadLocalContainer.class)
				.withPrefabValues(ThreadLocal.class, ThreadLocalContainer.RED_INSTANCE, ThreadLocalContainer.BLACK_INSTANCE)
				.verify();
	}
	
	static final class CollectionInterfacesContainer {
		private final Collection<String> collection;
		private final List<String> list;
		private final Set<String> set;
		private final Map<String, String> map;
		private final Queue<String> queue;
		
		public CollectionInterfacesContainer(Collection<String> collection, List<String> list, Set<String> set, Map<String, String> map, Queue<String> queue) {
			this.collection = collection;
			this.list = list;
			this.set = set;
			this.map = map;
			this.queue = queue;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CollectionInterfacesContainer)) {
				return false;
			}
			
			CollectionInterfacesContainer other = (CollectionInterfacesContainer)obj;
			boolean result = true;
			result &= nullSafeEquals(collection, other.collection);
			result &= nullSafeEquals(list, other.list);
			result &= nullSafeEquals(set, other.set);
			result &= nullSafeEquals(map, other.map);
			result &= nullSafeEquals(queue, other.queue);
			return result;
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 * nullSafeHashCode(collection);
			result += 31 * nullSafeHashCode(list);
			result += 31 * nullSafeHashCode(set);
			result += 31 * nullSafeHashCode(map);
			result += 31 * nullSafeHashCode(queue);
			return result;
		}

		@Override
		public String toString() {
			callAbstractMethodsOnInterface();
			return super.toString();
		}
		
		private void callAbstractMethodsOnInterface() {
			if (collection != null) collection.iterator();
			if (list != null) list.iterator();
			if (set != null) set.iterator();
			if (map != null) map.keySet();
			if (queue != null) queue.iterator();
		}
	}
	
	static final class CommonClassesContainer {
		private final String string;
		private final Integer integer;
		private final Class<?> type;
		private final Calendar calendar;
		private final Date date;
		private final File file;
		private final GregorianCalendar gregorianCalendar;
		private final Pattern pattern;
		private final ArrayList<String> arrayList;
		private final BitSet bitset;
		private final UUID uuid;
		
		public CommonClassesContainer(String string, Integer integer, Class<?> type, Calendar calendar,
				Date date, File file, GregorianCalendar gregorianCalendar, Pattern pattern,
				ArrayList<String> arrayList, BitSet bitset, UUID uuid) {
			this.string = string;
			this.integer = integer;
			this.type = type;
			this.calendar = calendar;
			this.date = date;
			this.file = file;
			this.gregorianCalendar = gregorianCalendar;
			this.pattern = pattern;
			this.arrayList = arrayList;
			this.bitset = bitset;
			this.uuid = uuid;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CommonClassesContainer)) {
				return false;
			}
			
			CommonClassesContainer other = (CommonClassesContainer)obj;
			boolean result = true;
			result &= nullSafeEquals(string, other.string);
			result &= nullSafeEquals(integer, other.integer);
			result &= nullSafeEquals(type, other.type);
			result &= nullSafeEquals(calendar, other.calendar);
			result &= nullSafeEquals(date, other.date);
			result &= nullSafeEquals(file, other.file);
			result &= nullSafeEquals(gregorianCalendar, other.gregorianCalendar);
			result &= nullSafeEquals(pattern, other.pattern);
			result &= nullSafeEquals(arrayList, other.arrayList);
			result &= nullSafeEquals(bitset, other.bitset);
			result &= nullSafeEquals(uuid, other.uuid);
			return result;
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 * nullSafeHashCode(string);
			result += 31 * nullSafeHashCode(integer);
			result += 31 * nullSafeHashCode(type);
			result += 31 * nullSafeHashCode(calendar);
			result += 31 * nullSafeHashCode(date);
			result += 31 * nullSafeHashCode(file);
			result += 31 * nullSafeHashCode(gregorianCalendar);
			result += 31 * nullSafeHashCode(pattern);
			result += 31 * nullSafeHashCode(arrayList);
			result += 31 * nullSafeHashCode(bitset);
			result += 31 * nullSafeHashCode(uuid);
			return result;
		}
	}
	
	static final class CompileTimeConstantContainer {
		private final String string = "string";
		private final int integer = 10;
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CompileTimeConstantContainer)) {
				return false;
			}
			CompileTimeConstantContainer other = (CompileTimeConstantContainer)obj;
			return string.equals(other.string) && integer == other.integer;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(string) + 31 * integer;
		}
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
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(instance);
		}
	}
}
