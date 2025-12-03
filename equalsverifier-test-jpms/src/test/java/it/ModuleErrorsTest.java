package it;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.AttributedString;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.ConfiguredEqualsVerifier;
import org.junit.jupiter.api.Test;

/*
 * Let's hope nobody needs prefab values for `java.text.AttributedString`, because we need a class here from the Java
 * APIs that doesn't already have prefab values.
 */
public class ModuleErrorsTest {

    private final ConfiguredEqualsVerifier ev = EqualsVerifier.configure().set(Mode.skipMockito());

    @Test
    void giveProperErrorMessage_whenClassUnderTestIsInaccessible() {
        assertThatThrownBy(
            () -> ev.forClass(AttributedString.class).suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("running on modulepath")
                .hasMessageContaining("The class")
                .hasMessageContaining("Consider opening");
    }

    @Test
    void giveProperErrorMessage_whenFieldIsInaccessible() {
        assertThatThrownBy(() -> ev.forClass(InaccessibleContainer.class).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("running on modulepath")
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
