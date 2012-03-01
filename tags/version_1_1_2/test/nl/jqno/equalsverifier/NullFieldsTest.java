/*
 * Copyright 2009-2010 Jan Ouwens
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
import nl.jqno.equalsverifier.testhelpers.points.Color;

import org.junit.Test;

public class NullFieldsTest {
	private static final String NON_NULLITY = "Non-nullity";
	private static final String EQUALS = "equals throws NullPointerException";
	private static final String HASHCODE = "hashCode throws NullPointerException";
	private static final String TOSTRING = "toString throws NullPointerException";
	private static final String ON_FIELD = "on field";

	@Test
	public void equalsOnOwnNullFields() {
		EqualsVerifier<EqualsThrowsNullOnThis> ev = EqualsVerifier.forClass(EqualsThrowsNullOnThis.class);
		assertFailure(ev, NON_NULLITY, EQUALS, ON_FIELD, "color");
	}

	@Test
	public void equalsOnOthersNullFields() {
		EqualsVerifier<EqualsThrowsNullOnOther> ev = EqualsVerifier.forClass(EqualsThrowsNullOnOther.class);
		assertFailure(ev, NON_NULLITY, EQUALS, ON_FIELD, "color");
	}
	
	@Test
	public void hashCodeOnNullFields() {
		EqualsVerifier<HashCodeThrowsNull> ev = EqualsVerifier.forClass(HashCodeThrowsNull.class);
		assertFailure(ev, NON_NULLITY, HASHCODE, ON_FIELD, "color");
	}
	
	@Test
	public void toStringOnNullFields() {
		EqualsVerifier<ToStringThrowsNull> ev = EqualsVerifier.forClass(ToStringThrowsNull.class);
		assertFailure(ev, NON_NULLITY, TOSTRING, ON_FIELD, "color");
	}
	
	@Test
	public void fieldsAreNeverNullForExamples() {
		EqualsThrowsNullOnThis blue = new EqualsThrowsNullOnThis(Color.BLUE);
		EqualsThrowsNullOnThis yellow = new EqualsThrowsNullOnThis(Color.YELLOW);
		
		EqualsVerifier.forExamples(blue, yellow)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void fieldsAreNeverNullForClass() {
		EqualsVerifier.forClass(EqualsThrowsNullOnThis.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void deepFieldsAreNeverNull() {
		EqualsVerifier.forClass(DeepNullA.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void deepNull() {
		EqualsVerifier.forClass(CheckedDeepNullA.class)
				.verify();
	}
	
	@Test
	public void deepNullSanity() {
		EqualsVerifier.forClass(DeepNullB.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void staticPrimitiveFields() {
		EqualsVerifier.forClass(StaticPrimitiveFields.class).verify();
	}

	@Test
	public void staticObjectFields() {
		EqualsVerifier.forClass(StaticObjectFields.class).verify();
	}
	
	static final class EqualsThrowsNullOnThis {
		final Color color;
		
		EqualsThrowsNullOnThis(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrowsNullOnThis)) {
				return false;
			}
			EqualsThrowsNullOnThis p = (EqualsThrowsNullOnThis)obj;
			return color.equals(p.color);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
	}
	
	static final class EqualsThrowsNullOnOther {
		final Color color;

		EqualsThrowsNullOnOther(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrowsNullOnOther)) {
				return false;
			}
			EqualsThrowsNullOnOther p = (EqualsThrowsNullOnOther)obj;
			return p.color.equals(color);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
	}
	
	static final class HashCodeThrowsNull {
		private final Color color;

		HashCodeThrowsNull(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashCodeThrowsNull)) {
				return false;
			}
			HashCodeThrowsNull p = (HashCodeThrowsNull)obj;
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
	
	static final class ToStringThrowsNull {
		private final Color color;
		
		public ToStringThrowsNull(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HashCodeThrowsNull)) {
				return false;
			}
			HashCodeThrowsNull p = (HashCodeThrowsNull)obj;
			return p.color == color;
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(color);
		}
		
		@Override
		public String toString() {
			throw new NullPointerException();
		}
	}
	
	static final class StaticPrimitiveFields {
		private static int one = Integer.parseInt("1");
		private static final int two = Integer.parseInt("2");
		private static int three = 3;
		private final int four;
		
		public StaticPrimitiveFields(int four) {
			this.four = four;
		}
		
		@SuppressWarnings("static-access")
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof StaticPrimitiveFields)) {
				return false;
			}
			StaticPrimitiveFields sf = (StaticPrimitiveFields)obj;
			return one == sf.one && two == sf.two && three == sf.three && four == sf.four;
		}
		
		@Override
		public int hashCode() {
			return 31 * (31 * (31 * (31 + one) + two) + three) + four;
		}
	}
	
	static final class StaticObjectFields {
		private static Object one = new Object();
		private static final Object two = new Object();
		private static Object three = new Object();
		private final Object four;
		
		public StaticObjectFields(Object four) {
			this.four = four;
		}
		
		@SuppressWarnings("static-access")
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof StaticObjectFields)) {
				return false;
			}
			StaticObjectFields sf = (StaticObjectFields)obj;
			return one == sf.one && two == sf.two && three == sf.three && four == sf.four;
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 + nullSafeHashCode(one);
			result += 31 * nullSafeHashCode(two);
			result += 31 * nullSafeHashCode(three);
			result += 31 * nullSafeHashCode(four);
			return result;
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
