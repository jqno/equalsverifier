package nl.jqno.equalsverifier.verify_release.jar;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NodepClassesJarTest {

    private static final String FILENAME = "equalsverifier-nodep.jar";
    private static JarReader reader;
    private static JarAsserter jar;

    @BeforeAll
    public static void setup() {
        reader = new JarReader(FILENAME);
        jar = new JarAsserter(reader);
    }

    @AfterAll
    public static void clean() throws Exception {
        reader.close();
    }

    @Test
    public void presenceOfCoreClasses() {
        jar.assertPresenceOfCoreClasses();
    }

    @Test
    public void presenceOfMultiReleaseClasses() {
        jar.assertPresenceOfMultiReleaseClasses();
    }

    @Test
    public void assertPresenceOfEmbeddedDependencies() {
        jar.assertPresenceOfEmbeddedDepedencies();
    }

    @Test
    public void contentOfManifest() {
        jar.assertContentOfManifest("EqualsVerifier (no dependencies)");
    }

    @Test
    public void versionsOfClassFiles() {
        jar.assertVersionsOfClassFiles();
    }

    @Test
    public void versionsOfEmbeddedDependencies() {
        jar.assertVersionsOfEmbeddedClassFiles();
    }
}
