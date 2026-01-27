package nl.jqno.equalsverifier.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConstructorInstantiatorTest {
    @Test
    void succeed_whenSutIsRecord() {
        EqualsVerifier.forClass(SomeRecord.class).verify();
    }

    @Test
    @Disabled
    void succeed_whenConstructorMatchesFields() {
        EqualsVerifier.forClass(ConstructorMatchesFields.class).verify();
    }

    @Test
    void fail_whenConstructorDoesNotMatchFields() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(ConstructorDoesNotMatchFields.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Not allowed to reflectively set final field");
    }

    record SomeRecord(int i) {}

    static final class ConstructorMatchesFields {
        private final int i;
        private final String s;

        public ConstructorMatchesFields(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstructorMatchesFields other && i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
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
