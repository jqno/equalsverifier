package nl.jqno.equalsverifier.verify_release.pom;

import nl.jqno.equalsverifier.verify_release.pom.helper.PomAsserter;
import nl.jqno.equalsverifier.verify_release.pom.helper.PomReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NodepPomTest {

    private static final String FILENAME = "equalsverifier-nodep.pom";
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
        pom.assertArtifactId("equalsverifier-nodep");
    }

    @Test
    void noDependencies() {
        pom.assertNumberOfDependencies(0);
    }
}
