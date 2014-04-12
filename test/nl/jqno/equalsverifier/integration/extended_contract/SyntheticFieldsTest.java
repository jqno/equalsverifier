/*
 * Copyright 2010, 2014 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class SyntheticFieldsTest {
	@Test
	public void succeed_whenClassHasASyntheticField() {
		EqualsVerifier.forClass(Outer.class).verify();
	}
	
	@Test
	public void succeed_whenClassHasAFieldThatHasASyntheticField() {
		EqualsVerifier.forClass(OuterContainer.class).verify();
	}
	
	/* non-static */ final class Outer {
		private final Inner inner;
		
		private /* non-static */ final class Inner {
			@SuppressWarnings("unused")
			int foo;
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
			return nullSafeEquals(inner, other.inner);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(inner);
		}
	}
	
	/* non-static */ final class OuterContainer {
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
			return nullSafeEquals(outer, other.outer);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(outer);
		}
	}
}
