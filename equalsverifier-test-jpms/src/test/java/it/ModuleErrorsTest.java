package it;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.AttributedString;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

/*
 * Let's hope nobody needs prefab values for `java.text.AttributedString`, because we need a class here from je Java
 * APIs that doesn't already have prefab values.
 */
public class ModuleErrorsTest {
    @Test
    void giveProperErrorMessage_whenClassUnderTestIsInaccessible() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(AttributedString.class)
                    .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("The class")
                .hasMessageContaining("Consider opening");
    }

    @Test
    void giveProperErrorMessage_whenFieldIsInaccessible() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(InaccessibleContainer.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Field x")
                .hasMessageContaining("Consider opening")
                .hasMessageContaining("add prefab values");
    }

    static final class InaccessibleContainer {

        private final AttributedString x;

        public InaccessibleContainer(AttributedString x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof InaccessibleContainer)) {
                return false;
            }
            InaccessibleContainer other = (InaccessibleContainer) obj;
            return Objects.equals(x, other.x);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x);
        }
    }
}
