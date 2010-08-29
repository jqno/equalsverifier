package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.Helper.assertFailure;
import static nl.jqno.equalsverifier.Helper.nullSafeEquals;
import static nl.jqno.equalsverifier.Helper.nullSafeHashCode;

import org.junit.Test;

public class NullFieldsWithExceptionsTest {
	private static final String EQUALS = "equals";
	private static final String HASH_CODE = "hashCode";
	private static final String TO_STRING = "toString";
	private static final String THROWS = "throws";
	private static final String ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException";
	private static final String ILLEGAL_STATE_EXCEPTION = "IllegalStateException";
	private static final String ILLEGAL_FORMAT_CONVERSION_EXCEPTION = "IllegalFormatConversionException";
	private static final String WHEN_FIRST_IS_NULL = "when field first is null";
	private static final String WHEN_S_IS_NULL = "when field foo is null";

	@Test
	public void issue31() {
		EqualsVerifier<Issue31> ev = EqualsVerifier.forClass(Issue31.class);
		assertFailure(ev, TO_STRING, THROWS, ILLEGAL_FORMAT_CONVERSION_EXCEPTION, WHEN_FIRST_IS_NULL);
	}
	
	@Test
	public void equalsThrowsIllegalArgumentExceptionWhenFieldIsNull() {
		EqualsVerifier<EqualsIllegalArgumentThrower> ev = EqualsVerifier.forClass(EqualsIllegalArgumentThrower.class);
		assertFailure(ev, EQUALS, THROWS, ILLEGAL_ARGUMENT_EXCEPTION, WHEN_S_IS_NULL);
	}
	
	@Test
	public void equalsThrowsIllegalStateExceptionWhenFieldIsNull() {
		EqualsVerifier<EqualsIllegalStateThrower> ev = EqualsVerifier.forClass(EqualsIllegalStateThrower.class);
		assertFailure(ev, EQUALS, THROWS, ILLEGAL_STATE_EXCEPTION, WHEN_S_IS_NULL);
	}
	
	@Test
	public void hashCodeThrowsIllegalArgumentExceptionWhenFieldIsNull() {
		EqualsVerifier<HashCodeIllegalArgumentThrower> ev = EqualsVerifier.forClass(HashCodeIllegalArgumentThrower.class);
		assertFailure(ev, HASH_CODE, THROWS, ILLEGAL_ARGUMENT_EXCEPTION, WHEN_S_IS_NULL);
	}
	
	@Test
	public void hashCodeThrowsIllegalStateExceptionWhenFieldIsNull() {
		EqualsVerifier<HashCodeIllegalStateThrower> ev = EqualsVerifier.forClass(HashCodeIllegalStateThrower.class);
		assertFailure(ev, HASH_CODE, THROWS, ILLEGAL_STATE_EXCEPTION, WHEN_S_IS_NULL);
	}
	
	@Test
	public void toStringThrowsIllegalArgumentExceptionWhenFieldIsNull() {
		EqualsVerifier<ToStringIllegalArgumentThrower> ev = EqualsVerifier.forClass(ToStringIllegalArgumentThrower.class);
		assertFailure(ev, TO_STRING, THROWS, ILLEGAL_ARGUMENT_EXCEPTION, WHEN_S_IS_NULL);
	}
	
	@Test
	public void toStringThrowsIllegalStateExceptionWhenFieldIsNull() {
		EqualsVerifier<ToStringIllegalStateThrower> ev = EqualsVerifier.forClass(ToStringIllegalStateThrower.class);
		assertFailure(ev, TO_STRING, THROWS, ILLEGAL_STATE_EXCEPTION, WHEN_S_IS_NULL);
	}
	
	final static class Issue31  {
		private final String first;
		private final String second;
		
		Issue31(String first, String second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Issue31)) {
				return false;
			}
			Issue31 other = (Issue31)obj;
			return nullSafeEquals(first, other.first) && nullSafeEquals(second, other.second);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(first) + 31 * nullSafeHashCode(second);
		}
		
		@Override
		public String toString() {
			// Throws IllegalArgumentException once "second" is non-null.
			return String.format("%d", second);
		}
	}
	
	abstract static class EqualsThrower {
		private final String foo;
		
		abstract RuntimeException throwable();
		
		EqualsThrower(String foo) {
			this.foo = foo;
		}
		
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
		
		@Override
		public final int hashCode() {
			return nullSafeHashCode(foo);
		}
	}
	
	static class EqualsIllegalArgumentThrower extends EqualsThrower {
		EqualsIllegalArgumentThrower(String foo) {
			super(foo);
		}

		@Override
		RuntimeException throwable() {
			return new IllegalArgumentException();
		}
	}
	
	static class EqualsIllegalStateThrower extends EqualsThrower {
		EqualsIllegalStateThrower(String foo) {
			super(foo);
		}
		
		@Override
		RuntimeException throwable() {
			return new IllegalStateException();
		}
	}
	
	abstract static class HashCodeThrower {
		private final String foo;
		
		abstract RuntimeException throwable();
		
		public HashCodeThrower(String foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrower)) {
				return false;
			}
			EqualsThrower other = (EqualsThrower)obj;
			return nullSafeEquals(foo, other.foo);
		}
		
		@Override
		public int hashCode() {
			if (foo == null) {
				throw throwable();
			}
			return foo.hashCode();
		}
	}
	
	static class HashCodeIllegalArgumentThrower extends HashCodeThrower {
		public HashCodeIllegalArgumentThrower(String foo) {
			super(foo);
		}

		@Override
		RuntimeException throwable() {
			return new IllegalArgumentException();
		}
	}
	
	static class HashCodeIllegalStateThrower extends HashCodeThrower {
		public HashCodeIllegalStateThrower(String foo) {
			super(foo);
		}
		
		@Override
		RuntimeException throwable() {
			return new IllegalStateException();
		}
	}
	
	abstract static class ToStringThrower {
		private final String foo;
		
		abstract RuntimeException throwable();
		
		public ToStringThrower(String foo) {
			this.foo = foo;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EqualsThrower)) {
				return false;
			}
			EqualsThrower other = (EqualsThrower)obj;
			return nullSafeEquals(foo, other.foo);
		}
		
		@Override
		public int hashCode() {
			return nullSafeHashCode(foo);
		}
		
		@Override
		public String toString() {
			if (foo == null) {
				throw throwable();
			}
			return foo;
		}
	}
	
	static class ToStringIllegalArgumentThrower extends ToStringThrower {
		public ToStringIllegalArgumentThrower(String foo) {
			super(foo);
		}

		@Override
		RuntimeException throwable() {
			return new IllegalArgumentException();
		}
	}
	
	static class ToStringIllegalStateThrower extends ToStringThrower {
		public ToStringIllegalStateThrower(String foo) {
			super(foo);
		}
		
		@Override
		RuntimeException throwable() {
			return new IllegalStateException();
		}
	}
}
