package nl.jqno.equalsverifier.integration.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

public class ConstructorTest {
    @Test
    void succeed_whenSutIsRecord() {
        EqualsVerifier.forClass(SomeRecord.class).verify();
    }

    @Test
    void succeed_whenConstructorMatchesFields() {
        EqualsVerifier.forClass(ConstructorMatchesFields.class).verify();
    }

    @Test
    void succeed_forFinalPoint() {
        // An example of constructor matching fields
        EqualsVerifier.forClass(FinalPoint.class).verify();
    }

    @Test
    void fail_whenConstructorDoesNotMatchFields() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(ConstructorDoesNotMatchFields.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Use #withFactory()");
    }

    @Test
    void succeed_whenOneOfSeveralConstructorsMatchesFields() {
        EqualsVerifier.forClass(OneConstructorMatchesFields.class).verify();
    }

    @Test
    void succeed_whenMatchingConstructorIsPrivate() {
        EqualsVerifier.forClass(ConstructorIsPrivate.class).verify();
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

    static final class OneConstructorMatchesFields {
        private final int i;
        private final String s;

        public OneConstructorMatchesFields(int i, String s) {
            this.i = i;
            this.s = s;
        }

        public OneConstructorMatchesFields(int i) {
            this(i, "yes");
        }

        public OneConstructorMatchesFields() {
            this(42, "yes");
        }

        public OneConstructorMatchesFields(int i, int j, String s) {
            this(i + j, s);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof OneConstructorMatchesFields other && i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }

    static final class ConstructorIsPrivate {
        private final int i;

        @SuppressWarnings("unused")
        private ConstructorIsPrivate(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ConstructorIsPrivate other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
}
