package nl.jqno.equalsverifier.integration.operational;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;

public class ReportTest {

    @Test
    public void isEmptyWhenClassIsCorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(FinalPoint.class).report();

        assertEquals(FinalPoint.class, report.getType());
        assertTrue(report.isSuccessful());
        assertEquals("", report.getMessage());
        assertNull(report.getCause());
    }

    @Test
    public void containsAppropriateErrorMessageAndExceptionWhenClassIsIncorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(Point.class).report();

        assertEquals(Point.class, report.getType());
        assertFalse(report.isSuccessful());
        assertThat(
            report.getMessage(),
            startsWith(
                "EqualsVerifier found a problem in class nl.jqno.equalsverifier.testhelpers.types.Point"
            )
        );
        assertEquals(AssertionException.class, report.getCause().getClass());
        assertNull(report.getCause().getMessage());
    }

    @Test
    public void reportReturnsTheSameInformationAsVerify() {
        EqualsVerifierReport report = EqualsVerifier.forClass(Point.class).report();
        try {
            EqualsVerifier.forClass(Point.class).verify();
            fail("Should have failed");
        } catch (AssertionError e) {
            assertEquals(e.getMessage(), report.getMessage());
            assertEquals(e.getCause().getClass(), report.getCause().getClass());

            StackTraceElement[] verified = e.getCause().getStackTrace();
            StackTraceElement[] reported = report.getCause().getStackTrace();
            // Implementation detail:
            // `report` has 1 extra stack frame because it delegates to an overload.
            assertEquals(verified.length + 1, reported.length);

            for (int i = 0; i < verified.length; i++) {
                if (!verified[i].getMethodName().equals("verify")) {
                    // When the `verify` method is reached, the stacktraces start to diverge.
                    break;
                }

                assertEquals(verified[i], reported[i]);
            }
        }
    }
}
