package nl.jqno.equalsverifier.verify_release.jar;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MainClassesJarTest {

    private static final String FILENAME = "equalsverifier-main.jar";
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
    public void assertAbsenceOfEmbeddedDependencies() {
        jar.assertAbsenceOfEmbeddedDepedencies();
    }

    @Test
    public void contentOfManifest() {
        jar.assertContentOfManifest("EqualsVerifier");
    }

    @Test
    public void versionsOfClassFiles() {
        jar.assertVersionsOfClassFiles();
    }
}
