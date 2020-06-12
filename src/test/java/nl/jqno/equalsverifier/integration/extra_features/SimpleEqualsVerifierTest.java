package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.Test;

public class SimpleEqualsVerifierTest extends ExpectedExceptionTestBase {

    @Test
    public void succeed_whenTestingGeneratedClass_givenASimpleEqualsVerifier() {
        EqualsVerifier.simple().forClass(IntelliJPoint.class).verify();
    }

    @Test
    public void mentionSimple_whenTestingGeneratedClass_givenNothingSpecial() {
        expectFailure("or use EqualsVerifier.simple()");
        EqualsVerifier.forClass(IntelliJPoint.class).verify();
    }

    @Test
    public void mentionSimple_whenTestingGeneratedClass_givenSuppressWarningStrictInheritance() {
        expectFailure("or use EqualsVerifier.simple()");
        EqualsVerifier.forClass(IntelliJPoint.class).suppress(Warning.STRICT_INHERITANCE).verify();
    }

    public static class IntelliJPoint {
        private int x;
        private int y;
        private Color color;

        public IntelliJPoint(int x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IntelliJPoint that = (IntelliJPoint) o;
            return x == that.x && y == that.y && color == that.color;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, color);
        }
    }
}
