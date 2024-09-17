package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class PrefabValuesForFieldInRecordTest {

    @Test
    public void fail_whenRecordHasSinglePrecondition() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(SinglePrecondition.class).verify())
            .assertFailure()
            .assertMessageContains("Record:", "failed to run constructor", "[1]");
    }

    @Test
    public void succeed_whenRecordHasSinglePrecondition_givenPrefabValuesForField() {
        EqualsVerifier
            .forClass(SinglePrecondition.class)
            .withPrefabValuesForField("i", 111, 142)
            .suppress(Warning.ZERO_FIELDS)
            .verify();
    }

    @Test
    public void fail_whenRecordHasDualPrecondition() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(DualPrecondition.class).verify())
            .assertFailure()
            .assertMessageContains("Record:", "failed to run constructor", "[1, 1]");
    }

    @Test
    public void fail_whenRecordHasDualPrecondition_givenPrefabValuesForOnlyOneField() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(DualPrecondition.class)
                    .withPrefabValuesForField("x", 111, 142)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("Record:", "failed to run constructor", "[111, 1]");
    }

    @Test
    public void succeed_whenRecordHasDualPrecondition_givenPrefabValueForBothFields() {
        EqualsVerifier
            .forClass(DualPrecondition.class)
            .withPrefabValuesForField("x", 111, 142)
            .withPrefabValuesForField("y", 505, 555)
            .suppress(Warning.ZERO_FIELDS)
            .verify();
    }

    record SinglePrecondition(int i) {
        public SinglePrecondition {
            if (i < 100 || i > 200) {
                throw new IllegalArgumentException("i must be between 100 and 200!");
            }
        }
    }

    record DualPrecondition(int x, int y) {
        public DualPrecondition {
            if (x < 100 || x > 200) {
                throw new IllegalArgumentException("x must be between 100 and 200!");
            }
            if (y < 500 || y > 600) {
                throw new IllegalArgumentException("y must be between 500 and 600!");
            }
        }
    }
}
