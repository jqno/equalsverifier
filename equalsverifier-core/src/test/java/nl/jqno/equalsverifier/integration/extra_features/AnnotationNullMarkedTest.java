package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jspecify.NullMarkedOnPackage;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.annotations.org.jspecify.annotations.NullMarked;
import nl.jqno.equalsverifier_testhelpers.annotations.org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class AnnotationNullMarkedTest {

    @Test
    void succeed_whenEqualsDoesntCheckForNull_givenNullMarkedAnnotationOnClass() {
        EqualsVerifier.forClass(NullMarkedOnClass.class).verify();
    }

    @Test
    void succeed_whenEqualsDoesntCheckForNull_givenNullMarkedAnnotationOnPackage() {
        EqualsVerifier.forClass(NullMarkedOnPackage.class).verify();
    }

    @Test
    void succeed_whenEqualsDoesntCheckForNull_givenNullMarkedAnnotationOnOuterClass() {
        EqualsVerifier.forClass(NullMarkedOuter.FInner.class).verify();
    }

    @Test
    void succeed_whenEqualsDoesntCheckForNull_givenNullMarkedAnnotationOnNestedOuterClass() {
        EqualsVerifier.forClass(NullMarkedOuter.FMiddle.FInnerInner.class).verify();
    }

    @Test
    void fail_whenEqualsDoesntCheckForNull_givenNullMarkedAndNullableAnnotationOnClass() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NullMarkedWithNullableOnClass.class).verify())
                .assertFailure()
                .assertMessageContains("Non-nullity", "equals throws NullPointerException", "'this' object's field o");
    }

    @Test
    void succeed_whenEqualsDoesntCheckForNull_givenNullMarkedAndNullableAnnotationOnClassAndWarningSuppressed() {
        EqualsVerifier.forClass(NullMarkedWithNullableOnClass.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void succeed_whenEqualsChecksForNull_givenNullMarkedAndNullableAnnotationOnClass() {
        EqualsVerifier.forClass(NullMarkedWithNullableOnClassAndNullCheckInEquals.class).verify();
    }

    @NullMarked
    static final class NullMarkedOnClass {

        private final Object o;

        public NullMarkedOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NullMarkedOnClass)) {
                return false;
            }
            NullMarkedOnClass other = (NullMarkedOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }

    @NullMarked
    static final class NullMarkedOuter {

        static final class FInner {

            private final Object o;

            public FInner(Object o) {
                this.o = o;
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof FInner)) {
                    return false;
                }
                FInner other = (FInner) obj;
                return o.equals(other.o);
            }

            @Override
            public int hashCode() {
                return Objects.hash(o);
            }
        }

        static final class FMiddle {

            static final class FInnerInner {

                private final Object o;

                public FInnerInner(Object o) {
                    this.o = o;
                }

                @Override
                public boolean equals(Object obj) {
                    if (!(obj instanceof FInnerInner)) {
                        return false;
                    }
                    FInnerInner other = (FInnerInner) obj;
                    return o.equals(other.o);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(o);
                }
            }
        }
    }

    @NullMarked
    static final class NullMarkedWithNullableOnClass {

        @Nullable
        private final Object o;

        public NullMarkedWithNullableOnClass(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NullMarkedWithNullableOnClass)) {
                return false;
            }
            NullMarkedWithNullableOnClass other = (NullMarkedWithNullableOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }

    @NullMarked
    static final class NullMarkedWithNullableOnClassAndNullCheckInEquals {

        @Nullable
        private final Object o;

        public NullMarkedWithNullableOnClassAndNullCheckInEquals(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NullMarkedWithNullableOnClassAndNullCheckInEquals)) {
                return false;
            }
            NullMarkedWithNullableOnClassAndNullCheckInEquals other =
                    (NullMarkedWithNullableOnClassAndNullCheckInEquals) obj;
            return o == null ? other.o == null : o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }
}
