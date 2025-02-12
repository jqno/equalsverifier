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
                .hasMessageContaining("Precondition", "red example is null");
    }

    @Test
    void blueShouldNotBeIsNull() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Precondition", "blue example is null");
    }

    @Test
    void valuesShouldBeOfSameType() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, "string"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Precondition", "examples are of different types");
    }

    @Test
    void valuesShouldNotBeEqual() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, v))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Precondition", "both examples are equal");
    }

    @Test
    void redFieldShouldNotBeNull() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(nullV, v))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Precondition", "red prefab value for field `v` is null");
    }

    @Test
    void blueFieldShouldNotBeNull() {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, nullV))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Precondition", "blue prefab value for field `String v` is null");
    }

    @Test
    void fieldShouldNotBeEqual() {
        Value helloV = new Value("hello", "planet");
        assertThatThrownBy(() -> EqualsVerifier.forExamples(v, helloV))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Precondition", "both prefab values for field `String v` are equal");
    }

    @Test
    void superFieldsShouldBeConsidered() {
        Sub s1 = new Sub("s1");
        Sub s2 = new Sub(null);
        assertThatThrownBy(() -> EqualsVerifier.forExamples(s1, s2))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Precondition", "blue prefab value for field `String s` is null");
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

    static class Super {
        protected final String s;

        public Super(String s) {
            this.s = s;
        }
    }

    static final class Sub extends Super {
        public Sub(String s) {
            super(s);
        }
    }
}
