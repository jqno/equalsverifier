package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class FloatAndDoubleTest {

    private static final String FLOAT = "Float: equals doesn't use Float.compare for field";
    private static final String DOUBLE = "Double: equals doesn't use Double.compare for field";
    private static final String REFLEXIVITY = "Reflexivity: == used instead of .equals()";

    @Test
    void fail_whenFloatsAreComparedByReference() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ComparePrimitiveFloatsByReference.class).verify())
                .assertFailure()
                .assertMessageContains(FLOAT, "f");
    }

    @Test
    void fail_whenObjectFloatsAreComparedByReference() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(CompareObjectFloatByReference.class).verify())
                .assertFailure()
                .assertMessageContains(REFLEXIVITY, "field: f");
    }

    @Test
    void succeed_whenFloatsAreComparedWithFloatCompare() {
        EqualsVerifier.forClass(CompareFloatCorrectly.class).verify();
    }

    @Test
    void succeed_whenObjectFloatsAreComparedWithEquals() {
        EqualsVerifier.forClass(CompareObjectFloatWithEquals.class).verify();
    }

    @Test
    void succeed_whenObjectFloatsAreComparedWithCompare() {
        EqualsVerifier.forClass(CompareObjectFloatWithCompare.class).verify();
    }

    @Test
    void succeed_whenFloatCannotBeNaN() {
        EqualsVerifier.forClass(FloatDontAllowNaN.class).verify();
    }

    @Test
    void fail_whenDoublesAreComparedByReference() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ComparePrimitiveDoubleByReference.class).verify())
                .assertFailure()
                .assertMessageContains(DOUBLE, "d");
    }

    @Test
    void fail_whenObjectDoublesAreComparedByReference() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(CompareObjectDoubleByReference.class).verify())
                .assertFailure()
                .assertMessageContains(REFLEXIVITY, "field: d");
    }

    @Test
    void succeed_whenDoublesAreComparedWithDoubleCompare() {
        EqualsVerifier.forClass(CompareDoubleCorrectly.class).verify();
    }

    @Test
    void succeed_whenObjectDoublesAreComparedWithEquals() {
        EqualsVerifier.forClass(CompareObjectDoubleWithEquals.class).verify();
    }

    @Test
    void succeed_whenObjectDoublesAreComparedWithCompare() {
        EqualsVerifier.forClass(CompareObjectDoubleWithCompare.class).verify();
    }

    @Test
    void succeed_whenDoubleCannotBeNaN() {
        EqualsVerifier.forClass(DoubleDontAllowNaN.class).verify();
    }

    static final class ComparePrimitiveFloatsByReference {

        private final float f;

        public ComparePrimitiveFloatsByReference(float f) {
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ComparePrimitiveFloatsByReference)) {
                return false;
            }
            return f == ((ComparePrimitiveFloatsByReference) obj).f;
        }

        @Override
        public int hashCode() {
            return Objects.hash(f);
        }
    }

    static final class CompareObjectFloatByReference {

        private final Float f;

        public CompareObjectFloatByReference(Float f) {
            this.f = f;
        }

        @Override
        @SuppressWarnings({ "BoxedPrimitiveEquality", "ReferenceEquality" })
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareObjectFloatByReference)) {
                return false;
            }
            return f == ((CompareObjectFloatByReference) obj).f;
        }

        @Override
        public int hashCode() {
            return Objects.hash(f);
        }
    }

    static final class CompareFloatCorrectly {

        private final float f;

        public CompareFloatCorrectly(float f) {
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareFloatCorrectly)) {
                return false;
            }
            return Float.compare(f, ((CompareFloatCorrectly) obj).f) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(f);
        }
    }

    static final class CompareObjectFloatWithEquals {

        private final Float f;

        public CompareObjectFloatWithEquals(Float f) {
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CompareObjectFloatWithEquals other
                    && (f == null ? other.f == null : f.equals(other.f));
        }

        @Override
        public int hashCode() {
            return Objects.hash(f);
        }
    }

    static final class CompareObjectFloatWithCompare {

        private final Float f;

        public CompareObjectFloatWithCompare(Float f) {
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CompareObjectFloatWithCompare other
                    && ((f == null || other.f == null)
                            ? (f == null && other.f == null)
                            : Float.compare(f, other.f) == 0);
        }

        @Override
        public int hashCode() {
            return Objects.hash(f);
        }
    }

    record FloatDontAllowNaN(double d, float f) {
        public FloatDontAllowNaN {
            if (Float.isNaN(f)) {
                throw new IllegalArgumentException("f cannot be NaN");
            }
        }
    }

    static final class ComparePrimitiveDoubleByReference {

        private final double d;

        public ComparePrimitiveDoubleByReference(double d) {
            this.d = d;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ComparePrimitiveDoubleByReference)) {
                return false;
            }
            return d == ((ComparePrimitiveDoubleByReference) obj).d;
        }

        @Override
        public int hashCode() {
            return Objects.hash(d);
        }
    }

    static final class CompareObjectDoubleByReference {

        private final Double d;

        public CompareObjectDoubleByReference(Double d) {
            this.d = d;
        }

        @Override
        @SuppressWarnings({ "BoxedPrimitiveEquality", "ReferenceEquality" })
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareObjectDoubleByReference)) {
                return false;
            }
            return d == ((CompareObjectDoubleByReference) obj).d;
        }

        @Override
        public int hashCode() {
            return Objects.hash(d);
        }
    }

    static final class CompareDoubleCorrectly {

        private final double d;

        public CompareDoubleCorrectly(double d) {
            this.d = d;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareDoubleCorrectly)) {
                return false;
            }
            return Double.compare(d, ((CompareDoubleCorrectly) obj).d) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(d);
        }
    }

    static final class CompareObjectDoubleWithEquals {

        private final Double d;

        public CompareObjectDoubleWithEquals(Double d) {
            this.d = d;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CompareObjectDoubleWithEquals other
                    && (d == null ? other.d == null : d.equals(other.d));
        }

        @Override
        public int hashCode() {
            return Objects.hash(d);
        }
    }

    static final class CompareObjectDoubleWithCompare {

        private final Double d;

        public CompareObjectDoubleWithCompare(Double d) {
            this.d = d;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CompareObjectDoubleWithCompare other
                    && ((d == null || other.d == null)
                            ? (d == null && other.d == null)
                            : Double.compare(d, other.d) == 0);
        }

        @Override
        public int hashCode() {
            return Objects.hash(d);
        }
    }

    record DoubleDontAllowNaN(double d, float f) {
        public DoubleDontAllowNaN {
            if (Double.isNaN(d)) {
                throw new IllegalArgumentException("d cannot be NaN");
            }
        }
    }
}
