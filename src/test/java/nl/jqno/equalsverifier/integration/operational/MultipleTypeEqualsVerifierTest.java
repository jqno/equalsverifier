package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.packages.correct.*;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.*;
import org.junit.Test;

public class MultipleTypeEqualsVerifierTest extends ExpectedExceptionTestBase {

    @Test
    public void succeed_whenVerifyingSeveralCorrectClasses() {
        EqualsVerifier.forClasses(A.class, B.class, C.class).verify();
    }

    @Test
    public void fail_whenVerifyingOneIncorrectClass() {
        expectFailure("EqualsVerifier found a problem in 1 class.", "IncorrectM");

        EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class).verify();
    }

    @Test
    public void fail_whenVerifyingTwoIncorrectClasses() {
        expectFailure("EqualsVerifier found a problem in 2 classes.", "IncorrectM", "IncorrectN");

        EqualsVerifier.forClasses(A.class, IncorrectM.class, C.class, IncorrectN.class).verify();
    }
}
