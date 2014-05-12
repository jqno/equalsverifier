/*
 * Copyright 2009-2010, 2013-2014 Jan Ouwens
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
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.points.Color;

import org.junit.Test;

public class NullFieldsTest {
	private static final String NON_NULLITY = "Non-nullity";
	private static final String EQUALS = "equals throws NullPointerException";
	private static final String HASHCODE = "hashCode throws NullPointerException";
	private static final String ON_FIELD = "on field";
	
	@Test
	public void fail_whenEqualsThrowsNpeOnThissField() {
		EqualsVerifier<EqualsThrowsNpeOnThis> ev = EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class);
		assertFailure(ev, NullPointerException.class, NON_NULLITY, EQUALS, ON_FIELD, "color");
	}
	
	@Test
	public void fail_whenEqualsThrowsNpeOnOthersField() {
		EqualsVerifier<EqualsThrowsNpeOnOther> ev = EqualsVerifier.forClass(EqualsThrowsNpeOnOther.class);
		assertFailure(ev, NullPointerException.class, NON_NULLITY, EQUALS, ON_FIELD, "color");
	}
	
	@Test
	public void fail_whenHashCodeThrowsNpe() {
		EqualsVerifier<HashCodeThrowsNpe> ev = EqualsVerifier.forClass(HashCodeThrowsNpe.class);
		assertFailure(ev, NullPointerException.class, NON_NULLITY, HASHCODE, ON_FIELD, "color");
	}
	
	@Test
	public void succeed_whenEqualsThrowsNpeOnThissField_givenExamples() {
		EqualsThrowsNpeOnThis blue = new EqualsThrowsNpeOnThis(Color.BLUE);
		EqualsThrowsNpeOnThis yellow = new EqualsThrowsNpeOnThis(Color.YELLOW);
		
		EqualsVerifier.forExamples(blue, yellow)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsThrowsNpeOnThissField_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsTestFieldWhichThrowsNpe() {
		EqualsVerifier.forClass(CheckedDeepNullA.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsThrowsNpeOnFieldWhichAlsoThrowsNpe_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(DeepNullA.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenDoingASanityCheckOnTheFieldUsedInThePreviousTests_givenWarningIsSuppressed() {
		EqualsVerifier.forClass(DeepNullB.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	static final class EqualsThrowsNpeOnThis {
		final Color color;
		
		public EqualsThrowsNpeOnThis(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrowsNpeOnThis)) {
				return false;
			}
			EqualsThrowsNpeOnThis p = (EqualsThrowsNpeOnThis)obj;
			return color.equals(p.color);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
	}
	
	static final class EqualsThrowsNpeOnOther {
		final Color color;
		
		public EqualsThrowsNpeOnOther(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrowsNpeOnOther)) {
				return false;
			}
			EqualsThrowsNpeOnOther p = (EqualsThrowsNpeOnOther)obj;
			return p.color.equals(color);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
	}
	
	static final class HashCodeThrowsNpe {
		private final Color color;
		
		public HashCodeThrowsNpe(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashCodeThrowsNpe)) {
				return false;
			}
			HashCodeThrowsNpe p = (HashCodeThrowsNpe)obj;
			return p.color == color;
		}
		
		@Override
		public int hashCode() {
			return color.hashCode();
		}
		
		@Override
		public String toString() {
			//Object.toString calls hashCode()
			return "";
		}
	}
	
	static final class CheckedDeepNullA {
		private final DeepNullB b;
		
		public CheckedDeepNullA(DeepNullB b) {
			this.b = b;
		}
		
		@Override 
		public int hashCode() {
			return nullSafeHashCode(b);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof CheckedDeepNullA)) {
				return false;
			}
			CheckedDeepNullA other = (CheckedDeepNullA)obj;
			return nullSafeEquals(b, other.b);
		}
	}
	
	static final class DeepNullA {
		private final DeepNullB b;
		
		public DeepNullA(DeepNullB b) {
			if (b == null) {
				throw new NullPointerException("b");
			}
			
			this.b = b;
		}
		
		@Override 
		public int hashCode() {
			return b.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof DeepNullA) && ((DeepNullA) obj).b.equals(b);
		}
	}
	
	static final class DeepNullB {
		private final Object o;
		
		public DeepNullB(Object o) {
			if (o == null) {
				throw new NullPointerException("o");
			}
			
			this.o = o;
		}
		
		@Override
		public int hashCode() {
			return o.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof DeepNullB) && o.equals(((DeepNullB) obj).o);
		}
	}
}
