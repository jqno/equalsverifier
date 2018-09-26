package nl.jqno.equalsverifier.integration.basic_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

public class HashCodeTest extends ExpectedExceptionTestBase {
    @Test
    public void fail_whenHashCodesAreInconsistent() {
        expectFailure("hashCode: hashCode should be consistent", RandomHashCode.class.getSimpleName());
        EqualsVerifier.forClass(RandomHashCode.class)
                .verify();
    }

    @Test
    public void fail_whenHashCodesAreUnequal_givenEqualObjects() {
        expectFailure("hashCode: hashCodes should be equal", NoHashCode.class.getSimpleName());
        EqualsVerifier.forClass(NoHashCode.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsShortCircuitsOnHashCode() {
        EqualsVerifier.forClass(ShortCircuitOnHashCode.class)
                .verify();
    }

    static final class RandomHashCode {
        @Override public boolean equals(Object obj) { return super.equals(obj); }

        @Override
        public int hashCode() {
            // Generate a new hashCode on every invocation.
            return new Object().hashCode();
        }
    }

    static class NoHashCode {
        private final int i;

        public NoHashCode(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NoHashCode)) {
                return false;
            }
            return i == ((NoHashCode)obj).i;
        }
    }

    static final class ShortCircuitOnHashCode {
        private final int i;

        public ShortCircuitOnHashCode(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ShortCircuitOnHashCode)) {
                return false;
            }
            if (hashCode() != obj.hashCode()) {
                return false;
            }
            ShortCircuitOnHashCode other = (ShortCircuitOnHashCode)obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
}
