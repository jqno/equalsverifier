package nl.jqno.equalsverifier.internal.reflection.annotations;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;

import org.junit.jupiter.api.Test;

public class NonnullAnnotationVerifierTest {

    @Test
    public void coverConstructor() {
        coverThePrivateConstructor(NonnullAnnotationVerifier.class);
    }
}
