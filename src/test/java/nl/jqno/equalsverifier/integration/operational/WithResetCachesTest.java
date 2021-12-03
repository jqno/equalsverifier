package nl.jqno.equalsverifier.integration.operational;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.util.ObjenesisWrapper;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.packages.correct.B;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;

public class WithResetCachesTest {

    @Test
    public void single() {
        Objenesis original = ObjenesisWrapper.getObjenesis();
        EqualsVerifier.forClass(A.class).withResetCaches();
        Objenesis reset = ObjenesisWrapper.getObjenesis();
        assertNotEquals(original, reset);
    }

    @Test
    public void multiple() {
        Objenesis original = ObjenesisWrapper.getObjenesis();
        EqualsVerifier.forClasses(A.class, B.class).withResetCaches();
        Objenesis reset = ObjenesisWrapper.getObjenesis();
        assertNotEquals(original, reset);
    }

    @Test
    public void configured() {
        Objenesis original = ObjenesisWrapper.getObjenesis();
        EqualsVerifier.configure().withResetCaches();
        Objenesis reset = ObjenesisWrapper.getObjenesis();
        assertNotEquals(original, reset);
    }
}
