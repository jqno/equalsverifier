package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

import javax.annotation.Nonnull;

public class CachedHashCodeTest extends ExpectedExceptionTestBase {
    private static final String SOME_NAME = "some name";
    private static final String CACHED_HASHCODE = "Cached hashCode:";
    private static final String MALFORMED_CALCULATEHASHCODEMETHOD = "Could not find calculateHashCodeMethod: must be 'private int";
    private static final String MALFORMED_CACHEDHASHCODEFIELD = "Could not find cachedHashCodeField: must be 'private int";

    @Test
    public void fail_whenCachedHashCodeIsValid_givenWithCachedHashCodeIsNotUsed() {
        expectFailure("Significant fields", "equals relies on", "name", "but hashCode does not");
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .verify();
    }

    @Test
    public void succeed_whenCachedHashCodeIsValid_givenWithCachedHashCodeIsUsed() {
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithValidCachedHashCode(SOME_NAME))
                .verify();
    }

    @Test
    public void succeed_whenCachedHashCodeIsValidAndLocatedInSuperclass_givenWithCachedHashCodeIsUsed() {
        EqualsVerifier.forClass(Subclass.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new Subclass(SOME_NAME))
                .verify();
    }

    @Test
    public void fail_whenCachedHashCodeIsInvalid_givenWithCachedHashCodeIsUsed() {
        expectFailure("Significant fields", "equals relies on", "name", "but hashCode does not");
        EqualsVerifier.forClass(ObjectWithInvalidCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithInvalidCachedHashCode(SOME_NAME))
                .verify();
    }

    @Test
    public void fail_whenCachedHashCodeFieldDoesNotExist() {
        expectException(IllegalArgumentException.class, CACHED_HASHCODE, MALFORMED_CACHEDHASHCODEFIELD, "doesNotExist");
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .withCachedHashCode("doesNotExist", "calcHashCode", new ObjectWithValidCachedHashCode(SOME_NAME));
    }

    @Test
    public void fail_whenCachedHashCodeFieldIsPublic() {
        expectException(IllegalArgumentException.class, CACHED_HASHCODE, MALFORMED_CACHEDHASHCODEFIELD, "publicField");
        EqualsVerifier.forClass(InvalidCachedHashCodeFieldContainer.class)
                .withCachedHashCode("publicField", "calculateHashCode", new InvalidCachedHashCodeFieldContainer());
    }

    @Test
    public void fail_whenCachedHashCodeFieldIsNotAnInt() {
        expectException(IllegalArgumentException.class, CACHED_HASHCODE, MALFORMED_CACHEDHASHCODEFIELD, "notAnInt");
        EqualsVerifier.forClass(InvalidCachedHashCodeFieldContainer.class)
                .withCachedHashCode("notAnInt", "calculateHashCode", new InvalidCachedHashCodeFieldContainer());
    }

    @Test
    public void fail_whenCalculateHashCodeMethodDoesNotExist() {
        expectException(IllegalArgumentException.class, CACHED_HASHCODE, MALFORMED_CALCULATEHASHCODEMETHOD, "doesNotExist");
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "doesNotExist", new ObjectWithValidCachedHashCode(SOME_NAME));
    }

    @Test
    public void fail_whenCalculateHashCodeMethodDoesNotReturnInt() {
        expectException(IllegalArgumentException.class, CACHED_HASHCODE, MALFORMED_CALCULATEHASHCODEMETHOD, "notAnInt");
        EqualsVerifier.forClass(InvalidCalculateHashCodeMethodsContainer.class)
                .withCachedHashCode("cachedHashCode", "notAnInt", new InvalidCalculateHashCodeMethodsContainer());
    }

    @Test
    public void fail_whenCalculateHashCodeMethodTakesParamters() {
        expectException(IllegalArgumentException.class, CACHED_HASHCODE, MALFORMED_CALCULATEHASHCODEMETHOD, "takesParameters");
        EqualsVerifier.forClass(InvalidCalculateHashCodeMethodsContainer.class)
                .withCachedHashCode("cachedHashCode", "takesParameters", new InvalidCalculateHashCodeMethodsContainer());
    }

    @Test
    public void fail_whenCalculateHashCodeMethodIsPublic() {
        expectException(IllegalArgumentException.class, CACHED_HASHCODE, MALFORMED_CALCULATEHASHCODEMETHOD, "visible");
        EqualsVerifier.forClass(InvalidCalculateHashCodeMethodsContainer.class)
                .withCachedHashCode("cachedHashCode", "visible", new InvalidCalculateHashCodeMethodsContainer());
    }

    @Test
    public void succeed_whenCalculateHashCodeMethodAndCalcHashCodeFieldAreProtected() {
        EqualsVerifier.forClass(ObjectWithProtectedCalculateHashCodeMembers.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithProtectedCalculateHashCodeMembers(SOME_NAME))
                .verify();
    }

    @Test
    public void succeed_whenCalculateHashCodeMethodAndCalcHashCodeFieldHaveDefaultVisibility() {
        EqualsVerifier.forClass(ObjectWithDefaultVisibilityCalculateHashCodeMembers.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithDefaultVisibilityCalculateHashCodeMembers(SOME_NAME))
                .verify();
    }

    @Test
    public void fail_whenExampleIsNull() {
        expectFailure(CACHED_HASHCODE, "example cannot be null.");
        EqualsVerifier.forClass(ObjectWithUninitializedCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", null)
                .verify();
    }

    @Test
    public void fail_whenCachedHashCodeFieldIsNotInitialized() {
        expectFailure(CACHED_HASHCODE, "hashCode is not properly initialized.");
        EqualsVerifier.forClass(ObjectWithUninitializedCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithUninitializedCachedHashCode(SOME_NAME))
                .verify();
    }

    @Test
    public void fail_whenHashCodeIsZero() {
        expectFailure(CACHED_HASHCODE, "example.hashCode() cannot be zero. Please choose a different example.");
        EqualsVerifier.forClass(ObjectWithLegitimatelyZeroHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithLegitimatelyZeroHashCode(1))
                .verify();
    }

    @Test
    public void succeed_whenCachedHashCodeFieldIsNotInitialized_givenExampleIsNullAndNoExampleForCachedHashCodeIsSuppressed() {
        EqualsVerifier.forClass(ObjectWithUninitializedCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", null)
                .suppress(Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE)
                .verify();
    }

    @Test
    public void fail_whenNoExampleForCachedHashCodeIsSuppressedAndExampleIsNotNull() {
        expectFailure(CACHED_HASHCODE, "example must be null if " + Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE + " is suppressed");
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithValidCachedHashCode(SOME_NAME))
                .suppress(Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE)
                .verify();
    }

    @Test
    public void fail_whenCachedHashCodeIsValid_givenWithCachedHashCodeIsUsedAndWarningNonfinalFieldsIsSuppressed() {
        expectFailure(CACHED_HASHCODE, "EqualsVerifier can only check cached hashCodes for immutable classes.");
        EqualsVerifier.forClass(ObjectWithValidCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithValidCachedHashCode(SOME_NAME))
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenCachedHashCodeIsLazilyInitialized_givenItIsValid() {
        EqualsVerifier.forClass(ObjectWithLazilyInitializedCachedHashCode.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithLazilyInitializedCachedHashCode(SOME_NAME))
                .verify();
    }

    @Test
    public void succeed_whenCachedHashCodeIsLazilyInitializedAndCachedHashCodeFieldIsCalledInEquals_givenItIsValid() {
        EqualsVerifier.forClass(ObjectWithLazyCachedHashCodeAndFieldIsCalledInEquals.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithLazyCachedHashCodeAndFieldIsCalledInEquals(SOME_NAME))
                .verify();
    }

    @Test
    public void succeed_whenCachedHashCodeIsLazilyInitializedAndHashCodeMethodIsCalledInEquals_givenItIsValid() {
        EqualsVerifier.forClass(ObjectWithLazyCachedHashCodeAndMethodIsCalledInEquals.class)
                .withCachedHashCode("cachedHashCode", "calcHashCode", new ObjectWithLazyCachedHashCodeAndMethodIsCalledInEquals(SOME_NAME))
                .verify();
    }

    static class ObjectWithValidCachedHashCode {
        @Nonnull private final String name;
        private final int cachedHashCode;

        public ObjectWithValidCachedHashCode(String name) {
            this.name = name;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithValidCachedHashCode)) {
                return false;
            }
            ObjectWithValidCachedHashCode that = (ObjectWithValidCachedHashCode)obj;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            return cachedHashCode;
        }

        private int calcHashCode() {
            return name.hashCode();
        }
    }

    static final class Subclass extends ObjectWithValidCachedHashCode {
        public Subclass(String name) { super(name); }
    }

    static class ObjectWithInvalidCachedHashCode {
        @Nonnull private final String name;
        private final int cachedHashCode;

        public ObjectWithInvalidCachedHashCode(String name) {
            this.name = name;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithInvalidCachedHashCode)) {
                return false;
            }
            ObjectWithInvalidCachedHashCode that = (ObjectWithInvalidCachedHashCode)obj;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            return cachedHashCode;
        }

        private int calcHashCode() {
            return 3;
        }
    }

    @SuppressWarnings("unused")
    static class InvalidCachedHashCodeFieldContainer {
        public int publicField;
        private String notAnInt;

        private int calculateHashCode() { return -1; }
    }

    @SuppressWarnings("unused")
    static class InvalidCalculateHashCodeMethodsContainer {
        private int cachedHashCode;

        public int visible() { return -1; }
        private String notAnInt() { return "wrong"; }
        private int takesParameters(int x) { return x; }
    }

    static class ObjectWithProtectedCalculateHashCodeMembers {
        protected final int cachedHashCode;
        @Nonnull private final String name;

        public ObjectWithProtectedCalculateHashCodeMembers(String name) {
            this.name = name;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithProtectedCalculateHashCodeMembers)) {
                return false;
            }
            ObjectWithProtectedCalculateHashCodeMembers that = (ObjectWithProtectedCalculateHashCodeMembers)obj;
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

    static class ObjectWithDefaultVisibilityCalculateHashCodeMembers {
        final int cachedHashCode;
        @Nonnull private final String name;

        public ObjectWithDefaultVisibilityCalculateHashCodeMembers(String name) {
            this.name = name;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithDefaultVisibilityCalculateHashCodeMembers)) {
                return false;
            }
            ObjectWithDefaultVisibilityCalculateHashCodeMembers that = (ObjectWithDefaultVisibilityCalculateHashCodeMembers)obj;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            return cachedHashCode;
        }

        /* default */ int calcHashCode() {
            return name.hashCode();
        }
    }

    static class ObjectWithUninitializedCachedHashCode {
        @Nonnull private final String name;
        private final int cachedHashCode;

        public ObjectWithUninitializedCachedHashCode(String name) {
            this.name = name;
            this.cachedHashCode = 0;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithUninitializedCachedHashCode)) {
                return false;
            }
            ObjectWithUninitializedCachedHashCode that = (ObjectWithUninitializedCachedHashCode)obj;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            return cachedHashCode;
        }

        @SuppressWarnings("unused")
        private int calcHashCode() {
            return name.hashCode();
        }
    }

    static class ObjectWithLegitimatelyZeroHashCode {
        @Nonnull private final int number;
        private final int cachedHashCode;

        public ObjectWithLegitimatelyZeroHashCode(int number) {
            this.number = number;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithLegitimatelyZeroHashCode)) {
                return false;
            }
            ObjectWithLegitimatelyZeroHashCode that = (ObjectWithLegitimatelyZeroHashCode)obj;
            return number == that.number;
        }

        @Override
        public final int hashCode() {
            return cachedHashCode;
        }

        private int calcHashCode() {
            return number - 1;
        }
    }

    static class ObjectWithLazilyInitializedCachedHashCode {
        @Nonnull private final String name;
        private int cachedHashCode;

        public ObjectWithLazilyInitializedCachedHashCode(String name) {
            this.name = name;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithLazilyInitializedCachedHashCode)) {
                return false;
            }
            ObjectWithLazilyInitializedCachedHashCode that = (ObjectWithLazilyInitializedCachedHashCode)obj;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            return calcHashCode();
        }

        private int calcHashCode() {
            if (cachedHashCode == 0) {
                cachedHashCode = name.hashCode();
            }
            return cachedHashCode;
        }
    }

    static class ObjectWithLazyCachedHashCodeAndFieldIsCalledInEquals {
        @Nonnull private final String name;
        private int cachedHashCode;

        public ObjectWithLazyCachedHashCodeAndFieldIsCalledInEquals(String name) {
            this.name = name;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithLazyCachedHashCodeAndFieldIsCalledInEquals)) {
                return false;
            }
            ObjectWithLazyCachedHashCodeAndFieldIsCalledInEquals that = (ObjectWithLazyCachedHashCodeAndFieldIsCalledInEquals)obj;
            if (cachedHashCode != that.cachedHashCode) {
                return false;
            }
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            if (cachedHashCode == 0) {
                cachedHashCode = calcHashCode();
            }
            return cachedHashCode;
        }

        private int calcHashCode() {
            return name.hashCode();
        }
    }

    static class ObjectWithLazyCachedHashCodeAndMethodIsCalledInEquals {
        @Nonnull private final String name;
        private int cachedHashCode;

        public ObjectWithLazyCachedHashCodeAndMethodIsCalledInEquals(String name) {
            this.name = name;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ObjectWithLazyCachedHashCodeAndMethodIsCalledInEquals)) {
                return false;
            }
            if (hashCode() != obj.hashCode()) {
                return false;
            }
            ObjectWithLazyCachedHashCodeAndMethodIsCalledInEquals that = (ObjectWithLazyCachedHashCodeAndMethodIsCalledInEquals)obj;
            return name.equals(that.name);
        }

        @Override
        public final int hashCode() {
            if (cachedHashCode == 0) {
                cachedHashCode = calcHashCode();
            }
            return cachedHashCode;
        }

        private int calcHashCode() {
            return name.hashCode();
        }
    }
}
