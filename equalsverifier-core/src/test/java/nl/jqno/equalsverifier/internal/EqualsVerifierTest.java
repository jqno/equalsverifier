package nl.jqno.equalsverifier.internal;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class EqualsVerifierTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(EqualsVerifier.class);
    }
}
