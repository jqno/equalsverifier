package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.parametersarenonnullbydefault.ParametersAreNonnullByDefaultOnPackage;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class AnnotationParametersAreNonnullByDefaultTest {

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnClass() {
        EqualsVerifier.forClass(ParametersAreNonnullByDefaultOnClass.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnPackage() {
        EqualsVerifier.forClass(ParametersAreNonnullByDefaultOnPackage.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnOuterClass() {
        EqualsVerifier.forClass(ParametersAreNonnullByDefaultOuter.FInner.class).verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnNestedOuterClass() {
        EqualsVerifier
            .forClass(ParametersAreNonnullByDefaultOuter.FMiddle.FInnerInner.class)
            .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(ParametersAreNonnullByDefaultWithNullableOnClass.class)
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
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClassAndWarningSuppressed() {
        EqualsVerifier
            .forClass(ParametersAreNonnullByDefaultWithNullableOnClass.class)
            .suppress(Warning.NULL_FIELDS)
            .verify();
    }

    @Test
    public void succeed_whenEqualsChecksForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
        EqualsVerifier
            .forClass(ParamsNonnullByDefaultWithNullableOnClassAndNullCheckInEquals.class)
            .verify();
    }

    @ParametersAreNonnullByDefault
    static final class ParametersAreNonnullByDefaultOnClass {

        private final Object o;

        public ParametersAreNonnullByDefaultOnClass(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ParametersAreNonnullByDefaultOnClass)) {
                return false;
            }
            ParametersAreNonnullByDefaultOnClass other = (ParametersAreNonnullByDefaultOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }

    @ParametersAreNonnullByDefault
    static final class ParametersAreNonnullByDefaultOuter {

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

    @ParametersAreNonnullByDefault
    static final class ParametersAreNonnullByDefaultWithNullableOnClass {

        @Nullable
        private final Object o;

        public ParametersAreNonnullByDefaultWithNullableOnClass(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ParametersAreNonnullByDefaultWithNullableOnClass)) {
                return false;
            }
            ParametersAreNonnullByDefaultWithNullableOnClass other = (ParametersAreNonnullByDefaultWithNullableOnClass) obj;
            return o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }

    @ParametersAreNonnullByDefault
    static final class ParamsNonnullByDefaultWithNullableOnClassAndNullCheckInEquals {

        @Nullable
        private final Object o;

        public ParamsNonnullByDefaultWithNullableOnClassAndNullCheckInEquals(Object o) {
            this.o = o;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof ParamsNonnullByDefaultWithNullableOnClassAndNullCheckInEquals)) {
                return false;
            }
            ParamsNonnullByDefaultWithNullableOnClassAndNullCheckInEquals other = (ParamsNonnullByDefaultWithNullableOnClassAndNullCheckInEquals) obj;
            return o == null ? other.o == null : o.equals(other.o);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(o);
        }
    }
}
