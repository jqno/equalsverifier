package nl.jqno.equalsverifier.mockito;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.PointContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StrictModeTest {
    @Test
    void verifySimpleClass_givenMockitoStrictMode_viaExtendWith() {
        EqualsVerifier.forClass(PointContainer.class).verify();
    }
}
