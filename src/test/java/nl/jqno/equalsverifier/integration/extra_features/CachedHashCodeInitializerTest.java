package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Test;

import javax.annotation.Nonnull;

/**
 * @author Niall Gallagher
 */
public class CachedHashCodeInitializerTest extends IntegrationTestBase {

    @Test // Test behavior when CachedHashCodeInitializer is NOT used...
    public void fail_whenCachedHashCodeIsValid_givenWithCachedHashCodeIsNotUsed() {
        expectFailure("Significant fields", "equals relies on", "name", "but hashCode does not");
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .verify();
    }

    @Test // Test behavior when CachedHashCodeInitializer IS used...
    public void succeed_whenCachedHashCodeIsValid_givenWithCachedHashCodeIsUsed() {
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode")
                .verify();
    }

    @Test // Test that we can validate a buggy implementation of cached hash code...
    public void fail_whenCachedHashCodeIsInvalid_givenWithCachedHashCodeIsUsed() {
        expectFailure("Significant fields", "equals relies on", "name", "but hashCode does not");
        EqualsVerifier.forClass(ObjectWithInvalidCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode")
                .verify();
    }

    static class ObjectWithValidCachedHashCode {

        @Nonnull final String name;
        final int cachedHashCode;

        public ObjectWithValidCachedHashCode(String name) {
            this.name = name;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ObjectWithValidCachedHashCode)) return false;
            ObjectWithValidCachedHashCode that = (ObjectWithValidCachedHashCode) o;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            return cachedHashCode;
        }

        protected int calcHashCode() {
            return name.hashCode();
        }
    }

    static class ObjectWithInvalidCachedHashCode {

        @Nonnull final String name;
        final int cachedHashCode;

        public ObjectWithInvalidCachedHashCode(String name) {
            this.name = name;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ObjectWithInvalidCachedHashCode)) return false;
            ObjectWithInvalidCachedHashCode that = (ObjectWithInvalidCachedHashCode) o;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            return cachedHashCode;
        }

        protected int calcHashCode() {
            return 3;
        }
    }
}
