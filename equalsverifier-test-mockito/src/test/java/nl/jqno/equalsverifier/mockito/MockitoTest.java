package nl.jqno.equalsverifier.mockito;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.FinalPointContainer;
import nl.jqno.equalsverifier_testhelpers.types.PointContainer;
import org.junit.jupiter.api.Test;

public class MockitoTest {
    @Test
    void verifySimpleClass() {
        EqualsVerifier.forClass(PointContainer.class).verify();
    }

    @Test
    void verifyFinalClassCintainer() {
        EqualsVerifier.forClass(FinalPointContainer.class).verify();
    }
}
