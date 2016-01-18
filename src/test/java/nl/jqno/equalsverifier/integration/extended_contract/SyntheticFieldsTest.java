/*
 * Copyright 2010, 2014-2015 Jan Ouwens
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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.*;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class SyntheticFieldsTest {
	@Test
	public void succeed_whenClassHasASyntheticField() {
		EqualsVerifier.forClass(Outer.class)
				.verify();
	}
	
	@Test
	public void succeed_whenClassHasAFieldThatHasASyntheticField() {
		EqualsVerifier.forClass(OuterContainer.class)
				.verify();
	}
	
	@Test
	public void succeed_whenClassIsInstrumentedByCobertura_givenCoberturaDoesntMarkItsFieldsSynthetic() {
		EqualsVerifier.forClass(CoberturaContainer.class)
				.verify();
	}
	
	/* non-static */ final class Outer {
		private final Inner inner;
		
		private /* non-static */ final class Inner {
			int foo;
			
			@Override public boolean equals(Object obj) { return obj instanceof Inner; }
			@Override public int hashCode() { return 42; }
		}
		
		public Outer() { inner = null; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
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
	
	public static final class CoberturaContainer {
		public static transient int[] __cobertura_counters;
		private final int i;
		
		public CoberturaContainer(int i) {
			this.i = i;
		}
		
		static {
			__cobertura_init();
		}
		
		public static void __cobertura_init() {
			__cobertura_counters = new int[1];
		}
		
		@Override
		public boolean equals(Object obj) {
			__cobertura_counters[0] += 1;
			if (!(obj instanceof CoberturaContainer)) {
				return false;
			}
			CoberturaContainer p = (CoberturaContainer)obj;
			return p.i == i;
		}
		
		@Override
		public int hashCode() {
			__cobertura_counters[0] += 1;
			return defaultHashCode(this);
		}
	}
}
