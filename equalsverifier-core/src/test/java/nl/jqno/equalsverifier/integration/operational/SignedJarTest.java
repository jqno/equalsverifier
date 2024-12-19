package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.signedjar.SignedJarPoint;
import org.junit.jupiter.api.Test;

class SignedJarTest {

    @Test
    void succeed_whenTestingAClassFromASignedJar() {
        EqualsVerifier.forClass(SignedJarPoint.class).verify();
    }

    @Test
    void succeed_whenTestingAClassThatExtendsFromAClassFromASignedJar() {
        EqualsVerifier.forClass(SubclassOfSignedJarPoint.class).verify();
    }

    static final class SubclassOfSignedJarPoint extends SignedJarPoint {

        public SubclassOfSignedJarPoint(int x, int y) {
            super(x, y);
        }
    }
}
