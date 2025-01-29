package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.packages.correct.B;
import nl.jqno.equalsverifier.testhelpers.packages.correct.C;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperA;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperI;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectM;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectN;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.subpackage.IncorrectO;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.subpackage.IncorrectP;
import org.junit.jupiter.api.Test;

class MultipleTypeEqualsVerifierTest {

    private static final String CORRECT_PACKAGE = "nl.jqno.equalsverifier.testhelpers.packages.correct";
    private static final String INCORRECT_PACKAGE = "nl.jqno.equalsverifier.testhelpers.packages.twoincorrect";
    private static final String SUBCLASSES_PACKAGE = "nl.jqno.equalsverifier.testhelpers.packages.subclasses";
    private static final String SOME_RECURSIVE_PACKAGE = "nl.jqno.equalsverifier.testhelpers.packages.somerecursive";
    private static final String INCORRECT_M = INCORRECT_PACKAGE + ".IncorrectM";
    private static final String INCORRECT_N = INCORRECT_PACKAGE + ".IncorrectN";
    private static final String INCORRECT_O = INCORRECT_PACKAGE + ".subpackage.IncorrectO";
    private static final String INCORRECT_P = INCORRECT_PACKAGE + ".subpackage.IncorrectP";

    @Test
    void succeed_whenVerifyingSeveralCorrectClasses_givenIterableOverload() {
        List<Class<?>> classes = Arrays.asList(A.class, B.class, C.class);
        EqualsVerifier.forClasses(classes).verify();
    }

    @Test
    void succeed_whenVerifyingSeveralCorrectClasses_givenVarargsOverload() {
        EqualsVerifier.forClasses(A.class, B.class, C.class).verify();
    }

    @Test
    void succeed_whenVerifyingACorrectPackage() {
        EqualsVerifier.forPackage(CORRECT_PACKAGE).verify();
    }

    @Test
    void succeed_whenVerifyingACorrectPackageRecursively() {
        EqualsVerifier.forPackage(CORRECT_PACKAGE, true).verify();
    }

    @Test
    void succeed_whenVerifyingAPackageWithASuperclass() {
        EqualsVerifier.forPackage(SUBCLASSES_PACKAGE, SuperA.class).verify();
    }

    @Test
    void succeed_whenVerifyingAPackageWithASuperInterface_givenOneOfTheImplementationsIsAlsoAnInterface() {
        EqualsVerifier.forPackage(SUBCLASSES_PACKAGE, SuperI.class).verify();
    }

