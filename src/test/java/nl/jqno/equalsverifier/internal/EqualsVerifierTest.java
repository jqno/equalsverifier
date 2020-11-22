package nl.jqno.equalsverifier.internal;

import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class EqualsVerifierTest {
    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(EqualsVerifier.class);
    }
}
