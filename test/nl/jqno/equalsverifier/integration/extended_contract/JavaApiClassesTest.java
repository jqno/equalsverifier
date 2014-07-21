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

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.io.File;
import java.text.SimpleDateFormat;
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
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Test;

public class JavaApiClassesTest extends IntegrationTestBase {
	@Test
	public void succeed_whenClassContainsACollectionInterface() {
		EqualsVerifier.forClass(CollectionInterfacesContainer.class)
				.verify();
	}
	
	@Test
	public void succeed_whenClassContainsACommonJavaApiType() {
		EqualsVerifier.forClass(CommonClassesContainer.class)
				.verify();
	}
	
	@Test
	public void fail_whenClassUsesOnlyCompileTimeConstantsInNonStaticFields() {
		expectFailure("Precondition: two objects are equal to each other");
		EqualsVerifier.forClass(CompileTimeConstantContainer.class)
				.verify();
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
		
		public CollectionInterfacesContainer(Collection<String> collection, List<String> list, Set<String> set, Map<String, String> map, Queue<String> queue)
			{ this.collection = collection; this.list = list; this.set = set; this.map = map; this.queue = queue; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }

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
	
	@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
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
		private final SimpleDateFormat simpleDateFormat;
		
		public CommonClassesContainer(String string, Integer integer, Class<?> type, Calendar calendar,
				Date date, File file, GregorianCalendar gregorianCalendar, Pattern pattern,
				ArrayList<String> arrayList, BitSet bitset, UUID uuid, SimpleDateFormat simpleDateFormat) {
			this.string = string; this.integer = integer; this.type = type; this.calendar = calendar; this.date = date;
			this.file = file; this.gregorianCalendar = gregorianCalendar; this.pattern = pattern; this.arrayList = arrayList;
			this.bitset = bitset; this.uuid = uuid; this.simpleDateFormat = simpleDateFormat;
		}
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
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
