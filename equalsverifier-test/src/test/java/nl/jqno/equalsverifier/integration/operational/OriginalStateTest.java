package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class OriginalStateTest {

    private static final String STATIC = "static";
    private static final String STATIC_FINAL = "static final";

    @Test
    void staticValueReturnsToOriginalState_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(CorrectEquals.class).verify();
        assertThat(CorrectEquals.STATIC_FINAL_VALUE).isEqualTo(STATIC_FINAL);
        assertThat(CorrectEquals.staticValue).isEqualTo(STATIC);
    }

    @Test
    void staticValueReturnsToOriginalStateRecursively_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(CorrectEqualsContainer.class).verify();
        assertThat(CorrectEquals.staticValue).isEqualTo(STATIC);
    }

    @Test
    void staticValueReturnsToOriginalStateDeeplyRecursively_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(CorrectEqualsContainerContainer.class).verify();
        assertThat(CorrectEquals.staticValue).isEqualTo(STATIC);
    }

    @Test
    void staticValueInSuperReturnsToOriginalState_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(SubContainer.class).verify();
        assertThat(SuperContainer.staticValue).isEqualTo(STATIC);
        assertThat(SuperContainer.STATIC_FINAL_VALUE).isEqualTo(STATIC_FINAL);
    }

    @Test
    void staticBigDecimalValueReturnsToOriginalState_whenEqualsVerifierFailsWithNpe() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(CorrectEqualsWithStaticBigDecimal.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Non-nullity");
        assertThat(CorrectEqualsWithStaticBigDecimal.staticValue).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void staticFloatValueReturnsToOriginalState_whenEqualsVerifierFailsWithNpe() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(CorrectEqualsWithStaticFloat.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Non-nullity");
        assertThat(CorrectEqualsWithStaticFloat.staticValue).isEqualTo(2.0f);
    }

    @Test
    void allValuesReturnToOriginalState_whenEqualsVerifierIsFinishedWithException() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(FailingEqualsContainerContainer.class).verify())
                .isInstanceOf(AssertionError.class)
                // Make sure EV fails on a check that actually mutates fields.
                .hasMessageContaining("Mutability");
        assertThat(CorrectEquals.STATIC_FINAL_VALUE).isEqualTo(STATIC_FINAL);
        assertThat(CorrectEquals.staticValue).isEqualTo(STATIC);
    }

    static final class CorrectEquals {

        private static final String STATIC_FINAL_VALUE = STATIC_FINAL;
        private static String staticValue = STATIC;
        private final String instanceValue;

        public CorrectEquals(String instanceValue) {
            this.instanceValue = instanceValue;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CorrectEquals other && Objects.equals(instanceValue, other.instanceValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(instanceValue);
        }
    }

    static final class CorrectEqualsContainer {

        private final CorrectEquals foo;

        public CorrectEqualsContainer(CorrectEquals foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CorrectEqualsContainer other && Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }

    static final class CorrectEqualsContainerContainer {

        private final CorrectEqualsContainer foo;

        public CorrectEqualsContainerContainer(CorrectEqualsContainer foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CorrectEqualsContainerContainer other && Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }

    abstract static class SuperContainer {

        private static final String STATIC_FINAL_VALUE = STATIC_FINAL;
        private static String staticValue = STATIC;

        private final CorrectEquals foo;

        public SuperContainer(CorrectEquals foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SuperContainer)) {
                return false;
            }
            SuperContainer other = (SuperContainer) obj;
            return Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }

    static final class SubContainer extends SuperContainer {

        public SubContainer(CorrectEquals foo) {
            super(foo);
        }
    }

    static final class CorrectEqualsWithStaticBigDecimal {

        static BigDecimal staticValue = BigDecimal.TEN;
        private final String instanceValue;

        public CorrectEqualsWithStaticBigDecimal(String instanceValue) {
            this.instanceValue = instanceValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectEqualsWithStaticBigDecimal other)) {
                return false;
            }
            return staticValue.compareTo(BigDecimal.ZERO) >= 0 && Objects.equals(instanceValue, other.instanceValue);
        }

        @Override
        public int hashCode() {
            return staticValue.intValue() + Objects.hash(instanceValue);
        }
    }

    static final class CorrectEqualsWithStaticFloat {

        static Float staticValue = 2.0f;
        private final float instanceValue;

        public CorrectEqualsWithStaticFloat(float instanceValue) {
            this.instanceValue = instanceValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectEqualsWithStaticFloat other)) {
                return false;
            }
            return Float.compare(instanceValue * staticValue, other.instanceValue * staticValue) == 0;
        }

        @Override
        public int hashCode() {
            return Float.hashCode(instanceValue * staticValue);
        }
    }

    static final class FailingEqualsContainerContainer {

        private CorrectEqualsContainer foo;

        public FailingEqualsContainerContainer(CorrectEqualsContainer foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FailingEqualsContainerContainer other && Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }
}
