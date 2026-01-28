package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

public class FinalMeansFinalTest {
    @Test
    void fail_whenFinalMeansFinalIsForced_givenClassWhoseConstructorDoesntMatchFields() {
        assertThatThrownBy(
            () -> EqualsVerifier.forClass(ConstructorDoesNotMatchFields.class).set(Mode.finalMeansFinal()).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate ConstructorDoesNotMatchFields");
    }

    @Test
    void succeed_whenFinalMeansFinalIsForced_givenClassWhoseConstructorMatchesFields() {
        EqualsVerifier.forClass(FinalPoint.class).set(Mode.finalMeansFinal()).verify();
    }

    @Test
    void succeed_withSimpleFactory() {
        EqualsVerifier
                .forClass(FinalPoint.class)
                .withFactory(values -> new FinalPoint(values.getInt("x"), values.getInt("y")))
                .verify();
    }

    static final class ConstructorDoesNotMatchFields {
        // order of fields reversed
        private final String s;
        private final int i;

        public ConstructorDoesNotMatchFields(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstructorDoesNotMatchFields other && i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }
}
