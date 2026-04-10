package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.signedjar.SignedJarPoint;
import org.junit.jupiter.api.Test;

class SignedJarIT {

    @Test
    void sanity_signedJarPointIsInASignedJar() throws Exception {
        String jarPath = System.getProperty("signedjar.path");
        try (JarFile jar = new JarFile(jarPath, true)) { // true: verify signatures
            String entryName = SignedJarPoint.class.getName().replace('.', '/') + ".class";
            JarEntry entry = jar.getJarEntry(entryName);

            assertThat(entry).isNotNull();

            // Reading the entry triggers signature verification
            jar.getInputStream(entry).transferTo(OutputStream.nullOutputStream());
            assertThat(entry.getCodeSigners()).isNotNull().isNotEmpty();
        }
    }

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
