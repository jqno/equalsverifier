/*
 * Copyright 2009 Jan Ouwens
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

import static nl.jqno.equalsverifier.Helper.assertFailure;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.junit.Test;

public class DifficultClassesTest {
	@Test
	public void objects() {
		EqualsVerifier.forClass(ObjectsContainer.class).verify();
	}
	
	@Test
	public void collections() {
		EqualsVerifier.forClass(CollectionsContainer.class).verify();
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
				.withPrefabValues(ThreadLocal.class, ThreadLocalContainer.FIRST_INSTANCE, ThreadLocalContainer.SECOND_INSTANCE)
				.verify();
	}
	
	static enum E {
		ONE, TWO;
	}
	
	static final class ObjectsContainer {
		private final String string;
		private final Integer integer;
		private final Class<?> klass;
		
		ObjectsContainer(String string, Integer integer, Class<?> klass) {
			this.string = string;
			this.integer = integer;
			this.klass = klass;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectsContainer)) {
				return false;
			}
			ObjectsContainer other = (ObjectsContainer)obj;
			return
					(string == null ? other.string == null : string.equals(other.string)) &&
					(integer == null ? other.integer == null : integer.equals(other.integer)) &&
					(klass == other.klass);
		}
		
		@Override
		public int hashCode() {
			int result = string == null ? 0 : string.hashCode();
			result += 31 * (integer == null ? Integer.MIN_VALUE : integer);
			result += 31 * (klass == null ? 0 : klass.hashCode());
			return result;
		}
	}
	
	static final class CollectionsContainer {
		private final List<String> list;
		private final Set<String> set;
		private final Map<String, String> map;
		private final Queue<String> queue;
		
		public CollectionsContainer(List<String> list, Set<String> set, Map<String, String> map, Queue<String> queue) {
			this.list = list;
			this.set = set;
			this.map = map;
			this.queue = queue;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CollectionsContainer)) {
				return false;
			}
			CollectionsContainer other = (CollectionsContainer)obj;
			return (list == null ? other.list == null : list.equals(other.list)) &&
					(set == null ? other.set == null : set.equals(other.set)) &&
					(map == null ? other.map == null : map.equals(other.map)) &&
					(queue == null ? other.queue == null : queue.equals(other.queue));
		}
		
		@Override
		public int hashCode() {
			int result = list == null ? 0 : list.hashCode();
			result += 31 * (set == null ? 0 : set.hashCode());
			result += 31 * (map == null ? 0 : map.hashCode());
			result += 31 * (queue == null ? 0 : queue.hashCode());
			return result;
		}
	}
	
	static final class AbstractContainer {
		private final AbstractClass ac;
		
		AbstractContainer(AbstractClass ac) {
			this.ac = ac;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AbstractContainer)) {
				return false;
			}
			AbstractContainer other = (AbstractContainer)obj;
			return ac == null ? other.ac == null : ac.equals(other.ac);
		}
		
		@Override
		public int hashCode() {
			return ac == null ? 0 : ac.hashCode();
		}
	}
	
	private static abstract class AbstractClass {
		private int i;
		
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
			return string == null ? 0 : string.hashCode() + 31 * integer;
		}
	}
	
	static final class ThreadLocalContainer {
		public static final ThreadLocal<Integer> FIRST_INSTANCE = new ThreadLocal<Integer>() {
			@Override
			protected Integer initialValue() {
				return 10;
			}
		};
		public static final ThreadLocal<Integer> SECOND_INSTANCE = new ThreadLocal<Integer>() {
			@Override
			protected Integer initialValue() {
				return 20;
			}
		};
		private final ThreadLocal<Integer> tl = FIRST_INSTANCE; 
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ThreadLocalContainer)) {
				return false;
			}
			ThreadLocalContainer tlc = (ThreadLocalContainer)obj;
			if (tl == null) {
				return tlc.tl == null;
			}
			if (tlc.tl == null) {
				return false;
			}
			if (tl.get() == null) {
				return tlc.tl.get() == null;
			}
			return tl.get().equals(tlc.tl.get());
		}
		
		@Override
		public int hashCode() {
			return tl == null ? 0 : tl.hashCode();
		}
	}
}
