package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.signedjar.SignedJarPoint;
import org.junit.Test;

public class SignedJarTest {
    @Test
    public void succeed_whenTestingAClassFromASignedJar() {
        EqualsVerifier.forClass(SignedJarPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenTestingAClassThatExtendsFromAClassFromASignedJar() {
        EqualsVerifier.forClass(SubclassOfSignedJarPoint.class)
                .verify();
    }

    static final class SubclassOfSignedJarPoint extends SignedJarPoint {
        public SubclassOfSignedJarPoint(int x, int y) {
            super(x, y);
        }
    }
}
