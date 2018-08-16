package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.types.GetClassPoint;
import nl.jqno.equalsverifier.testhelpers.types.MutablePoint;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import org.junit.Test;

public class ConfiguredEqualsVerifierTest {

    @Test
    public void succeed_whenEqualsUsesGetClassInsteadOfInstanceOf_givenUsingGetClassIsPreConfigured() {
        EqualsVerifier.configure()
                .usingGetClass()
                .forClass(GetClassPoint.class)
                .verify();
    }

    @Test
    public void suppressedWarningsArePassedOn() {
        EqualsVerifier.configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClass(PointContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenTwoWarningsAreSuppressedButInDifferentPlaces() {
        EqualsVerifier.configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClass(MutablePoint.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        EqualsVerifier.configure()
                .suppress(Warning.NONFINAL_FIELDS)
                .forClass(MutablePoint.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }
}
