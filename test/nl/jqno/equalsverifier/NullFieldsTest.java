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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import nl.jqno.equalsverifier.points.Color;

import org.junit.Test;

public class NullFieldsTest extends EqualsVerifierTestBase {
	@Test
	public void equalsOnNullFields() {
		EqualsVerifier<EqualsThrowsNull> ev = EqualsVerifier.forClass(EqualsThrowsNull.class);
		verifyFailure("Non-nullity: equals throws NullPointerException", ev);
	}
	
	@Test
	public void hashCodeOnNullFields() {
		EqualsVerifier<HashCodeThrowsNull> ev = EqualsVerifier.forClass(HashCodeThrowsNull.class);
		verifyFailure("Non-nullity: hashCode throws NullPointerException", ev);
	}
	
	@Test
	public void toStringOnNullFields() {
		EqualsVerifier<ToStringThrowsNull> ev = EqualsVerifier.forClass(ToStringThrowsNull.class);
		verifyFailure("Non-nullity: toString throws NullPointerException", ev);
	}
	
	@Test
	public void fieldsAreNeverNullForExamples() {
		EqualsThrowsNull blue = new EqualsThrowsNull(Color.BLUE);
		EqualsThrowsNull yellow = new EqualsThrowsNull(Color.YELLOW);
		
		EqualsVerifier.forExamples(blue, yellow)
				.fieldsAreNeverNull()
				.verify();
	}
	
	@Test
	public void fieldsAreNeverNullForClass() {
		try {
			EqualsVerifier.forClass(EqualsThrowsNull.class).fieldsAreNeverNull();
			fail("Assertion didn't fail");
		}
		catch (AssertionError e) {
			assertEquals("fieldsAreNeverNull() requires construction via forExamples() instead of via forClass().",
					e.getMessage());
		}
	}
	
	@Test
	public void staticPrimitiveFields() {
		EqualsVerifier.forClass(StaticPrimitiveFields.class).verify();
	}

	@Test
	public void staticObjectFields() {
		EqualsVerifier.forClass(StaticObjectFields.class).verify();
	}
	
	private static final class EqualsThrowsNull {
		final Color color;

		EqualsThrowsNull(Color color) {
			this.color = color;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrowsNull)) {
				return false;
			}
			EqualsThrowsNull p = (EqualsThrowsNull)obj;
			return p.color.equals(color);
		}
		
		@Override
		public int hashCode() {
			return (color == null ? 0 : color.hashCode());
		}
	}
	
	private static final class HashCodeThrowsNull {
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
	
	private static final class ToStringThrowsNull {
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
			return (color == null ? 0 : color.hashCode());
		}
		
		@Override
		public String toString() {
			throw new NullPointerException();
		}
	}
	
	private static final class StaticPrimitiveFields {
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
	
	private static final class StaticObjectFields {
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
			int result = 31 + (one == null ? 0 : one.hashCode());
			result = 31 * result + (two == null ? 0 : two.hashCode());
			result = 31 * result + (three == null ? 0 : three.hashCode());
			result = 31 * result + (four == null ? 0 : four.hashCode());
			return result;
		}
	}
}
