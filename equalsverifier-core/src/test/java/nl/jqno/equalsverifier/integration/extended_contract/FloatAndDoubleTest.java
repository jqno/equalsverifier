package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class FloatAndDoubleTest {

    private static final String FLOAT = "Float: equals doesn't use Float.compare for field";
    private static final String DOUBLE = "Double: equals doesn't use Double.compare for field";

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
                .assertMessageContains(FLOAT, "f");
    }

    @Test
    void succeed_whenFloatsAreComparedWithFloatCompare() {
        EqualsVerifier.forClass(CompareFloatCorrectly.class).verify();
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
                .assertMessageContains(DOUBLE, "d");
    }

    @Test
    void succeed_whenDoublesAreComparedWithDoubleCompare() {
        EqualsVerifier.forClass(CompareDoubleCorrectly.class).verify();
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
}
