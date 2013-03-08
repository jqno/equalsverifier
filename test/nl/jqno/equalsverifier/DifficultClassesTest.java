/*
 * Copyright 2009-2012 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Test;

public class DifficultClassesTest {
	@Test
	public void objects() {
		EqualsVerifier.forClass(ObjectsContainer.class).verify();
	}
	
	@Test
	public void collectionInterfaces() {
		EqualsVerifier.forClass(CollectionInterfacesContainer.class).verify();
	}
	
	@Test
	public void commonClasses() {
		EqualsVerifier.forClass(CommonClassesContainer.class).verify();
	}
	
	@Test
	public void abstractClass() {
		EqualsVerifier.forClass(AbstractContainer.class).verify();
	}
	
	@Test
	public void compileTimeConstant() {
		EqualsVerifier<CompileTimeConstant> ev = EqualsVerifier.forClass(CompileTimeConstant.class);
		assertFailure(ev, "Precondition: two objects are equal to each other");
	}
	
	@Test
	public void threadLocal() {
		EqualsVerifier.forClass(ThreadLocalContainer.class)
				.withPrefabValues(ThreadLocal.class, ThreadLocalContainer.RED_INSTANCE, ThreadLocalContainer.BLACK_INSTANCE)
				.verify();
	}
	
	@Test
	public void ignoreSingleValueEnum() {
		EqualsVerifier.forClass(SingleValueEnumContainer.class).verify();
	}
	
	@Test
	public void useSingleValueEnum() {
		EqualsVerifier.forClass(SingleValueEnumUser.class).verify();
	}
	
	static final class ObjectsContainer {
		private final String string;
		private final Integer integer;
		private final Class<?> type;
		
		ObjectsContainer(String string, Integer integer, Class<?> type) {
			this.string = string;
			this.integer = integer;
			this.type = type;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectsContainer)) {
				return false;
			}
			
			ObjectsContainer other = (ObjectsContainer)obj;
			boolean result = true;
			result &= nullSafeEquals(string, other.string);
			result &= nullSafeEquals(integer, other.integer);
			result &= type == other.type;
			return result;
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 * nullSafeHashCode(string);
			result += 31 * nullSafeHashCode(integer);
			result += 31 * nullSafeHashCode(type);
			return result;
		}
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
		private final Calendar calendar;
		private final Date date;
		private final File file;
		private final GregorianCalendar gregorianCalendar;
		private final Pattern pattern;
		private final ArrayList<String> arrayList;
		
		public CommonClassesContainer(Calendar calendar, Date date, File file, GregorianCalendar gregorianCalendar, Pattern pattern, ArrayList<String> arrayList) {
			this.calendar = calendar;
			this.date = date;
			this.file = file;
			this.gregorianCalendar = gregorianCalendar;
			this.pattern = pattern;
			this.arrayList = arrayList;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CommonClassesContainer)) {
				return false;
			}
			
			CommonClassesContainer other = (CommonClassesContainer)obj;
			boolean result = true;
			result &= nullSafeEquals(calendar, other.calendar);
			result &= nullSafeEquals(date, other.date);
			result &= nullSafeEquals(file, other.file);
			result &= nullSafeEquals(gregorianCalendar, other.gregorianCalendar);
			result &= nullSafeEquals(pattern, other.pattern);
			result &= nullSafeEquals(arrayList, other.arrayList);
			return result;
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 * nullSafeHashCode(calendar);
			result += 31 * nullSafeHashCode(date);
			result += 31 * nullSafeHashCode(file);
			result += 31 * nullSafeHashCode(gregorianCalendar);
			result += 31 * nullSafeHashCode(pattern);
			result += 31 * nullSafeHashCode(arrayList);
			return result;
		}
	}
	
	static final class AbstractContainer {
		private final AbstractClass foo;
		
		AbstractContainer(AbstractClass ac) {
			this.foo = ac;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AbstractContainer)) {
				return false;
			}
			AbstractContainer other = (AbstractContainer)obj;
			return nullSafeEquals(foo, other.foo);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(foo);
		}
	}
	
	private static abstract class AbstractClass {
		private int i;
		
		abstract void someMethod();
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AbstractClass)) {
				return false;
			}
			return i == ((AbstractClass)obj).i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class CompileTimeConstant {
		private final String string = "string";
		private final int integer = 10;
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CompileTimeConstant)) {
				return false;
			}
			CompileTimeConstant other = (CompileTimeConstant)obj;
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
	
	enum SingleValueEnum { INSTANCE }
	
	static final class SingleValueEnumContainer {
		private final int i;
		
		@SuppressWarnings("unused")
		private final SingleValueEnum sve = SingleValueEnum.INSTANCE;
		
		public SingleValueEnumContainer(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SingleValueEnumContainer)) {
				return false;
			}
			SingleValueEnumContainer other = (SingleValueEnumContainer)obj;
			return i == other.i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class SingleValueEnumUser {
		private final SingleValueEnum sve;
		
		public SingleValueEnumUser(SingleValueEnum sve) {
			this.sve = sve;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SingleValueEnumUser)) {
				return false;
			}
			SingleValueEnumUser other = (SingleValueEnumUser)obj;
			return sve == other.sve;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(sve);
		}
	}
}
