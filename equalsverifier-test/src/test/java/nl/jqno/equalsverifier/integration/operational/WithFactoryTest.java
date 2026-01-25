package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

public class WithFactoryTest {
    @Test
    void simple() {
        EqualsVerifier
                .forClass(FinalPoint.class)
                .withFactory(values -> new FinalPoint(values.getInt("x"), values.getInt("y")))
                .verify();
    }
}
