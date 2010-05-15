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
package nl.jqno.equalsverifier;

import org.junit.Test;

public class SyntheticFieldsTest {
	@Test
	public void nonstaticInnerClassHasSyntheticReferenceToOuterDirect() {
		EqualsVerifier.forClass(Outer.class).debug().verify();
	}
	
	@Test
	public void nonstaticInnerClassHasSyntheticReferenceToOuterIndirect() {
		EqualsVerifier.forClass(OuterContainer.class).verify();
	}
	
	
	public final class Outer {
		private final Inner inner;
		
		private class Inner {
			@SuppressWarnings("unused")
			int i;
		}
		
		public Outer() {
			inner = null;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Outer)) {
				return false;
			}
			Outer other = (Outer)obj;
			return inner == null ? other.inner == null : inner.equals(other.inner);
		}
		
		@Override
		public int hashCode() {
			return inner == null ? 0 : inner.hashCode();
		}
	}
	
	public final class OuterContainer {
		private final Outer outer;
		
		public OuterContainer() {
			outer = null;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof OuterContainer)) {
				return false;
			}
			OuterContainer other = (OuterContainer)obj;
			return outer == null ? other.outer == null : outer.equals(other.outer);
		}
		
		@Override
		public int hashCode() {
			return outer == null ? 0 : outer.hashCode();
		}
	}
}
