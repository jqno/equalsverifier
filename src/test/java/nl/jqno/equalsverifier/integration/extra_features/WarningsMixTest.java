package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.Test;

public class WarningsMixTest extends IntegrationTestBase {
    @Test
    public void fail_whenFieldsAreNonfinalAndClassIsNonfinal_givenOnlyStrictInheritanceWarningIsSuppressed() {
        expectFailure("Mutability:");
        EqualsVerifier.forClass(MutablePoint.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void fail_whenFieldsAreNonFinalAndClassIsNonFinal_givenOnlyNonfinalFieldsWarningIsSuppressed() {
        expectFailure("Subclass:");
        EqualsVerifier.forClass(MutablePoint.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenBothStrictInheritanceAndNonfinalFieldsWarningsAreSuppressed() {
        EqualsVerifier.forClass(MutablePoint.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceWarningIsSuppressed() {
        expectFailureWithCause(NullPointerException.class, "Non-nullity:");
        EqualsVerifier.forClass(NeverNullColorContainer.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void fail_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenOnlyNullFieldsWarningIsSuppressed() {
        expectFailure("Subclass:");
        EqualsVerifier.forClass(NeverNullColorContainer.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenClassIsNonfinalAndEqualsDoesNotCheckNull_givenBothStrictInheritanceAndNullFieldsWarningsAreSuppressed() {
        EqualsVerifier.forClass(NeverNullColorContainer.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenWarningsAreSuppressedSeparately_givenBothWarningsNeedToBeSuppressed() {
        EqualsVerifier.forClass(NeverNullColorContainer.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceAndNullFieldsWarningsAreSuppressed() {
        expectFailure("Mutability:");
        EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenClassAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyStrictInheritanceAndNonfinalFieldsWarningsAreSuppressed() {
        expectFailureWithCause(NullPointerException.class, "Non-nullity:");
        EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenOnlyNonfinalFieldsAndNullFieldsWarningsAreSuppressed() {
        expectFailure("Subclass:");
        EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
                .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenClassIsNonfinalAndFieldsAreNonfinalAndEqualsDoesNotCheckNull_givenAllNecessaryWarningsAreSuppressed() {
        EqualsVerifier.forClass(NeverNullAndMutableColorContainer.class)
                .suppress(Warning.STRICT_INHERITANCE, Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS)
                .verify();
    }

    static class MutablePoint {
        private int x;
        private int y;

        public MutablePoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MutablePoint)) {
                return false;
            }
            MutablePoint other = (MutablePoint)obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return x + (31 * y);
        }
    }

    static class NeverNullColorContainer {
        private final Color color;

        public NeverNullColorContainer(Color color) { this.color = color; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NeverNullColorContainer)) {
                return false;
            }
            return color == ((NeverNullColorContainer)obj).color;
        }

        @Override
        public int hashCode() {
            return color.hashCode();
        }
    }

    static class NeverNullAndMutableColorContainer {
        private Color color;

        public NeverNullAndMutableColorContainer(Color color) { this.color = color; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NeverNullAndMutableColorContainer)) {
                return false;
            }
            return color == ((NeverNullAndMutableColorContainer)obj).color;
        }

        @Override
        public int hashCode() {
            return color.hashCode();
        }
    }
}
