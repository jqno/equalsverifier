package nl.jqno.equalsverifier.integration.operational;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.util.ObjenesisWrapper;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;

public class WithResetCachesTest {

    @Test
    public void resetObjenesisCacheOnEachRun() {
        Objenesis original = ObjenesisWrapper.getObjenesis();
        EqualsVerifier.forClass(A.class).verify();
        Objenesis reset = ObjenesisWrapper.getObjenesis();
        assertNotEquals(original, reset);
    }
}
