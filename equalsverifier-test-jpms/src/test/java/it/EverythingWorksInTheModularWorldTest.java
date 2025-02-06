package it;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.jpms.model.*;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPoint;
import nl.jqno.equalsverifier.jpms.model.Records.RecordPointContainer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class EverythingWorksInTheModularWorldTest {

    @Test
    void classCanBeVerified() {
        EqualsVerifier.forClass(ClassPoint.class).verify();
    }

    @Test
    void classContainingClassCanBeVerifier() {
        EqualsVerifier.forClass(ClassPointContainer.class).verify();
    }

    @Test
    @Disabled("It's impossble to load `equalsverifier-16` in the module world "
            + "because it creates a split path. We can enable this test again "
            + "when EqualsVerifier's baseline becomes Java 17")
    void recordCanBeVerified() {
        EqualsVerifier.forClass(RecordPoint.class).verify();
    }

    @Test
    @Disabled("It's impossble to load `equalsverifier-16` in the module world "
            + "because it creates a split path. We can enable this test again "
            + "when EqualsVerifier's baseline becomes Java 17")
    void recordContainingRecordCanBeVerified() {
        EqualsVerifier.forClass(RecordPointContainer.class).verify();
    }

    @Test
    void classContainingFieldsFromOtherJdkModulesCanBeVerifier() {
        EqualsVerifier.forClass(FieldsFromJdkModulesHaver.class).verify();
    }

    @Test
    void nonFinalClassCanBeVerified() {
        EqualsVerifier.forClass(NonFinal.class).verify();
    }

    @Test
    void forPackageWorks() {
        EqualsVerifier
                .forPackage("nl.jqno.equalsverifier.jpms.model")
                .except(c -> Records.class.equals(c.getEnclosingClass()) || Records.class.equals(c))
                .verify();
    }
}
