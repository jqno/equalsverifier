package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.math.BigDecimal;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.BigDecimalFieldCheck;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class BigDecimalTest {

    @Test
    public void fail_whenBigDecimalsComparedUsingEqualsWithComparablyConsistentHashCode() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(BigDecimalEqualsWithComparablyConsistentHashCode.class)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains(
                BigDecimalFieldCheck.ERROR_DOC_TITLE,
                "BigDecimal",
                "equals",
                "compareTo",
                "bd",
                Warning.BIGDECIMAL_EQUALITY.toString()
            );
    }

    @Test
    public void fail_whenBigDecimalsComparedUsingCompareToWithInconsistentHashCode() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(BigDecimalInconsistentHashCode.class).verify())
            .assertFailure()
            .assertMessageContains(
                BigDecimalFieldCheck.ERROR_DOC_TITLE,
                "BigDecimal",
                "hashCode",
                "compareTo",
                "bd",
                Warning.BIGDECIMAL_EQUALITY.toString()
            );
    }

    @Test
    public void succeed_whenBigDecimalsComparedUsingEquals_givenBigDecimalEqualsWarningIsSuppressed() {
        EqualsVerifier
            .forClass(BigDecimalEquals.class)
            .suppress(Warning.BIGDECIMAL_EQUALITY)
            .verify();
    }

    @Test
    public void succeed_whenBigDecimalsComparedUsingCompareTo() {
        EqualsVerifier.forClass(BigDecimalCompareTo.class).verify();
    }

    /**
     * Uses standard equals and hashCode for objects.
     * 0 and 0.0 are not equal.
     */
    private static final class BigDecimalEquals {

        private final BigDecimal bd;

        public BigDecimalEquals(BigDecimal bd) {
            this.bd = bd;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BigDecimalEquals)) {
                return false;
            }
            BigDecimalEquals other = (BigDecimalEquals) obj;
            return Objects.equals(bd, other.bd);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    /**
     * Uses compareTo for BigDecimal equality and ensures hashCode is equal for equal BigDecimal instances.
     * 0 and 0.0 are equal and produce the same hashCode.
     */
    private static final class BigDecimalCompareTo {

        private final BigDecimal bd;

        public BigDecimalCompareTo(BigDecimal bd) {
            this.bd = bd;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BigDecimalCompareTo)) {
                return false;
            }
            BigDecimalCompareTo other = (BigDecimalCompareTo) obj;
            return comparablyEquals(bd, other.bd);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(comparablyHashed(bd));
        }
    }

    /**
     * Uses standard equals but with a consistent hashCode for comparably equal instances.
     * 0 and 0.0 are not equal but produce the same hashCode.
     */
    private static final class BigDecimalEqualsWithComparablyConsistentHashCode {

        private final BigDecimal bd;

        public BigDecimalEqualsWithComparablyConsistentHashCode(BigDecimal bd) {
            this.bd = bd;
        }

        public BigDecimal getBd() {
            return bd;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BigDecimalEqualsWithComparablyConsistentHashCode)) {
                return false;
            }
            BigDecimalEqualsWithComparablyConsistentHashCode other = (BigDecimalEqualsWithComparablyConsistentHashCode) obj;
            return Objects.equals(bd, other.bd);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(comparablyHashed(bd));
        }
    }

    /**
     * Uses compareTo for BigDecimal equality but has hashCode that is inconsistent.
     * 0 and 0.0 are equal but produce different hashCodes.
     */
    private static final class BigDecimalInconsistentHashCode {

        private final BigDecimal bd;

        public BigDecimalInconsistentHashCode(BigDecimal bd) {
            this.bd = bd;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BigDecimalInconsistentHashCode)) {
                return false;
            }
            BigDecimalInconsistentHashCode other = (BigDecimalInconsistentHashCode) obj;
            return comparablyEquals(bd, other.bd);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    /**
     * Checks equality using compareTo rather than equals.
     */
    private static boolean comparablyEquals(BigDecimal left, BigDecimal right) {
        boolean bothNull = left == null && right == null;
        boolean bothNonNullAndEqual = left != null && right != null && left.compareTo(right) == 0;
        return bothNull || bothNonNullAndEqual;
    }

    /**
     * Returns a instance (or null) that produces the same hashCode as any other instance that is equal using compareTo.
     */
    private static BigDecimal comparablyHashed(BigDecimal bd) {
        return bd != null ? bd.stripTrailingZeros() : null;
    }
}
