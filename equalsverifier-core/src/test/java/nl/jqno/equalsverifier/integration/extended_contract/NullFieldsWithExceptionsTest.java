package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class NullFieldsWithExceptionsTest {

    private static final String EQUALS = "equals";
    private static final String HASH_CODE = "hashCode";
    private static final String THROWS = "throws";
    private static final String ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException";
    private static final String ILLEGAL_STATE_EXCEPTION = "IllegalStateException";
    private static final String WHEN_FOO_IS_NULL = "when field foo is null";

    @Test
    public void recogniseUnderlyingNpe_whenIllegalArgumentExceptionIsThrownInEquals_givenFieldIsNull() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(EqualsIllegalArgumentThrower.class).verify())
            .assertFailure()
            .assertCause(IllegalArgumentException.class)
            .assertMessageContains(EQUALS, THROWS, ILLEGAL_ARGUMENT_EXCEPTION, WHEN_FOO_IS_NULL);
    }

    @Test
    public void recogniseUnderlyingNpe_whenIllegalStateExceptionIsThrownInEquals_givenFieldIsNull() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(EqualsIllegalStateThrower.class).verify())
            .assertFailure()
            .assertCause(IllegalStateException.class)
            .assertMessageContains(EQUALS, THROWS, ILLEGAL_STATE_EXCEPTION, WHEN_FOO_IS_NULL);
    }

    @Test
    public void recogniseUnderlyingNpe_whenIllegalArgumentExceptionIsThrownInHashCode_givenFieldIsNull() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(HashCodeIllegalArgumentThrower.class).verify())
            .assertFailure()
            .assertCause(IllegalArgumentException.class)
            .assertMessageContains(HASH_CODE, THROWS, ILLEGAL_ARGUMENT_EXCEPTION, WHEN_FOO_IS_NULL);
    }

    @Test
    public void recogniseUnderlyingNpe_whenIllegalStateExceptionIsThrownInHashCode_givenFieldIsNull() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(HashCodeIllegalStateThrower.class).verify())
            .assertFailure()
            .assertCause(IllegalStateException.class)
            .assertMessageContains(HASH_CODE, THROWS, ILLEGAL_STATE_EXCEPTION, WHEN_FOO_IS_NULL);
    }

    abstract static class EqualsThrower {

        private final String foo;

        public EqualsThrower(String foo) {
            this.foo = foo;
        }

        protected abstract RuntimeException throwable();

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof EqualsThrower)) {
                return false;
            }
            EqualsThrower other = (EqualsThrower) obj;
            if (foo == null) {
                throw throwable();
            }
            return Objects.equals(foo, other.foo);
        }

        @Override
        public final int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class EqualsIllegalArgumentThrower extends EqualsThrower {

        public EqualsIllegalArgumentThrower(String foo) {
            super(foo);
        }

        @Override
        protected RuntimeException throwable() {
            return new IllegalArgumentException();
        }
    }

    static class EqualsIllegalStateThrower extends EqualsThrower {

        public EqualsIllegalStateThrower(String foo) {
            super(foo);
        }

        @Override
        protected RuntimeException throwable() {
            return new IllegalStateException();
        }
    }

    abstract static class HashCodeThrower {

        private final String foo;

        public HashCodeThrower(String foo) {
            this.foo = foo;
        }

        protected abstract RuntimeException throwable();

        @Override
        public final boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public final int hashCode() {
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
        protected RuntimeException throwable() {
            return new IllegalArgumentException();
        }
    }

    static class HashCodeIllegalStateThrower extends HashCodeThrower {

        public HashCodeIllegalStateThrower(String foo) {
            super(foo);
        }

        @Override
        protected RuntimeException throwable() {
            return new IllegalStateException();
        }
    }
}
