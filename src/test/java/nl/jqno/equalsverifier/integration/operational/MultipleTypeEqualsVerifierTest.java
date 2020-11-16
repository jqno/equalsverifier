package nl.jqno.equalsverifier.integration.operational;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.packages.correct.B;
import nl.jqno.equalsverifier.testhelpers.packages.correct.C;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectM;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectN;
import org.junit.jupiter.api.Test;

public class MultipleTypeEqualsVerifierTest extends ExpectedExceptionTestBase {
    private static final String CORRECT_PACKAGE =
            "nl.jqno.equalsverifier.testhelpers.packages.correct";
    private static final String INCORRECT_PACKAGE =
            "nl.jqno.equalsverifier.testhelpers.packages.twoincorrect";
    private static final String INCORRECT_M = INCORRECT_PACKAGE + ".IncorrectM";
    private static final String INCORRECT_N = INCORRECT_PACKAGE + ".IncorrectN";

    @Test
    public void succeed_whenVerifyingSeveralCorrectClasses_givenIterableOverload() {
        List<Class<?>> classes = Arrays.asList(A.class, B.class, C.class);
        EqualsVerifier.forClasses(classes).verify();
    }

    @Test
    public void succeed_whenVerifyingSeveralCorrectClasses_givenVarargsOverload() {
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
    public void fail_whenCallingForPackage_givenTwoClassesInPackageAreIncorrect() {
        expectFailure(
                "EqualsVerifier found a problem in 2 classes.",
                "IncorrectM",
                "IncorrectN",
                "Subclass: equals is not final.",
                "Reflexivity: object does not equal itself:");

        EqualsVerifier.forPackage(INCORRECT_PACKAGE).verify();
    }

    @Test
    public void fail_whenCallingForPackage_whenPackageHasNoClasses() {
        expectException(
                IllegalStateException.class,
                "nl.jqno.equalsverifier.doesnotexist",
                "doesn't contain any (non-Test) types");

        EqualsVerifier.forPackage("nl.jqno.equalsverifier.doesnotexist");
    }

    @Test
    public void
            succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreExcepted() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE)
                .except(IncorrectM.class, IncorrectN.class)
                .verify();
    }

    @Test
    public void fail_whenExceptingAClassThatDoesntExistInThePackage() {
        expectException(IllegalStateException.class, "Unknown class(es) found", "IncorrectM");

        EqualsVerifier.forPackage(CORRECT_PACKAGE).except(IncorrectM.class);
    }

    @Test
    public void
            succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreExceptedByPredicate() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE)
                .except(c -> c.getSimpleName().contains("Incorrect"))
                .verify();
    }

    @Test
    public void
            fail_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreNotExceptedByPredicate() {
        expectFailure("EqualsVerifier found a problem in 2 classes");

        EqualsVerifier.forPackage(INCORRECT_PACKAGE).except(c -> false).verify();
    }

    @Test
    public void
            succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenAllClassesAreExceptedByPredicate() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE).except(c -> true).verify();
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
