package nl.jqno.equalsverifier.verify_release.jar;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NodepClassesJarTest {

    private static final String FILENAME = "equalsverifier-nodep.jar";
    private static JarReader reader;
    private static JarAsserter jar;

    @BeforeAll
    static void setup() {
        reader = new JarReader(FILENAME);
        jar = new JarAsserter(reader);
    }

    @AfterAll
    static void clean() throws Exception {
        reader.close();
    }

    @Test
    void presenceOfCoreClasses() {
        jar.assertPresenceOfCoreClasses();
    }

    @Test
    void presenceOfMultiReleaseClasses() {
        jar.assertPresenceOfMultiReleaseClasses();
    }

    @Test
    void absenceOfMetainfStuff() {
        jar.assertAbsenceOfMetainfStuff();
    }

    @Test
    void assertAbsenceOfTesthelpers() {
        jar.assertAbsenceOfTesthelpers();
    }

    @Test
    void assertPresenceOfEmbeddedDependencies() {
        jar.assertPresenceOfEmbeddedDepedencies();
    }

    @Test
    void assertAbsenceOfEmbeddedDependenciesUnderTheirOwnPath() {
        jar.assertAbsenceOfEmbeddedDepedenciesUnderTheirOriginalPath();
    }

    @Test
    void absenceOfOptionalDependencies() {
        jar.assertAbsenceOfOptionalDependencies();
    }

    @Test
    void contentOfManifest() {
        jar.assertContentOfManifest("EqualsVerifier (no dependencies)");
    }

    @Test
    void versionsOfClassFiles() {
        jar.assertVersionsOfClassFiles();
    }

    @Test
    void versionsOfEmbeddedDependencies() {
        jar.assertVersionsOfEmbeddedClassFiles();
    }

    @Test
    void presenceOfModuleInfoWithoutDependencies() {
        jar.assertModuleInfoWithoutDependencies();
    }
}
