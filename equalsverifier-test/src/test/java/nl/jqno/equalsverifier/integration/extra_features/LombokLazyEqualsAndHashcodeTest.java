package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: LocalFinalVariableName
// CHECKSTYLE OFF: MemberName
// CHECKSTYLE OFF: NeedBraces

class LombokLazyEqualsAndHashcodeTest {

    @Test
    void withLombokCachedHashCode() {
        EqualsVerifier
                .forClass(LazyPojo.class)
                .withLombokCachedHashCode(new LazyPojo("a", new Object()))
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    void defaultEqualsVerifierFailsForCachedLombokEqualsAndHashcode() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(LazyPojo.class).suppress(Warning.STRICT_INHERITANCE).verify())
                .assertFailure()
                .assertMessageContains("hashCode relies on $hashCodeCache, but equals does not.");
    }

    @Test
    void defaultEqualsVerifierFailsForCachedLombokEqualsAndHashcodeWhenUsingWithCachedHashCode() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(LazyPojo.class)
                            .suppress(Warning.STRICT_INHERITANCE)
                            .withCachedHashCode("$hashCodeCache", "hashCode", new LazyPojo("bar", new Object()))
                            .verify())
                .assertThrows(IllegalArgumentException.class)
                .assertMessageContains(
                    "Cached hashCode: Could not find calculateHashCodeMethod: must be 'private int hashCode()'");
    }

    /**
     * This class has been generated with Lombok (1.18.20). It is equivalent to:
     *
     * <pre>
     * &#64;RequiredArgsConstructor
     * &#64;EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
     * public class LazyPojo {
     *
     *     private final String foo;
     *
     *     private final Object bar;
     * }
     * </pre>
     */
    @SuppressWarnings({ "RedundantIfStatement", "EqualsReplaceableByObjectsCall" })
    private static class LazyPojo {

        private transient int $hashCodeCache;

        private final String foo;
        private final Object bar;

        public LazyPojo(String foo, Object bar) {
            this.foo = foo;
            this.bar = bar;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this)
                return true;
            if (!(o instanceof LazyPojo))
                return false;
            final LazyPojo other = (LazyPojo) o;
            if (!other.canEqual(this))
                return false;
            final Object this$foo = this.foo;
            final Object other$foo = other.foo;
            if (this$foo == null ? other$foo != null : !this$foo.equals(other$foo))
                return false;
            final Object this$bar = this.bar;
            final Object other$bar = other.bar;
            if (this$bar == null ? other$bar != null : !this$bar.equals(other$bar))
                return false;
            return true;
        }

        protected boolean canEqual(Object other) {
            return other instanceof LazyPojo;
        }

        @Override
        public int hashCode() {
            if (this.$hashCodeCache != 0) {
                return this.$hashCodeCache;
            }
            else {
                final int PRIME = 59;
                int result = 1;
                final Object $foo = this.foo;
                result = result * PRIME + ($foo == null ? 43 : $foo.hashCode());
                final Object $bar = this.bar;
                result = result * PRIME + ($bar == null ? 43 : $bar.hashCode());

                this.$hashCodeCache = result;
                return result;
            }
        }
    }
}
