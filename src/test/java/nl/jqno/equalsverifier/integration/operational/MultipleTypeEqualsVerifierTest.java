package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.packages.correct.*;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.*;
import org.junit.Test;

public class MultipleTypeEqualsVerifierTest extends ExpectedExceptionTestBase {
    private static final String INCORRECT_M =
            "nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectM";
    private static final String INCORRECT_N =
            "nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectN";

    @Test
    public void succeed_whenVerifyingSeveralCorrectClasses() {
        EqualsVerifier.forClasses(A.class, B.class, C.class).verify();
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
}
