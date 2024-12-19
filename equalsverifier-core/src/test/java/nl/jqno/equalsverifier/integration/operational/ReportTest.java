package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class ReportTest {

    @Test
    void isEmptyWhenClassIsCorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(FinalPoint.class).report();

        assertThat(report.getType()).isEqualTo(FinalPoint.class);
        assertThat(report.isSuccessful()).isTrue();
        assertThat(report.getMessage()).isEqualTo("");
        assertThat(report.getCause()).isNull();
    }

    @Test
    void containsAppropriateErrorMessageAndExceptionWhenClassIsIncorrect() {
        EqualsVerifierReport report = EqualsVerifier.forClass(Point.class).report();

        assertThat(report.getType()).isEqualTo(Point.class);
        assertThat(report.isSuccessful()).isFalse();
        assertThat(report.getMessage())
                .startsWith("EqualsVerifier found a problem in class nl.jqno.equalsverifier.testhelpers.types.Point");
        assertThat(report.getCause().getClass()).isEqualTo(AssertionException.class);
        assertThat(report.getCause().getMessage()).isNull();
    }

    @Test
    void reportReturnsTheSameInformationAsVerify() {
        EqualsVerifierReport report = EqualsVerifier.forClass(Point.class).report();
        try {
            EqualsVerifier.forClass(Point.class).verify();
            fail("Should have failed");
        }
        catch (AssertionError e) {
            assertThat(report.getMessage()).isEqualTo(e.getMessage());
            assertThat(report.getCause().getClass()).isEqualTo(e.getCause().getClass());

            StackTraceElement[] verified = e.getCause().getStackTrace();
            StackTraceElement[] reported = report.getCause().getStackTrace();
            // Implementation detail:
            // `report` has 1 extra stack frame because it delegates to an overload.
            assertThat(reported.length).isEqualTo(verified.length + 1);

            for (int i = 0; i < verified.length; i++) {
                if (!verified[i].getMethodName().equals("verify")) {
                    // When the `verify` method is reached, the stacktraces start to diverge.
                    break;
                }

                assertThat(reported[i]).isEqualTo(verified[i]);
            }
        }
    }
}
