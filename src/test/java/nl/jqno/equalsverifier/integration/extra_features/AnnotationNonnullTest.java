package nl.jqno.equalsverifier.integration.extra_features;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom.NonnullFindbugs1xCustomOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom.NonnullFindbugs1xWithCheckForNullOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax.NonnullFindbugs1xJavaxOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax.NonnullFindbugs1xWithNullableOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.custom.NonnullJsr305CustomOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.custom.NonnullJsr305WithNullableOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.inapplicable.NonnullJsr305InapplicableOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.javax.NonnullJsr305JavaxOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.javax.NonnullJsr305WithCheckForNullOnPackage;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.*;
import org.junit.Test;

public class AnnotationNonnullTest extends ExpectedExceptionTestBase {
    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotation() {
        EqualsVerifier.forClass(NonnullManual.class).verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotationButOneDoesnt() {
        expectFailureWithCause(
                NullPointerException.class,
                "Non-nullity",
                "equals throws NullPointerException",
                "on field noAnnotation");
        EqualsVerifier.forClass(NonnullManualMissedOne.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotationInSuperclass() {
        EqualsVerifier.forClass(SubclassNonnullManual.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithJavaxNonnullAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullFindbugs1xJavaxOnClass.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithCustomNonnullAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullFindbugs1xCustomOnClass.class).verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEmptyFindbugs1xDefaultAnnotationOnClass() {
        expectFailure("Non-nullity");
        EqualsVerifier.forClass(EmptyFindbugs1xCustomOnClass.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithoutNonnullAnnotationOnClass() {
        expectFailure("Non-nullity");
        EqualsVerifier.forClass(NotNonnullFindbugs1xCustomOnClass.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithJavaxNonnullAnnotationOnPackage() {
        EqualsVerifier.forClass(NonnullFindbugs1xJavaxOnPackage.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithCustomNonnullAnnotationOnPackage() {
        EqualsVerifier.forClass(NonnullFindbugs1xCustomOnPackage.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationForFields() {
        EqualsVerifier.forClass(NonnullFindbugs1xForFields.class).verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationForParameters() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(NonnullFindbugs1xForParameters.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationOnOuterClass() {
        EqualsVerifier.forClass(NonnullFindbugs1xOuter.FInner.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationOnNestedOuterClass() {
        EqualsVerifier.forClass(NonnullFindbugs1xOuter.FMiddle.FInnerInner.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndNullableAnnotationOnClass() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullFindbugs1xWithNullableOnClass.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndCheckForNullAnnotationOnClass() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullFindbugs1xWithCheckForNullOnClass.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndNullableAnnotationOnPackage() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullFindbugs1xWithNullableOnPackage.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndCheckForNullAnnotationOnPackage() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullFindbugs1xWithCheckForNullOnPackage.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoenstCheckForNull_givenJsr305DefaultAndCheckForNullOnPackageAndWarningSuppressed() {
        EqualsVerifier.forClass(NonnullFindbugs1xWithCheckForNullOnPackage.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenEqualsChecksForNull_givenJsr305DefaultAndNullableAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals.class)
                .verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithJavaxNonnullAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullJsr305JavaxOnClass.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithCustomNonnullAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullJsr305CustomOnClass.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithInapplicableTypeQualifierDefaultOnClass() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(NonnullJsr305InapplicableOnClass.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithJavaxNonnullAnnotationOnPackage() {
        EqualsVerifier.forClass(NonnullJsr305JavaxOnPackage.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithCustomNonnullAnnotationOnPackage() {
        EqualsVerifier.forClass(NonnullJsr305CustomOnPackage.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithInapplicableTypeQualifierDefaultOnPackage() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(NonnullJsr305InapplicableOnPackage.class).verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndNullableAnnotationOnClass() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullJsr305WithNullableOnClass.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationOnOuterClass() {
        EqualsVerifier.forClass(NonnullJsr305Outer.JInner.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationOnNestedOuterClass() {
        EqualsVerifier.forClass(NonnullJsr305Outer.JMiddle.JInnerInner.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndCheckForNullAnnotationOnClass() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullJsr305WithCheckForNullOnClass.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndNullableAnnotationOnPackage() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullJsr305WithNullableOnPackage.class).verify();
    }

    @Test
    public void
            fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndCheckForNullAnnotationOnPackage() {
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
        EqualsVerifier.forClass(NonnullJsr305WithCheckForNullOnPackage.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsDoenstCheckForNull_givenJsr305DefaultAndNullableOnPackageAndWarningSuppressed() {
        EqualsVerifier.forClass(NonnullJsr305WithNullableOnPackage.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void
            succeed_whenEqualsChecksForNull_givenJsr305DefaultAndCheckForNullAnnotationOnClass() {
        EqualsVerifier.forClass(NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals.class)
                .verify();
    }

    static class NonnullManual {
        @Nonnull private final Object o;
        @NonNull private final Object p;
        @NotNull private final Object q;

        public NonnullManual(Object o, Object p, Object q) {
            this.o = o;
            this.p = p;
            this.q = q;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonnullManual)) {
                return false;
            }
            NonnullManual other = (NonnullManual) obj;
            return o.equals(other.o) && p.equals(other.p) && q.equals(other.q);
        }

        @Override
        public final int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class SubclassNonnullManual extends NonnullManual {
        public SubclassNonnullManual(Object o, Object p, Object q) {
            super(o, p, q);
        }
    }

    static final class NonnullManualMissedOne {
        @Nonnull private final Object o;
        // No annotation
        private final Object noAnnotation;
        @Nonnull private final Object q;

        public NonnullManualMissedOne(Object o, Object p, Object q) {
            this.o = o;
            this.noAnnotation = p;
            this.q = q;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullManualMissedOne)) {
                return false;
            }
            NonnullManualMissedOne other = (NonnullManualMissedOne) obj;
            return o.equals(other.o)
                    && noAnnotation.equals(other.noAnnotation)
                    && q.equals(other.q);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation(Nonnull.class)
    static final class NonnullFindbugs1xJavaxOnClass {
        private final Object o;

        public NonnullFindbugs1xJavaxOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullFindbugs1xJavaxOnClass)) {
                return false;
            }
            NonnullFindbugs1xJavaxOnClass other = (NonnullFindbugs1xJavaxOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation(NotNull.class)
    static final class NonnullFindbugs1xCustomOnClass {
        private final Object o;

        public NonnullFindbugs1xCustomOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullFindbugs1xCustomOnClass)) {
                return false;
            }
            NonnullFindbugs1xCustomOnClass other = (NonnullFindbugs1xCustomOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation({})
    static final class EmptyFindbugs1xCustomOnClass {
        private final Object o;

        public EmptyFindbugs1xCustomOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EmptyFindbugs1xCustomOnClass)) {
                return false;
            }
            EmptyFindbugs1xCustomOnClass other = (EmptyFindbugs1xCustomOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation(Immutable.class)
    static final class NotNonnullFindbugs1xCustomOnClass {
        private final Object o;

        public NotNonnullFindbugs1xCustomOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NotNonnullFindbugs1xCustomOnClass)) {
                return false;
            }
            NotNonnullFindbugs1xCustomOnClass other = (NotNonnullFindbugs1xCustomOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotationForFields(NotNull.class)
    static final class NonnullFindbugs1xForFields {
        private final Object o;

        public NonnullFindbugs1xForFields(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullFindbugs1xForFields)) {
                return false;
            }
            NonnullFindbugs1xForFields other = (NonnullFindbugs1xForFields) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters(NotNull.class)
    static final class NonnullFindbugs1xForParameters {
        private final Object o;

        public NonnullFindbugs1xForParameters(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullFindbugs1xForParameters)) {
                return false;
            }
            NonnullFindbugs1xForParameters other = (NonnullFindbugs1xForParameters) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation(Nonnull.class)
    static class NonnullFindbugs1xOuter {
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
                return defaultHashCode(this);
            }
        }

        static class FMiddle {
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
                    return defaultHashCode(this);
                }
            }
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation(Nonnull.class)
    static final class NonnullFindbugs1xWithNullableOnClass {
        private final Object o;
        @Nullable private final Object p;

        public NonnullFindbugs1xWithNullableOnClass(Object o, Object p) {
            this.o = o;
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullFindbugs1xWithNullableOnClass)) {
                return false;
            }
            NonnullFindbugs1xWithNullableOnClass other = (NonnullFindbugs1xWithNullableOnClass) obj;
            return o.equals(other.o) && p.equals(other.p);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation(Nonnull.class)
    static final class NonnullFindbugs1xWithCheckForNullOnClass {
        private final Object o;
        @CheckForNull private final Object p;

        public NonnullFindbugs1xWithCheckForNullOnClass(Object o, Object p) {
            this.o = o;
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullFindbugs1xWithCheckForNullOnClass)) {
                return false;
            }
            NonnullFindbugs1xWithCheckForNullOnClass other =
                    (NonnullFindbugs1xWithCheckForNullOnClass) obj;
            return o.equals(other.o) && p.equals(other.p);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @edu.umd.cs.findbugs.annotations.DefaultAnnotation(Nonnull.class)
    static final class NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals {
        private final Object o;
        @Nullable private final Object p;

        public NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals(Object o, Object p) {
            this.o = o;
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals)) {
                return false;
            }
            NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals other =
                    (NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals) obj;
            return o.equals(other.o) && (p == null ? other.p == null : p.equals(other.p));
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultNonnullJavax
    static final class NonnullJsr305JavaxOnClass {
        private final Object o;

        public NonnullJsr305JavaxOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullJsr305JavaxOnClass)) {
                return false;
            }
            NonnullJsr305JavaxOnClass other = (NonnullJsr305JavaxOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultNonnullCustom
    static final class NonnullJsr305CustomOnClass {
        private final Object o;

        public NonnullJsr305CustomOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullJsr305CustomOnClass)) {
                return false;
            }
            NonnullJsr305CustomOnClass other = (NonnullJsr305CustomOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultNonnullInapplicable
    static final class NonnullJsr305InapplicableOnClass {
        private final Object o;

        public NonnullJsr305InapplicableOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullJsr305InapplicableOnClass)) {
                return false;
            }
            NonnullJsr305InapplicableOnClass other = (NonnullJsr305InapplicableOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultNonnullJavax
    static class NonnullJsr305Outer {
        static final class JInner {
            private final Object o;

            public JInner(Object o) {
                this.o = o;
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof JInner)) {
                    return false;
                }
                JInner other = (JInner) obj;
                return o.equals(other.o);
            }

            @Override
            public int hashCode() {
                return defaultHashCode(this);
            }
        }

        static class JMiddle {
            static final class JInnerInner {
                private final Object o;

                public JInnerInner(Object o) {
                    this.o = o;
                }

                @Override
                public boolean equals(Object obj) {
                    if (!(obj instanceof JInnerInner)) {
                        return false;
                    }
                    JInnerInner other = (JInnerInner) obj;
                    return o.equals(other.o);
                }

                @Override
                public int hashCode() {
                    return defaultHashCode(this);
                }
            }
        }
    }

    @DefaultNonnullJavax
    static final class NonnullJsr305WithNullableOnClass {
        private final Object o;
        @Nullable private final Object p;

        public NonnullJsr305WithNullableOnClass(Object o, Object p) {
            this.o = o;
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullJsr305WithNullableOnClass)) {
                return false;
            }
            NonnullJsr305WithNullableOnClass other = (NonnullJsr305WithNullableOnClass) obj;
            return o.equals(other.o) && p.equals(other.p);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultNonnullJavax
    static final class NonnullJsr305WithCheckForNullOnClass {
        private final Object o;
        @CheckForNull private final Object p;

        public NonnullJsr305WithCheckForNullOnClass(Object o, Object p) {
            this.o = o;
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullJsr305WithCheckForNullOnClass)) {
                return false;
            }
            NonnullJsr305WithCheckForNullOnClass other = (NonnullJsr305WithCheckForNullOnClass) obj;
            return o.equals(other.o) && p.equals(other.p);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultNonnullJavax
    static final class NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals {
        private final Object o;
        @CheckForNull private final Object p;

        public NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals(Object o, Object p) {
            this.o = o;
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals)) {
                return false;
            }
            NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals other =
                    (NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals) obj;
            return o.equals(other.o) && (p == null ? other.p == null : p.equals(other.p));
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
