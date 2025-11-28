package it;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.ScanOption;
import nl.jqno.equalsverifier.api.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.jpms.model.*;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPoint;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPointContainer;
import org.junit.jupiter.api.Test;

public class ObjectInstantiationWorksInTheModularWorldTest {

    private final ConfiguredEqualsVerifier ev = EqualsVerifier.configure().set(Mode.skipMockito());

    @Test
    void classCanBeVerified() {
        ev.forClass(ClassPoint.class).verify();
    }

    @Test
    void classContainingClassCanBeVerified() {
        ev.forClass(ClassPointContainer.class).verify();
    }

    @Test
    void recordCanBeVerified() {
        ev.forClass(RecordPoint.class).verify();
    }

    @Test
    void recordContainingRecordCanBeVerified() {
        ev.forClass(RecordPointContainer.class).verify();
    }

    @Test
    void classContainingFieldsFromOtherJdkModulesCanBeVerified() {
        ev.forClass(FieldsFromJdkModulesHaver.class).verify();
    }

    @Test
    void nonFinalClassCanBeVerified() {
        ev.forClass(NonFinal.class).verify();
    }

    @Test
    void forPackageWorks() {
        ev.forPackage("nl.jqno.equalsverifier.jpms.model", ScanOption.except(Records.class::equals)).verify();
    }
}
