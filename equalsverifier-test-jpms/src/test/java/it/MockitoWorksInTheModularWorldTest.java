package it;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.ScanOption;
import nl.jqno.equalsverifier.jpms.model.*;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPoint;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPointContainer;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.RecursiveTypeContainer;
import org.junit.jupiter.api.Test;

public class MockitoWorksInTheModularWorldTest {

    @Test
    void classCanBeVerified() {
        EqualsVerifier.forClass(ClassPoint.class).verify();
    }

    @Test
    void classContainingClassCanBeVerified() {
        EqualsVerifier.forClass(ClassPointContainer.class).verify();
    }

    @Test
    void recordCanBeVerified() {
        EqualsVerifier.forClass(RecordPoint.class).verify();
    }

    @Test
    void recordContainingRecordCanBeVerified() {
        EqualsVerifier.forClass(RecordPointContainer.class).verify();
    }

    @Test
    void classContainingFieldsFromOtherJdkModulesCanBeVerified() {
        EqualsVerifier.forClass(FieldsFromJdkModulesHaver.class).verify();
    }

    @Test
    void recursiveClassCanBeVerified() {
        EqualsVerifier.forClass(RecursiveTypeContainer.class).verify();
    }

    @Test
    void nonFinalClassCanBeVerified() {
        EqualsVerifier.forClass(NonFinal.class).verify();
    }

    @Test
    void forPackageWorks() {
        EqualsVerifier
                .forPackage("nl.jqno.equalsverifier.jpms.model", ScanOption.except(Records.class::equals))
                .verify();
    }
}
