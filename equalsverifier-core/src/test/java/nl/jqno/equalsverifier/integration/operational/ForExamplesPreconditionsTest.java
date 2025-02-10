package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ForExamplesPreconditionsTest {
    private Value v = new Value("hello", "world");
    private Value nullV = new Value(null, "world");

    @Test
    void sanity() {
        EqualsVerifier.forClass(Value.class).verify();
    }

    @Test
    void redShouldNotBeNull() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(null, v))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("red value is null");
    }

    @Test
    void blueShouldNotBeIsNull() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("blue value is null");
    }

    @Test
    void valuesShouldBeOfSameType() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, "string"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("values are of different types");
    }

    @Test
    void valuesShouldNotBeEqual() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, v))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("both values are equal");
    }

    @Test
    void redFieldShouldNotBeNull() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(nullV, v)).isInstanceOf(NullPointerException.class);
        // .hasMessageContaining("red prefab value of type String is null");
    }

    @Test
    void blueFieldShouldNotBeNull() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, nullV))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("blue prefab value of type String is null");
    }

    @Test
    void fieldShouldNotBeEqual() {
        Value helloV = new Value("hello", "planet");
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, helloV))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("both prefab values of type String are equal");
    }

    static final class Value {
        private final String v;
        private final String w;

        public Value(String v, String w) {
            this.v = v;
            this.w = w;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Value)) {
                return false;
            }
            Value other = (Value) obj;
            return Objects.equals(v, other.v) && Objects.equals(w, other.w);
        }

        @Override
        public int hashCode() {
            return Objects.hash(v, w);
        }
    }
}
