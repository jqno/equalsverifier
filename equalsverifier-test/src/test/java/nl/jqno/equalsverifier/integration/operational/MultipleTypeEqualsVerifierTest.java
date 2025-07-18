package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.ScanOption;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.packages.correct.B;
import nl.jqno.equalsverifier.testhelpers.packages.correct.C;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperA;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperI;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectM;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectN;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.subpackage.IncorrectO;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.subpackage.IncorrectP;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("CheckReturnValue")
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
        EqualsVerifier.forPackage(CORRECT_PACKAGE, ScanOption.recursive()).verify();
    }

    @Test
    void fail_whenVerifyingAPackageWithASuperclass_ifSuperclassIsNull() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(SUBCLASSES_PACKAGE, ScanOption.mustExtend(null)).verify())
                .assertThrows(NullPointerException.class);
    }

    @Test
    void succeed_whenVerifyingAPackageWithASuperclass() {
        EqualsVerifier.forPackage(SUBCLASSES_PACKAGE, ScanOption.mustExtend(SuperA.class)).verify();
    }

    @Test
    void succeed_whenVerifyingAPackageWithASuperInterface_givenOneOfTheImplementationsIsAlsoAnInterface() {
        EqualsVerifier.forPackage(SUBCLASSES_PACKAGE, ScanOption.mustExtend(SuperI.class)).verify();
    }

    @Test
    void fail_whenVerifyingAThirdPartyPackage_becauseTheyDontPassEqualsVerifier() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage("org.objenesis").verify())
                .assertThrows(AssertionError.class)
                .assertMessageContains("EqualsVerifier found a problem");
    }

    @Test
    void fail_whenVerifyingAThirdPartyPackageWithNoClasses() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage("org.junit").verify())
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "doesn't contain any (non-Test) types");
    }

    @Test
    void fail_whenVerifyingAThirdPartyPackage_givenScanOptionIgnoreExternalJars() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage("org.objenesis", ScanOption.ignoreExternalJars()).verify())
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "doesn't contain any (non-Test) types");
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
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE, ScanOption.recursive()).verify())
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
                .when(
                    () -> EqualsVerifier
                            .forPackage(INCORRECT_PACKAGE, ScanOption.recursive(), ScanOption.mustExtend(Object.class))
                            .verify())
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
                .when(() -> EqualsVerifier.forPackage("nl.jqno.equalsverifier.doesnotexist", ScanOption.recursive()))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("nl.jqno.equalsverifier.doesnotexist", "doesn't contain any (non-Test) types");
    }

    @Test
    void fail_whenCallingForPackageWithASuperclass_whenPackageHasNoClasses() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forPackage("nl.jqno.equalsverifier.doesnotexist", ScanOption.mustExtend(Object.class)))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("nl.jqno.equalsverifier.doesnotexist", "doesn't contain any (non-Test) types");
    }

    @Test
    void succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreExcepted() {
        EqualsVerifier.forPackage(INCORRECT_PACKAGE, ScanOption.except(IncorrectM.class, IncorrectN.class)).verify();
    }

    @Test
    void succeed_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenFailingClassesAreExcepted() {
        EqualsVerifier
                .forPackage(
                    INCORRECT_PACKAGE,
                    ScanOption.recursive(),
                    ScanOption.except(IncorrectM.class, IncorrectN.class, IncorrectO.class, IncorrectP.class))
                .verify();
    }

    @Test
    void succeed_whenCallingForPackageWithASuperclassOnAPackageContainingFailingClasses_givenFailingClassesAreExcepted() {
        EqualsVerifier
                .forPackage(
                    INCORRECT_PACKAGE,
                    ScanOption.recursive(),
                    ScanOption.mustExtend(Object.class),
                    ScanOption.except(IncorrectM.class, IncorrectN.class, IncorrectO.class, IncorrectP.class))
                .verify();
    }

    @Test
    void fail_whenExceptingAClassThatDoesntExistInThePackage() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(CORRECT_PACKAGE, ScanOption.except(IncorrectM.class)))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Unknown class(es) found", "IncorrectM");
    }

    @Test
    void fail_whenExceptingAClassThatDoesntExistInThePackageAndSubPackages() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forPackage(CORRECT_PACKAGE, ScanOption.recursive(), ScanOption.except(IncorrectM.class)))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Unknown class(es) found", "IncorrectM");
    }

    @Test
    void succeed_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreExceptedByPredicate() {
        EqualsVerifier
                .forPackage(INCORRECT_PACKAGE, ScanOption.except(c -> c.getSimpleName().contains("Incorrect")))
                .verify();
    }

    @Test
    void succeed_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenFailingClassesAreExceptedByPredicate() {
        EqualsVerifier
                .forPackage(
                    INCORRECT_PACKAGE,
                    ScanOption.recursive(),
                    ScanOption.except(c -> c.getSimpleName().contains("Incorrect")))
                .verify();
    }

    @Test
    void fail_whenCallingForPackageOnAPackageContainingFailingClasses_givenFailingClassesAreNotExceptedByPredicate() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE, ScanOption.except(c -> false)).verify())
                .assertFailure()
                .assertMessageContains("EqualsVerifier found a problem in 2 classes");
    }

    @Test
    void fail_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenFailingClassesAreNotExceptedByPredicate() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forPackage(INCORRECT_PACKAGE, ScanOption.recursive(), ScanOption.except(c -> false))
                            .verify())
                .assertFailure()
                .assertMessageContains("EqualsVerifier found a problem in 4 classes");
    }

    @Test
    void fail_whenCallingForPackageOnAPackageContainingFailingClasses_givenAllClassesAreExceptedByPredicate() {
        ExpectedException
                .when(() -> EqualsVerifier.forPackage(INCORRECT_PACKAGE, ScanOption.except(c -> true)).verify())
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(INCORRECT_PACKAGE, "doesn't contain any (non-Test) types");
    }

    @Test
    void fail_whenCallingForPackageRecursivelyOnAPackageContainingFailingClasses_givenAllClassesAreExceptedByPredicate() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forPackage(INCORRECT_PACKAGE, ScanOption.recursive(), ScanOption.except(c -> true))
                            .verify())
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(INCORRECT_PACKAGE, "doesn't contain any (non-Test) types");
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
