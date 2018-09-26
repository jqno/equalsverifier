package nl.jqno.equalsverifier.integration.basic_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Objects;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class TransitivityTest extends ExpectedExceptionTestBase {
    @Test
    public void succeed_whenEqualityForTwoFieldsIsCombinedUsingAnd() {
        EqualsVerifier.forClass(TwoFieldsUsingAnd.class)
                .verify();
    }

    @Test
    public void fail_whenEqualityForTwoFieldsIsCombinedUsingOr() {
        expectFailure("Transitivity", "two of these three instances are equal to each other, so the third one should be, too",
                TwoFieldsUsingOr.class.getSimpleName());
        EqualsVerifier.forClass(TwoFieldsUsingOr.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void succeed_whenEqualityForThreeFieldsIsCombinedUsingAnd() {
        EqualsVerifier.forClass(ThreeFieldsUsingAnd.class)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingOr() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(ThreeFieldsUsingOr.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingOr_givenRelaxedEqualExamples() {
        ThreeFieldsUsingOr one = new ThreeFieldsUsingOr("a", "1", "alpha");
        ThreeFieldsUsingOr two = new ThreeFieldsUsingOr("b", "1", "alpha");
        ThreeFieldsUsingOr three = new ThreeFieldsUsingOr("c", "1", "alpha");
        ThreeFieldsUsingOr other = new ThreeFieldsUsingOr("d", "4", "delta");

        expectFailure("Transitivity");
        EqualsVerifier.forRelaxedEqualExamples(one, two, three)
                .andUnequalExample(other)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingAndAndOr() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(ThreeFieldsUsingAndOr.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingOrAndAnd() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(ThreeFieldsUsingOrAnd.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForFiveFieldsIsCombinedUsingOr() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(FiveFieldsUsingOr.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForFiveFieldsIsCombinedUsingAndsAndOrs() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(FiveFieldsUsingAndsAndOrs.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Ignore("This class is not transitive, and it should fail. See issue 78.")
    @Test
    public void fail_whenInstancesAreEqualIfAtLeastTwoFieldsAreEqual() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(AtLeast2FieldsAreEqual.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    static final class TwoFieldsUsingAnd {
        private final String f;
        private final String g;

        public TwoFieldsUsingAnd(String f, String g) { this.f = f; this.g = g; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TwoFieldsUsingAnd)) {
                return false;
            }
            TwoFieldsUsingAnd other = (TwoFieldsUsingAnd)obj;
            return Objects.equals(f, other.f) && Objects.equals(g, other.g);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class TwoFieldsUsingOr {
        private final String f;
        private final String g;

        public TwoFieldsUsingOr(String f, String g) { this.f = f; this.g = g; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TwoFieldsUsingOr)) {
                return false;
            }
            TwoFieldsUsingOr other = (TwoFieldsUsingOr)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class ThreeFieldsUsingAnd {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingAnd(String f, String g, String h) {
            this.f = f; this.g = g; this.h = h;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingAnd)) {
                return false;
            }
            ThreeFieldsUsingAnd other = (ThreeFieldsUsingAnd)obj;
            return Objects.equals(f, other.f) && Objects.equals(g, other.g) && Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class ThreeFieldsUsingOr {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingOr(String f, String g, String h) {
            this.f = f; this.g = g; this.h = h;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingOr)) {
                return false;
            }
            ThreeFieldsUsingOr other = (ThreeFieldsUsingOr)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) || Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class ThreeFieldsUsingAndOr {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingAndOr(String f, String g, String h) {
            this.f = f; this.g = g; this.h = h;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingAndOr)) {
                return false;
            }
            ThreeFieldsUsingAndOr other = (ThreeFieldsUsingAndOr)obj;
            return Objects.equals(f, other.f) && Objects.equals(g, other.g) || Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class ThreeFieldsUsingOrAnd {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingOrAnd(String f, String g, String h) {
            this.f = f; this.g = g; this.h = h;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingOrAnd)) {
                return false;
            }
            ThreeFieldsUsingOrAnd other = (ThreeFieldsUsingOrAnd)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) && Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class FiveFieldsUsingOr {
        private final String f;
        private final String g;
        private final String h;
        private final String i;
        private final String j;

        public FiveFieldsUsingOr(String f, String g, String h, String i, String j) {
            this.f = f; this.g = g; this.h = h; this.i = i; this.j = j;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FiveFieldsUsingOr)) {
                return false;
            }
            FiveFieldsUsingOr other = (FiveFieldsUsingOr)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) ||
                    Objects.equals(h, other.h) || Objects.equals(i, other.i) || Objects.equals(j, other.j);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class FiveFieldsUsingAndsAndOrs {
        private final String f;
        private final String g;
        private final String h;
        private final String i;
        private final String j;

        public FiveFieldsUsingAndsAndOrs(String f, String g, String h, String i, String j) {
            this.f = f; this.g = g; this.h = h; this.i = i; this.j = j;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FiveFieldsUsingAndsAndOrs)) {
                return false;
            }
            FiveFieldsUsingAndsAndOrs other = (FiveFieldsUsingAndsAndOrs)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) &&
                    Objects.equals(h, other.h) || Objects.equals(i, other.i) && Objects.equals(j, other.j);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class AtLeast2FieldsAreEqual {
        private final int i;
        private final int j;
        private final int k;
        private final int l;

        public AtLeast2FieldsAreEqual(int i, int j, int k, int l) {
            this.i = i; this.j = j; this.k = k; this.l = l;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AtLeast2FieldsAreEqual)) {
                return false;
            }
            AtLeast2FieldsAreEqual other = (AtLeast2FieldsAreEqual)obj;
            int x = 0;
            if (i == other.i) { x++; }
            if (j == other.j) { x++; }
            if (k == other.k) { x++; }
            if (l == other.l) { x++; }
            return x >= 2;
        }

        @Override public int hashCode() { return 42; }
    }
}
