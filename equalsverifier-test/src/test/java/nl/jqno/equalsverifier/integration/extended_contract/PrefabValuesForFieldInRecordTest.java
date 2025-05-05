package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.PreconditionTypeHelper.*;
import org.junit.jupiter.api.Test;

class PrefabValuesForFieldInRecordTest {

    @Test
    void fail_whenRecordHasSinglePrecondition() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SinglePreconditionRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record:", "failed to run constructor", "[1]");
    }

    @Test
    void succeed_whenRecordHasSinglePrecondition_givenPrefabValuesForField() {
        EqualsVerifier.forClass(SinglePreconditionRecord.class).withPrefabValuesForField("i", 111, 142).verify();
    }

    @Test
    void fail_whenRecordHasDualPrecondition() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(DualPreconditionRecord.class).verify())
                .assertFailure()
                .assertMessageContains("Record:", "failed to run constructor", "[1, 1]");
    }

    @Test
    void fail_whenRecordHasDualPrecondition_givenPrefabValuesForOnlyOneField() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(DualPreconditionRecord.class)
                            .withPrefabValuesForField("x", 111, 142)
                            .verify())
                .assertFailure()
                .assertMessageContains("Record:", "failed to run constructor", "[111, 1]");
    }

    @Test
    void succeed_whenRecordHasDualPrecondition_givenPrefabValueForBothFields() {
        EqualsVerifier
                .forClass(DualPreconditionRecord.class)
                .withPrefabValuesForField("x", 111, 142)
                .withPrefabValuesForField("y", 505, 555)
                .verify();
    }
}
