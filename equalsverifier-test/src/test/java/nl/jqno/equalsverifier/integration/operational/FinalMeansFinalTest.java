package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Locale;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier_testhelpers.types.*;
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
    void succeed_whenFinalMeansFinalIsForced_givenClassWhoseConstructorMatchesFields_givenConstructorIsPrivate() {
        EqualsVerifier.forClass(PrivateConstructorMatchesFields.class).set(Mode.finalMeansFinal()).verify();
    }

    @Test
    void succeed_whenFinalMeansFinalIsForced_givenClassWhoseConstructorMatchesFields_givenItIsAbstractSoDynamicSubclassIsGenerated() {
        EqualsVerifier.forClass(AbstractConstructorMatchesFields.class).set(Mode.finalMeansFinal()).verify();
    }

    @Test
    void succeed_withSimpleFactory() {
        EqualsVerifier
                .forClass(FinalPoint.class)
                .set(Mode.finalMeansFinal())
                .withFactory(values -> new FinalPoint(values.getInt("x"), values.getInt("y")))
                .verify();
    }

    @Test
    void succeed_withExpandedFactory() {
        EqualsVerifier
                .forClass(ConstructorDoesNotMatchFields.class)
                .set(Mode.finalMeansFinal())
                .withFactory(
                    values -> new ConstructorDoesNotMatchFields("" + values.getInt("i")),
                    values -> new TrivialSubConstructorDoesNotMatchFields("" + values.getInt("i")))
                .verify();
    }

    @Test
    void succeed_withRedefinedSubclass() {
        EqualsVerifier
                .forClass(CanEqualPoint.class)
                .withRedefinedSubclass(
                    values -> new CanEqualColorPoint(values.getInt("x"), values.getInt("y"), values.get("color")))
                .verify();
    }

    @Test
    void succeed_withRedefinedSuperclass() {
        EqualsVerifier
                .forClass(CanEqualColorPoint.class)
                .withRedefinedSuperclass(values -> new CanEqualPoint(values.getInt("x"), values.getInt("y")))
                .verify();
    }

    @Test
    void fail_whenSutHasPrecondition() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(Preconditioned.class)
                    .withFactory(values -> new Preconditioned(values.getString("s")))
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("s should start with precondition:");
    }

    @Test
    void succeed_whenSutHasPrecondition_givenPrefabValues() {
        EqualsVerifier
                .forClass(Preconditioned.class)
                .withFactory(values -> new Preconditioned(values.getString("s")))
                .withPrefabValues(String.class, "precondition:red", "precondition:blue")
                .verify();
    }

    @Test
    void succeed_whenSutHasPrecondition_givenPrefabValuesForField() {
        EqualsVerifier
                .forClass(Preconditioned.class)
                .withFactory(values -> new Preconditioned(values.getString("s")))
                .withPrefabValuesForField("s", "precondition:red", "precondition:blue")
                .verify();
    }

    static class ConstructorDoesNotMatchFields {
        private final int i;

        public ConstructorDoesNotMatchFields(String i) {
            this.i = Integer.valueOf(i);
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof ConstructorDoesNotMatchFields other && i == other.i;
        }

        @Override
        public final int hashCode() {
            return i;
        }
    }

    static class TrivialSubConstructorDoesNotMatchFields extends ConstructorDoesNotMatchFields {
        public TrivialSubConstructorDoesNotMatchFields(String i) {
            super(i);
        }
    }

    static final class PrivateConstructorMatchesFields {
        private final int i;

        @SuppressWarnings("unused")
        private PrivateConstructorMatchesFields(int i) {
            this.i = i;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof PrivateConstructorMatchesFields other && i == other.i;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i);
        }
    }

    static abstract class AbstractConstructorMatchesFields {
        private final int i;

        public AbstractConstructorMatchesFields(int i) {
            this.i = i;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof AbstractConstructorMatchesFields other && i == other.i;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class Preconditioned {
        private final String s;

        public Preconditioned(String s) {
            if (s != null && !s.toLowerCase(Locale.getDefault()).startsWith("precondition:")) {
                throw new IllegalArgumentException("s should start with precondition:");
            }
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Preconditioned other && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
    }
}
