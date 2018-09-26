package nl.jqno.equalsverifier.internal.reflection.annotations;

import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;

public class NonnullAnnotationVerifierTest extends ExpectedExceptionTestBase {
    @Test
    public void coverConstructor() {
        coverThePrivateConstructor(NonnullAnnotationVerifier.class);
    }
}
