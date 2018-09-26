package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

public class ReportTest {
    @Test
    public void isEmptyWhenClassIsCorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(FinalPoint.class).report();

        assertTrue(report.isSuccessful());
        assertEquals("", report.getMessage());
        assertNull(report.getCause());
    }

    @Test
    public void containsAppropriateErrorMessageAndExceptionWhenClassIsIncorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(Point.class).report();

        assertFalse(report.isSuccessful());
        assertThat(report.getMessage(), startsWith("EqualsVerifier found a problem in class Point"));
        assertEquals(AssertionException.class, report.getCause().getClass());
        assertNull(report.getCause().getMessage());
    }

    @Test
    public void reportReturnsTheSameInformationAsVerify() {
        EqualsVerifierReport report = EqualsVerifier.forClass(Point.class).report();
        try {
            EqualsVerifier.forClass(Point.class).verify();
            fail("Should have failed");
        }
        catch (AssertionError e) {
            assertEquals(e.getMessage(), report.getMessage());
            assertEquals(e.getCause().getClass(), report.getCause().getClass());

            StackTraceElement[] verified = e.getCause().getStackTrace();
            StackTraceElement[] reported = report.getCause().getStackTrace();
            assertEquals(verified.length, reported.length);

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
