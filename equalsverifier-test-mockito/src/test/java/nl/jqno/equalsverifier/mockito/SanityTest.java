package nl.jqno.equalsverifier.mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.GetClassPoint;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

public class SanityTest {
    @Test
    void twoMockitoPointsAreUnequal() {
        var a = mock(Point.class);
        var b = mock(Point.class);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void incorrectEqualsIsStillDetected() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(GetClassPoint.class).verify())
                .assertFailure()
                .assertMessageContains("Subclass:");
    }

    @Test
    void mockitoIsReportedInErrorMessage() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(GetClassPoint.class).verify())
                .assertFailure()
                .assertMessageContains("Mockito: available");
    }
}
