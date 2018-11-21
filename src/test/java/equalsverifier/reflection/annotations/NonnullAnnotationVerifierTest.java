package equalsverifier.reflection.annotations;

import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

import static equalsverifier.testhelpers.Util.coverThePrivateConstructor;

public class NonnullAnnotationVerifierTest extends ExpectedExceptionTestBase {
    @Test
    public void coverConstructor() {
        coverThePrivateConstructor(NonnullAnnotationVerifier.class);
    }
}
