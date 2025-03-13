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
        run(null, v, NullPointerException.class, "Precondition", "red example is null");
    }

    @Test
    void blueShouldNotBeIsNull() {
        run(v, null, NullPointerException.class, "Precondition", "blue example is null");
    }

    @Test
    void valuesShouldBeOfSameType() {
        run(v, "string", IllegalStateException.class, "Precondition", "examples are of different types");
    }

    @Test
    void valuesShouldNotBeEqual() {
        run(v, v, IllegalStateException.class, "Precondition", "both examples are equal");
    }

    @Test
    void redFieldShouldNotBeNull() {
        run(nullV, v, NullPointerException.class, "Precondition", "red prefab value for field `v` is null");
    }

    @Test
    void blueFieldShouldNotBeNull() {
        run(v, nullV, NullPointerException.class, "Precondition", "blue prefab value for field `String v` is null");
    }

    @Test
    void fieldShouldNotBeEqual() {
        Value helloV = new Value("hello", "planet");
        run(
            v,
            helloV,
            IllegalStateException.class,
            "Precondition",
            "both prefab values for field `String v` are equal");
    }

    @Test
    void superFieldsShouldBeConsidered() {
        Sub s1 = new Sub("s1");
        Sub s2 = new Sub(null);
        run(s1, s2, NullPointerException.class, "Precondition", "blue prefab value for field `String s` is null");
    }

    private void run(Object red, Object blue, Class<? extends Throwable> exception, String... fragments) {
        assertThatThrownBy(() -> EqualsVerifier.forExamples(red, blue))
                .isInstanceOf(exception)
                .hasMessageContainingAll(fragments);
        assertThatThrownBy(() -> EqualsVerifier.simple().forExamples(red, blue))
                .isInstanceOf(exception)
                .hasMessageContainingAll(fragments);
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
