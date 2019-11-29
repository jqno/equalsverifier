package nl.jqno.equalsverifier.internal.reflection.annotations;

import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;

import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

public class NonnullAnnotationVerifierTest extends ExpectedExceptionTestBase {
    @Test
    public void coverConstructor() {
        coverThePrivateConstructor(NonnullAnnotationVerifier.class);
    }
}
