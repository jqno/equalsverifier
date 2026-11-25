package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class ClassProbeTest {
    @Test
    void isBuiltinValueClass() {
        assertThat(ClassProbe.of(Integer.class).isValue()).isTrue();
    }

    @Test
    void isNotBuiltinValueClass() {
        assertThat(ClassProbe.of(String.class).isValue()).isFalse();
    }

    @Test
    void isCusomValueClass() {
        assertThat(ClassProbe.of(Value.class).isValue()).isTrue();
    }

    @Test
    void isNotCusomValueClass() {
        assertThat(ClassProbe.of(NotValue.class).isValue()).isFalse();
    }

    @SuppressWarnings({ "ValueClassEquals", "unused" })
    static value class Value {
        private final int i;

        public Value(int i) {
            this.i = i;
        }
    }

    @SuppressWarnings("unused")
    static class NotValue {
        private final int i;

        public NotValue(int i) {
            this.i = i;
        }
    }
}
