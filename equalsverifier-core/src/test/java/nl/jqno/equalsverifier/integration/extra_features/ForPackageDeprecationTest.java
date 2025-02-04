package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperA;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectM;
import nl.jqno.equalsverifier.testhelpers.packages.twoincorrect.IncorrectN;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class ForPackageDeprecationTest {
    @Test
    void directScanRecursively() {
        EqualsVerifier.forPackage("nl.jqno.equalsverifier.testhelpers.packages.correct", true).verify();
    }

    @Test
    void simpleScanRecursively() {
        EqualsVerifier.simple().forPackage("nl.jqno.equalsverifier.testhelpers.packages.correct", true).verify();
    }

    @Test
    void directMustExtend() {
        EqualsVerifier.forPackage("nl.jqno.equalsverifier.testhelpers.packages.subclasses", SuperA.class).verify();
    }

    @Test
    void simpleMustExtend() {
        EqualsVerifier
                .simple()
                .forPackage("nl.jqno.equalsverifier.integration.extra_features.simple_package", Object.class)
                .verify();
    }

    @Test
    void directExceptClass() {
        EqualsVerifier
                .forPackage("nl.jqno.equalsverifier.testhelpers.packages.twoincorrect")
                .except(IncorrectM.class, IncorrectN.class)
                .verify();
    }

    @Test
    void simpleExceptClass() {
        EqualsVerifier
                .simple()
                .forPackage("nl.jqno.equalsverifier.testhelpers.packages.twoincorrect")
                .except(IncorrectM.class, IncorrectN.class)
                .verify();
    }
}
