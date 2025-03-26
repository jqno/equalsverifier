package nl.jqno.equalsverifier.mockito;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
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

    @Test
    void verifyClassThatCallsDirectlyIntoField() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(PojoWithoutEqualsContainer.class)
                            .suppress(Warning.NULL_FIELDS)
                            .verify())
                .assertFailure()
                .assertMessageContains("Unable to use Mockito to mock field methodCaller of type PojoWithoutEquals.");
    }

    @Test
    void verifyClassThatCallsDirectlyIntoField_givenPrefabValue() {
        var red = new PojoWithoutEquals(1);
        var blue = new PojoWithoutEquals(2);
        EqualsVerifier
                .forClass(PojoWithoutEqualsContainer.class)
                .withPrefabValues(PojoWithoutEquals.class, red, blue)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    record SinglePreconditionRecordContainer(PreconditionTypeHelper.SinglePreconditionRecord r) {}

    static final class PojoWithoutEquals {
        private final int i;

        PojoWithoutEquals(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }
    }

    static final class PojoWithoutEqualsContainer {
        private final PojoWithoutEquals methodCaller;

        PojoWithoutEqualsContainer(PojoWithoutEquals methodCaller) {
            this.methodCaller = methodCaller;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PojoWithoutEqualsContainer other
                    && Objects.equals(methodCaller.getI(), other.methodCaller.getI());
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodCaller.getI());
        }
    }

}
