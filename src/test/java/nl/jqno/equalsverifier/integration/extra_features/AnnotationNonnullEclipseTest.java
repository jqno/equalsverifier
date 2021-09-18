package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse.NonnullEclipseOnPackage;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.eclipse.jdt.annotation.DefaultLocation;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

public class AnnotationNonnullEclipseTest {

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseAnnotationOnFieldType() {
        EqualsVerifier.forClass(NonnullTypeUse.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullEclipseOnClass.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnPackage() {
        EqualsVerifier.forClass(NonnullEclipseOnPackage.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnOuterClass() {
        EqualsVerifier.forClass(NonnullEclipseOuter.FInner.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnNestedOuterClass() {
        EqualsVerifier.forClass(NonnullEclipseOuter.FMiddle.FInnerInner.class).verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(NonnullEclipseWithNullableOnClass.class).verify())
            .assertFailure()
            .assertMessageContains(
                "Non-nullity",
                "equals throws NullPointerException",
                "on field o"
            );
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClassAndWarningSuppressed() {
        EqualsVerifier
            .forClass(NonnullEclipseWithNullableOnClass.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

    @Test
    public void succeed_whenEqualsChecksForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
        EqualsVerifier
            .forClass(NonnullEclipseWithNullableOnClassAndNullCheckInEquals.class)
            .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnPackage() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(NonnullEclipseWithNullableOnPackageAndNullCheckInEquals.class)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Non-nullity",
                "equals throws NullPointerException",
                "on field o"
            );
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnPackageAndWarningIsSuppressed() {
        EqualsVerifier
            .forClass(NonnullEclipseWithNullableOnPackageAndNullCheckInEquals.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationButInapplicableLocationOnClass() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(NonnullEclipseWithInapplicableLocationOnClass.class)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Non-nullity",
                "equals throws NullPointerException",
                "on field o"
            );
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationWithApplicableLocationOnClass() {
        EqualsVerifier.forClass(NonnullEclipseWithApplicableLocationOnClass.class).verify();
    }

    static final class NonnullTypeUse {

        @NonNull
        private final Object o;

        public NonnullTypeUse(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullTypeUse)) {
                return false;
            }
            NonnullTypeUse other = (NonnullTypeUse) obj;
            return o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }

    @NonNullByDefault
    static final class NonnullEclipseOnClass {

        private final Object o;

        public NonnullEclipseOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseOnClass)) {
                return false;
            }
            NonnullEclipseOnClass other = (NonnullEclipseOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }

    @NonNullByDefault
    static final class NonnullEclipseOuter {

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

    @NonNullByDefault
    static final class NonnullEclipseWithNullableOnClass {

        @Nullable
        private final Object o;

        public NonnullEclipseWithNullableOnClass(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithNullableOnClass)) {
                return false;
            }
            NonnullEclipseWithNullableOnClass other = (NonnullEclipseWithNullableOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }

    @NonNullByDefault
    static final class NonnullEclipseWithNullableOnClassAndNullCheckInEquals {

        @Nullable
        private final Object o;

        public NonnullEclipseWithNullableOnClassAndNullCheckInEquals(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithNullableOnClassAndNullCheckInEquals)) {
                return false;
            }
            NonnullEclipseWithNullableOnClassAndNullCheckInEquals other = (NonnullEclipseWithNullableOnClassAndNullCheckInEquals) obj;
            return o == null ? other.o == null : o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }

    static final class NonnullEclipseWithNullableOnPackageAndNullCheckInEquals {

        @Nullable
        private final Object o;

        public NonnullEclipseWithNullableOnPackageAndNullCheckInEquals(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithNullableOnPackageAndNullCheckInEquals)) {
                return false;
            }
            NonnullEclipseWithNullableOnPackageAndNullCheckInEquals other = (NonnullEclipseWithNullableOnPackageAndNullCheckInEquals) obj;
            return o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }

    @NonNullByDefault({ DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE })
    static final class NonnullEclipseWithInapplicableLocationOnClass {

        private final Object o;

        public NonnullEclipseWithInapplicableLocationOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithInapplicableLocationOnClass)) {
                return false;
            }
            NonnullEclipseWithInapplicableLocationOnClass other = (NonnullEclipseWithInapplicableLocationOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }

    @NonNullByDefault({ DefaultLocation.FIELD, DefaultLocation.RETURN_TYPE })
    static final class NonnullEclipseWithApplicableLocationOnClass {

        private final Object o;

        public NonnullEclipseWithApplicableLocationOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithApplicableLocationOnClass)) {
                return false;
            }
            NonnullEclipseWithApplicableLocationOnClass other = (NonnullEclipseWithApplicableLocationOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }
}
