package nl.jqno.equalsverifier.verify_release.jar;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MainClassesJarTest {

    private static final String FILENAME = "equalsverifier-main.jar";
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
    void assertAbsenceOfEmbeddedDependencies() {
        jar.assertAbsenceOfEmbeddedDepedencies();
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
        jar.assertContentOfManifest("EqualsVerifier");
    }

    @Test
    void versionsOfClassFiles() {
        jar.assertVersionsOfClassFiles();
    }

    @Test
    void presenceOfModuleInfoWithDependencies() {
        jar.assertModuleInfoWithDependencies();
    }

    @Test
    void moduleInfoIsIdenticalToCore() throws Exception {
        try (var coreReader = new JarReader("equalsverifier-core.jar")) {
            var coreModuleinfo = coreReader.getContentOf("module-info.class");
            var mainModuleinfo = reader.getContentOf("module-info.class");
            assertThat(mainModuleinfo).containsExactly(coreModuleinfo);
        }
    }
}
