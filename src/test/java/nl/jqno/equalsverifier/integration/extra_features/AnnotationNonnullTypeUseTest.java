package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse.NonnullEclipseOnPackage;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.eclipse.jdt.annotation.DefaultLocation;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import java.util.Objects;

public class AnnotationNonnullTypeUseTest extends IntegrationTestBase {
    @Test
    public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() {
        EqualsVerifier.forClass(NonnullManual.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullEclipseOnClass.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnPackage() {
        EqualsVerifier.forClass(NonnullEclipseOnPackage.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnOuterClass() {
        EqualsVerifier.forClass(NonnullEclipseOuter.FInner.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnNestedOuterClass() {
        EqualsVerifier.forClass(NonnullEclipseOuter.FMiddle.FInnerInner.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(NonnullEclipseWithNullableOnClass.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClassAndWarningSuppressed() {
        EqualsVerifier.forClass(NonnullEclipseWithNullableOnClass.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenEqualsChecksForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullEclipseWithNullableOnClassAndNullCheckInEquals.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnPackage() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(NonnullEclipseWithNullableOnPackageAndNullCheckInEquals.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnPackageAndWarningIsSuppressed() {
        EqualsVerifier.forClass(NonnullEclipseWithNullableOnPackageAndNullCheckInEquals.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationButInapplicableLocationOnClass() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(NonnullEclipseWithInapplicableLocationOnClass.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationWithApplicableLocationOnClass() {
        EqualsVerifier.forClass(NonnullEclipseWithApplicableLocationOnClass.class)
                .verify();
    }

    static final class NonnullManual {
        private final @NonNull Object o;

        public NonnullManual(Object o) { this.o = o; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullManual)) {
                return false;
            }
            NonnullManual other = (NonnullManual)obj;
            return o.equals(other.o);
        }

        @Override public final int hashCode() { return Objects.hash(o); }
    }

    @NonNullByDefault
    static final class NonnullEclipseOnClass {
        private final Object o;

        public NonnullEclipseOnClass(Object o) { this.o = o; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseOnClass)) {
                return false;
            }
            NonnullEclipseOnClass other = (NonnullEclipseOnClass)obj;
            return o.equals(other.o);
        }

        @Override public int hashCode() { return Objects.hash(o); }
    }

    @NonNullByDefault
    static final class NonnullEclipseOuter {
        static final class FInner {
            private final Object o;

            public FInner(Object o) { this.o = o; }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof FInner)) {
                    return false;
                }
                FInner other = (FInner)obj;
                return o.equals(other.o);
            }

            @Override public int hashCode() { return Objects.hash(o); }
        }

        static final class FMiddle {
            static final class FInnerInner {
                private final Object o;

                public FInnerInner(Object o) { this.o = o; }

                @Override
                public boolean equals(Object obj) {
                    if (!(obj instanceof FInnerInner)) {
                        return false;
                    }
                    FInnerInner other = (FInnerInner)obj;
                    return o.equals(other.o);
                }

                @Override public int hashCode() { return Objects.hash(o); }
            }
        }
    }

    @NonNullByDefault
    static final class NonnullEclipseWithNullableOnClass {
        private final @Nullable Object o;

        public NonnullEclipseWithNullableOnClass(Object o) { this.o = o; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithNullableOnClass)) {
                return false;
            }
            NonnullEclipseWithNullableOnClass other = (NonnullEclipseWithNullableOnClass)obj;
            return o.equals(other.o);
        }

        @Override public final int hashCode() { return Objects.hash(o); }
    }

    @NonNullByDefault
    static final class NonnullEclipseWithNullableOnClassAndNullCheckInEquals {
        private final @Nullable Object o;

        public NonnullEclipseWithNullableOnClassAndNullCheckInEquals(Object o) { this.o = o; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithNullableOnClassAndNullCheckInEquals)) {
                return false;
            }
            NonnullEclipseWithNullableOnClassAndNullCheckInEquals other = (NonnullEclipseWithNullableOnClassAndNullCheckInEquals)obj;
            return o == null ? other.o == null : o.equals(other.o);
        }

        @Override public final int hashCode() { return Objects.hash(o); }
    }

    static final class NonnullEclipseWithNullableOnPackageAndNullCheckInEquals {
        private final @Nullable Object o;

        public NonnullEclipseWithNullableOnPackageAndNullCheckInEquals(Object o) { this.o = o; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithNullableOnPackageAndNullCheckInEquals)) {
                return false;
            }
            NonnullEclipseWithNullableOnPackageAndNullCheckInEquals other =
                    (NonnullEclipseWithNullableOnPackageAndNullCheckInEquals)obj;
            return o.equals(other.o);
        }

        @Override public final int hashCode() { return Objects.hash(o); }
    }

    @NonNullByDefault({ DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE })
    static final class NonnullEclipseWithInapplicableLocationOnClass {
        private final Object o;

        public NonnullEclipseWithInapplicableLocationOnClass(Object o) { this.o = o; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithInapplicableLocationOnClass)) {
                return false;
            }
            NonnullEclipseWithInapplicableLocationOnClass other = (NonnullEclipseWithInapplicableLocationOnClass)obj;
            return o.equals(other.o);
        }

        @Override public int hashCode() { return Objects.hash(o); }
    }

    @NonNullByDefault({ DefaultLocation.FIELD, DefaultLocation.RETURN_TYPE })
    static final class NonnullEclipseWithApplicableLocationOnClass {
        private final Object o;

        public NonnullEclipseWithApplicableLocationOnClass(Object o) { this.o = o; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullEclipseWithApplicableLocationOnClass)) {
                return false;
            }
            NonnullEclipseWithApplicableLocationOnClass other = (NonnullEclipseWithApplicableLocationOnClass)obj;
            return o.equals(other.o);
        }

        @Override public int hashCode() { return Objects.hash(o); }
    }
}