    @Test
    void fail_whenVerifyingAThirdPartyPackage() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage("org.junit").verify())
                .assertThrows(ReflectionException.class)
                .assertMessageContains("Could not resolve");
    }

    @Test
    void doesNotReportNonrecursive_whenPackageContainsRecursiveAndNonrecursiveClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(SOME_RECURSIVE_PACKAGE).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure", "RecursiveA", "RecursiveB")
                .assertMessageDoesNotContain("Nonrecursive");
    }

    @Test
    void fail_whenVerifyingOneIncorrectClass() {
        ExpectedException
                .when(() -> EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "EqualsVerifier found a problem in 1 class.",
                    "* " + INCORRECT_M,
                    "Subclass: equals is not final.");
    }

    @Test
    void fail_whenVerifyingTwoIncorrectClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class, IncorrectN.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "EqualsVerifier found a problem in 2 classes.",
                    "* " + INCORRECT_M,
                    "* " + INCORRECT_N,
                    "Subclass: equals is not final.",
                    "Reflexivity: object does not equal itself:");
    }

    @Test
    void fail_whenVerifyingAPackageWithTwoIncorrectClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE).verify())
                .assertFailure()
                .assertMessageContains(
                    "EqualsVerifier found a problem in 2 classes.",
                    "* " + INCORRECT_M,
                    "* " + INCORRECT_N,
                    "Subclass: equals is not final.",
                    "Reflexivity: object does not equal itself:");
    }

    @Test
    void fail_whenVerifyingAPackageRecursivelyWithFourIncorrectClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE, true).verify())
                .assertFailure()
                .assertMessageContains(
                    "EqualsVerifier found a problem in 4 classes.",
                    "* " + INCORRECT_M,
                    "* " + INCORRECT_N,
                    "* " + INCORRECT_O,
                    "* " + INCORRECT_P,
                    "Subclass: equals is not final.",
                    "Reflexivity: object does not equal itself:");
    }

    @Test
    void fail_whenVerifyingAPackageWithASuperclassWithFourIncorrectClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE, Object.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "EqualsVerifier found a problem in 4 classes.",
                    "* " + INCORRECT_M,
                    "* " + INCORRECT_N,
                    "* " + INCORRECT_O,
                    "* " + INCORRECT_P,
                    "Subclass: equals is not final.",
                    "Reflexivity: object does not equal itself:");
    }

    @Test
    void fail_whenCallingForPackage_whenPackageHasNoClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage("nl.jqno.equalsverifier.doesnotexist"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("nl.jqno.equalsverifier.doesnotexist", "doesn't contain any (non-Test) types");
    }

    @Test
    void fail_whenCallingForPackageRecursively_whenPackageHasNoClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage("nl.jqno.equalsverifier.doesnotexist", true))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("nl.jqno.equalsverifier.doesnotexist", "doesn't contain any (non-Test) types");
    }

    @Test
    void fail_whenCallingForPackageWithASuperclass_whenPackageHasNoClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage("nl.jqno.equalsverifier.doesnotexist", Object.class))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("nl.jqno.equalsverifier.doesnotexist", "doesn't contain any (non-Test) types");
    }

    @Test
    void succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreExcepted() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE).except(IncorrectM.class, IncorrectN.class).verify();
    }

    @Test
    void succeed_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenFailingClassesAreExcepted() {
        EqualsVerifier
                .forPackage(INCORRECT_PACKAGE, true)
                .except(IncorrectM.class, IncorrectN.class, IncorrectO.class, IncorrectP.class)
                .verify();
    }

    @Test
    void succeed_whenCallingForPackageWithASuperclassOnAPackageContainingFailingClasses_givenFailingClassesAreExcepted() {
        EqualsVerifier
                .forPackage(INCORRECT_PACKAGE, Object.class)
                .except(IncorrectM.class, IncorrectN.class, IncorrectO.class, IncorrectP.class)
                .verify();
    }

    @Test
    void fail_whenExceptingAClassThatDoesntExistInThePackage() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(CORRECT_PACKAGE).except(IncorrectM.class))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Unknown class(es) found", "IncorrectM");
    }

    @Test
    void fail_whenExceptingAClassThatDoesntExistInThePackageAndSubPackages() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(CORRECT_PACKAGE, true).except(IncorrectM.class))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Unknown class(es) found", "IncorrectM");
    }

    @Test
    void succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreExceptedByPredicate() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE).except(c -> c.getSimpleName().contains("Incorrect")).verify();
    }

    @Test
    void succeed_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenFailingClassesAreExceptedByPredicate() {
        EqualsVerifier
                .forPackage(INCORRECT_PACKAGE, true)
                .except(c -> c.getSimpleName().contains("Incorrect"))
                .verify();
    }

    @Test
    void fail_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreNotExceptedByPredicate() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE).except(c -> false).verify())
                .assertFailure()
                .assertMessageContains("EqualsVerifier found a problem in 2 classes");
    }

    @Test
    void fail_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenFailingClassesAreNotExceptedByPredicate() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE, true).except(c -> false).verify())
                .assertFailure()
                .assertMessageContains("EqualsVerifier found a problem in 4 classes");
    }

    @Test
    void succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenAllClassesAreExceptedByPredicate() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE).except(c -> true).verify();
    }

    @Test
    void succeed_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenAllClassesAreExceptedByPredicate() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE, true).except(c -> true).verify();
    }

    @Test
    void succeed_whenReportingOnSeveralCorrectClasses() {
        List<EqualsVerifierReport> reports = EqualsVerifier.forClasses(A.class, B.class, C.class).report();

        assertThat(reports).hasSize(3);
        assertSuccessful(reports.get(0), A.class);
        assertSuccessful(reports.get(1), B.class);
        assertSuccessful(reports.get(2), C.class);
    }

    @Test
    void fail_whenReportingOnOneIncorrectClass() {
        List<EqualsVerifierReport> reports = EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class).report();

        assertThat(reports).hasSize(3);
        assertSuccessful(reports.get(0), A.class);
        assertSuccessful(reports.get(2), C.class);
        assertUnsuccessful(reports.get(1), IncorrectM.class, "Subclass: equals is not final.");
    }

    @Test
    void fail_whenReportingOnTwoIncorrectClasses() {
        List<EqualsVerifierReport> reports =
                EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class, IncorrectN.class).report();

        assertThat(reports).hasSize(4);
        assertSuccessful(reports.get(0), A.class);
        assertSuccessful(reports.get(2), C.class);
        assertUnsuccessful(reports.get(1), IncorrectM.class, "Subclass: equals is not final.");
        assertUnsuccessful(reports.get(3), IncorrectN.class, "Reflexivity: object does not equal itself:");
    }

    private void assertSuccessful(EqualsVerifierReport report, Class<?> type) {
        assertThat(report.isSuccessful()).isTrue();
        assertThat(report.getType()).isEqualTo(type);
    }

    private void assertUnsuccessful(EqualsVerifierReport report, Class<?> type, String message) {
        assertThat(report.isSuccessful()).isFalse();
        assertThat(report.getType()).isEqualTo(type);
        assertThat(report.getMessage()).contains(message);
    }
}
