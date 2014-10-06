/*
 * Copyright 2010, 2013-2014 Jan Ouwens
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
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Test;

public class NullFieldsWithExceptionsTest extends IntegrationTestBase {
	private static final String EQUALS = "equals";
	private static final String HASH_CODE = "hashCode";
	private static final String THROWS = "throws";
	private static final String ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException";
	private static final String ILLEGAL_STATE_EXCEPTION = "IllegalStateException";
	private static final String WHEN_FOO_IS_NULL = "when field foo is null";
	
	@Test
	public void recogniseUnderlyingNpe_whenIllegalArgumentExceptionIsThrownInEquals_givenFieldIsNull() {
		expectFailureWithCause(IllegalArgumentException.class, EQUALS, THROWS, ILLEGAL_ARGUMENT_EXCEPTION, WHEN_FOO_IS_NULL);
		EqualsVerifier.forClass(EqualsIllegalArgumentThrower.class)
				.verify();
	}
	
	@Test
	public void recogniseUnderlyingNpe_whenIllegalStateExceptionIsThrownInEquals_givenFieldIsNull() {
		expectFailureWithCause(IllegalStateException.class, EQUALS, THROWS, ILLEGAL_STATE_EXCEPTION, WHEN_FOO_IS_NULL);
		EqualsVerifier.forClass(EqualsIllegalStateThrower.class)
				.verify();
	}
	
	@Test
	public void recogniseUnderlyingNpe_whenIllegalArgumentExceptionIsThrownInHashCode_givenFieldIsNull() {
		expectFailureWithCause(IllegalArgumentException.class, HASH_CODE, THROWS, ILLEGAL_ARGUMENT_EXCEPTION, WHEN_FOO_IS_NULL);
		EqualsVerifier.forClass(HashCodeIllegalArgumentThrower.class)
				.verify();
	}
	
	@Test
	public void recogniseUnderlyingNpe_whenIllegalStateExceptionIsThrownInHashCode_givenFieldIsNull() {
		expectFailureWithCause(IllegalStateException.class, HASH_CODE, THROWS, ILLEGAL_STATE_EXCEPTION, WHEN_FOO_IS_NULL);
		EqualsVerifier.forClass(HashCodeIllegalStateThrower.class)
				.verify();
	}
	
	abstract static class EqualsThrower {
		private final String foo;
		
		protected abstract RuntimeException throwable();
		
		public EqualsThrower(String foo) { this.foo = foo; }
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrower)) {
				return false;
			}
			EqualsThrower other = (EqualsThrower)obj;
			if (foo == null) {
				throw throwable();
			}
			return nullSafeEquals(foo, other.foo);
		}
		
		@Override public final int hashCode() { return defaultHashCode(this); }
	}
	
	static class EqualsIllegalArgumentThrower extends EqualsThrower {
		public EqualsIllegalArgumentThrower(String foo) { super(foo); }
		
		@Override
		protected RuntimeException throwable() {
			return new IllegalArgumentException();
		}
	}
	
	static class EqualsIllegalStateThrower extends EqualsThrower {
		public EqualsIllegalStateThrower(String foo) { super(foo); }
		
		@Override
		protected RuntimeException throwable() {
			return new IllegalStateException();
		}
	}
	
	abstract static class HashCodeThrower {
		private final String foo;
		
		protected abstract RuntimeException throwable();
		
		public HashCodeThrower(String foo) { this.foo = foo; }
		
		@Override public final boolean equals(Object obj) { return defaultEquals(this, obj); }
		
		@Override
		public final int hashCode() {
			if (foo == null) {
				throw throwable();
			}
			return foo.hashCode();
		}
	}
	
	static class HashCodeIllegalArgumentThrower extends HashCodeThrower {
		public HashCodeIllegalArgumentThrower(String foo) { super(foo); }
		
		@Override
		protected RuntimeException throwable() {
			return new IllegalArgumentException();
		}
	}
	
	static class HashCodeIllegalStateThrower extends HashCodeThrower {
		public HashCodeIllegalStateThrower(String foo) { super(foo); }
		
		@Override
		protected RuntimeException throwable() {
			return new IllegalStateException();
		}
	}
}
