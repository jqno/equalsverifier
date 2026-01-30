package nl.jqno.equalsverifier.integration.strictfinal;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.CanEqualColorPoint;
import nl.jqno.equalsverifier_testhelpers.types.CanEqualPoint;
import org.junit.jupiter.api.Test;

public class InheritanceTest {
    @Test
    void succeed_whenClassHasRedefinedSuperclass_givenConstructorsMatchFields() {
        EqualsVerifier.forClass(CanEqualColorPoint.class).withRedefinedSuperclass().verify();
    }

    @Test
    void succeed_whenClassHasRedefinedSuperclass_givenConstructosucceed_whenClassHasRedefinedSubclass_givenConstructorsMatchFieldsrsMatchFields() {
        EqualsVerifier.forClass(CanEqualPoint.class).withRedefinedSubclass(CanEqualColorPoint.class).verify();
    }
}
