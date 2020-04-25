package nl.jqno.equalsverifier.integration.operational;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.packages.correct.*;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.*;
import org.junit.Test;

public class MultipleTypeEqualsVerifierTest extends ExpectedExceptionTestBase {
    private static final String CORRECT_PACKAGE =
            "nl.jqno.equalsverifier.testhelpers.packages.correct";
    private static final String INCORRECT_PACKAGE =
            "nl.jqno.equalsverifier.testhelpers.packages.twoincorrect";
    private static final String INCORRECT_M = INCORRECT_PACKAGE + ".IncorrectM";
    private static final String INCORRECT_N = INCORRECT_PACKAGE + ".IncorrectN";

    @Test
    public void succeed_whenVerifyingSeveralCorrectClasses() {
        EqualsVerifier.forClasses(A.class, B.class, C.class).verify();
    }

    @Test
    public void succeed_whenVerifyingACorrectPackage() {
        EqualsVerifier.forPackage(CORRECT_PACKAGE).verify();
    }

    @Test
    public void fail_whenVerifyingOneIncorrectClass() {
        expectFailure(
                "EqualsVerifier found a problem in 1 class.",
                "* " + INCORRECT_M,
                "Subclass: equals is not final.");

        EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class).verify();
    }

    @Test
    public void fail_whenVerifyingTwoIncorrectClasses() {
        expectFailure(
                "EqualsVerifier found a problem in 2 classes.",
                "* " + INCORRECT_M,
                "* " + INCORRECT_N,
                "Subclass: equals is not final.",
                "Reflexivity: object does not equal itself:");

        EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class, IncorrectN.class).verify();
    }

    @Test
    public void fail_whenVerifyingAPackageWithTwoIncorrectClasses() {
        expectFailure(
                "EqualsVerifier found a problem in 2 classes.",
                "* " + INCORRECT_M,
                "* " + INCORRECT_N,
                "Subclass: equals is not final.",
                "Reflexivity: object does not equal itself:");

        EqualsVerifier.forPackage(INCORRECT_PACKAGE).verify();
    }

    @Test
    public void succeed_whenReportingOnSeveralCorrectClasses() {
        List<EqualsVerifierReport> reports =
                EqualsVerifier.forClasses(A.class, B.class, C.class).report();

        assertEquals(3, reports.size());
        assertSuccessful(reports.get(0), A.class);
        assertSuccessful(reports.get(1), B.class);
        assertSuccessful(reports.get(2), C.class);
    }

    @Test
    public void fail_whenReportingOnOneIncorrectClass() {
        List<EqualsVerifierReport> reports =
                EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class).report();

        assertEquals(3, reports.size());
        assertSuccessful(reports.get(0), A.class);
        assertSuccessful(reports.get(2), C.class);
        assertUnsuccessful(reports.get(1), IncorrectM.class, "Subclass: equals is not final.");
    }

    @Test
    public void fail_whenReportingOnTwoIncorrectClasses() {
        List<EqualsVerifierReport> reports =
                EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class, IncorrectN.class)
                        .report();

        assertEquals(4, reports.size());
        assertSuccessful(reports.get(0), A.class);
        assertSuccessful(reports.get(2), C.class);
        assertUnsuccessful(reports.get(1), IncorrectM.class, "Subclass: equals is not final.");
        assertUnsuccessful(
                reports.get(3), IncorrectN.class, "Reflexivity: object does not equal itself:");
    }

    private void assertSuccessful(EqualsVerifierReport report, Class<?> type) {
        assertTrue(report.isSuccessful());
        assertEquals(type, report.getType());
    }

    private void assertUnsuccessful(EqualsVerifierReport report, Class<?> type, String message) {
        assertFalse(report.isSuccessful());
        assertEquals(type, report.getType());
        assertThat(report.getMessage(), containsString(message));
    }
}
