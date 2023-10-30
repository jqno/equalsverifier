package nl.jqno.equalsverifier.integration.extended_contract;

import java.text.AttributedString;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

/*
 * Let's hope nobody needs prefab values for `java.text.AttributedString`,
 * because we need a class here from je Java APIs that doesn't already
 * have prefab values.
 */
public class ModulesTest {

    @Test
    public void giveProperErrorMessage_whenClassUnderTestIsInaccessible() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(AttributedString.class).verify())
            .assertFailure()
            .assertMessageContains("The class", "Consider opening");
    }

    @Test
    public void giveProperErrorMessage_whenFieldIsInaccessible() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(InaccessibleContainer.class).verify())
            .assertFailure()
            .assertMessageContains("Field x", "Consider opening", "add prefab values");
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
