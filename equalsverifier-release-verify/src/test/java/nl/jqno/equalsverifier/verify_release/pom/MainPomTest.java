package nl.jqno.equalsverifier.verify_release.pom;

import static org.junit.jupiter.api.Assertions.assertAll;

import nl.jqno.equalsverifier.verify_release.pom.helper.PomAsserter;
import nl.jqno.equalsverifier.verify_release.pom.helper.PomReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MainPomTest {

    private static final String FILENAME = "equalsverifier-main.pom";
    private static PomAsserter pom;

    @BeforeAll
    static void setup() throws Exception {
        var reader = new PomReader(FILENAME);
        pom = new PomAsserter(reader);
    }

    @Test
    void commonProperties() {
        pom.assertCommonProperties();
    }

    @Test
    void artifactId() {
        pom.assertArtifactId("equalsverifier");
    }

    @Test
    void dependencies() {
        assertAll(
            () -> pom.assertNumberOfDependencies(6),
            () -> pom.assertDependencyIsOptional(1),
            () -> pom.assertDependencyIsOptional(2),
            () -> pom.assertDependencyIsOptional(3),
            () -> pom.assertDependencyIsOptional(4),
            () -> pom.assertDependency(5, "org.objenesis", "objenesis"),
            () -> pom.assertDependency(6, "net.bytebuddy", "byte-buddy"));
    }
}
