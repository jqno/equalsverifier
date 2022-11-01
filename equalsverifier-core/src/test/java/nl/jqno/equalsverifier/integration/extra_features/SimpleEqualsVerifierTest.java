package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.jupiter.api.Test;

public class SimpleEqualsVerifierTest {

    @Test
    public void succeed_whenTestingClass_givenASimpleEqualsVerifier() {
        EqualsVerifier.simple().forClass(SimplePoint.class).verify();
    }

    @Test
    public void succeed_whenTestingClassesRecursively_givenASimpleEqualsVerifier() {
        EqualsVerifier
            .simple()
            .forPackage("nl.jqno.equalsverifier.integration.extra_features.simple_package", true)
            .verify();
    }

    @Test
    public void succeed_whenTestingClassesThatMustExtendSomething_givenASimpleEqualsVerifier() {
        EqualsVerifier
            .simple()
            .forPackage(
                "nl.jqno.equalsverifier.integration.extra_features.simple_package",
                Object.class
            )
            .verify();
    }

    @Test
    public void mentionSimple_whenTestingClass_givenNothingSpecial() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(SimplePoint.class).verify())
            .assertFailure()
            .assertMessageContains("or use EqualsVerifier.simple()");
    }

    @Test
    public void mentionSimple_whenTestingClassesRecursively_givenNothingSpecial() {
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
    public void mentionSimple_whenTestingClassesThatMustExtendSomething_givenNothingSpecial() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forPackage(
                        "nl.jqno.equalsverifier.integration.extra_features.simple_package",
                        Object.class
                    )
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("or use EqualsVerifier.simple()");
    }

    @Test
    public void mentionSimple_whenTestingClass_givenSuppressWarningStrictInheritance() {
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

    @Test
    public void fail_whenTestingClassesRecursively_whenPackageHasNoClasses() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .simple()
                    .forPackage("nl.jqno.equalsverifier.doesnotexist", true)
                    .verify()
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "nl.jqno.equalsverifier.doesnotexist",
                "doesn't contain any (non-Test) types"
            );
    }

    @Test
    public void fail_whenTestingClassesThatMustExtendSomething_whenPackageHasNoClasses() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .simple()
                    .forPackage("nl.jqno.equalsverifier.doesnotexist", Object.class)
                    .verify()
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "nl.jqno.equalsverifier.doesnotexist",
                "doesn't contain any (non-Test) types"
            );
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
