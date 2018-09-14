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
        assertNull(report.getException());
    }

    @Test
    public void containsAppropriateErrorMessageAndExceptionWhenClassIsIncorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(Point.class).report();

        assertFalse(report.isSuccessful());
        assertThat(report.getMessage(), startsWith("EqualsVerifier found a problem in class Point"));
        assertEquals(AssertionException.class, report.getException().getClass());
        assertNull(report.getException().getMessage());

        report.getException().printStackTrace();
    }
}
