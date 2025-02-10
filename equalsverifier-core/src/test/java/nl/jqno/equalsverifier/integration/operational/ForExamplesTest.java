package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.RecursiveType;
import org.junit.jupiter.api.Test;

public class ForExamplesTest {
    @Test
    void recursion() {
        var red = new RecursiveType(new RecursiveType(null));
        var blue = new RecursiveType(new RecursiveType(new RecursiveType(null)));
        run(red, blue);
    }

    @Test
    void valuesPerField() {
        var red = new MultiString("red", "blue");
        var blue = new MultiString("green", "yellow");
        run(red, blue);
    }

    private void run(Object red, Object blue) {
        assertThat(true)
                .satisfies(
                    b -> EqualsVerifier.forExamples(red, blue).verify(),
                    b -> EqualsVerifier.simple().forExamples(red, blue).verify());
    }

    static final class MultiString {
        private final String red;
        private final String blue;

        public MultiString(String red, String blue) {
            this.red = red;
            this.blue = blue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MultiString)) {
                return false;
            }
            MultiString other = (MultiString) obj;
            return Objects.equals(red, other.red) && Objects.equals(blue, other.blue);
        }

        @Override
        public int hashCode() {
            if (Objects.equals(red, blue)) {
                throw new IllegalStateException("red and blue can't be equal! (" + red + ")");
            }
            return Objects.hash(red, blue);
        }
    }
}
