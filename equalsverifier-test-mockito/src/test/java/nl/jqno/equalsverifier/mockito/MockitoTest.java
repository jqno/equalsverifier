package nl.jqno.equalsverifier.mockito;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.FinalPointContainer;
import nl.jqno.equalsverifier_testhelpers.types.PointContainer;
import nl.jqno.equalsverifier_testhelpers.types.PreconditionTypeHelper;
import org.junit.jupiter.api.Test;

public class MockitoTest {
    @Test
    void verifySimpleClass() {
        EqualsVerifier.forClass(PointContainer.class).verify();
    }

    @Test
    void verifyFinalClassContainer() {
        EqualsVerifier.forClass(FinalPointContainer.class).verify();
    }

    @Test
    void verifyRecordWithPrecondition() {
        EqualsVerifier.forClass(SinglePreconditionRecordContainer.class).verify();
    }

    record SinglePreconditionRecordContainer(PreconditionTypeHelper.SinglePreconditionRecord r) {}
}
