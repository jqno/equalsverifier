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

import org.junit.Test;

public class DifficultClassesTest extends EqualsVerifierTestBase {
	@Test
	public void difficultClasses() {
		EqualsVerifier.forClass(ObjectsContainer.class).verify();
	}
	
	@Test
	public void compileTimeConstant() {
		EqualsVerifier<CompileTimeConstant> ev = EqualsVerifier.forClass(CompileTimeConstant.class);
		verifyFailure("Precondition: two objects are equal to each other", ev);
	}
	
	@Test
	public void threadLocal() {
		EqualsVerifier.forClass(ThreadLocalContainer.class)
				.withPrefabValues(ThreadLocal.class, ThreadLocalContainer.FIRST_INSTANCE, ThreadLocalContainer.SECOND_INSTANCE)
				.verify();
	}
	
	private static final class ObjectsContainer {
		private final String string;
		private final Integer integer;
		
		ObjectsContainer(String string, Integer integer) {
			this.string = string;
			this.integer = integer;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ObjectsContainer)) {
				return false;
			}
			ObjectsContainer other = (ObjectsContainer)obj;
			return
					string == null ? other.string == null : string.equals(other.string) &&
					integer == null ? other.integer == null : integer.equals(other.integer);
		}
		
		@Override
		public int hashCode() {
			return string == null ? 1 : string.hashCode() +
					31 * (integer == null ? Integer.MIN_VALUE : integer);
		}
	}
	
	private static final class CompileTimeConstant {
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
	
	private static final class ThreadLocalContainer {
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
