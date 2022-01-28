package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.jupiter.api.Test;

public class SimpleEqualsVerifierTest {

    @Test
    public void succeed_whenTestingGeneratedClass_givenASimpleEqualsVerifier() {
        EqualsVerifier.simple().forClass(SimplePoint.class).verify();
    }

    @Test
    public void succeed_whenTestingGeneratedClassesRecursively_givenASimpleEqualsVerifier() {
        EqualsVerifier
            .simple()
            .forPackage("nl.jqno.equalsverifier.integration.extra_features.simple_package", true)
            .verify();
    }

    @Test
    public void mentionSimple_whenTestingGeneratedClass_givenNothingSpecial() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(SimplePoint.class).verify())
            .assertFailure()
            .assertMessageContains("or use EqualsVerifier.simple()");
    }

    @Test
    public void mentionSimple_whenTestingGeneratedClassesRecursively_givenNothingSpecial() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forPackage(
                        "nl.jqno.equalsverifier.integration.extra_features.simple_package",
                        true
                    )
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("or use EqualsVerifier.simple()");
    }

    @Test
    public void mentionSimple_whenTestingGeneratedClass_givenSuppressWarningStrictInheritance() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SimplePoint.class)
                    .suppress(Warning.STRICT_INHERITANCE)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("or use EqualsVerifier.simple()");
    }

    public static class SimplePoint {

        private int x;
        private int y;
        private Color color;

        public SimplePoint(int x) {
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
            SimplePoint that = (SimplePoint) o;
            return x == that.x && y == that.y && color == that.color;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, color);
        }
    }
}
