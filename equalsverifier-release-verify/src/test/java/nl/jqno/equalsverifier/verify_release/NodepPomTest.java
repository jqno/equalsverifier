package nl.jqno.equalsverifier.verify_release;

import nl.jqno.equalsverifier.verify_release.pom.helper.PomAsserter;
import nl.jqno.equalsverifier.verify_release.pom.helper.PomReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NodepPomTest {

    private static final String FILENAME = "equalsverifier-nodep.pom";
    private static PomAsserter pom;

    @BeforeAll
    public static void setup() throws Exception {
        var reader = new PomReader(FILENAME);
        pom = new PomAsserter(reader);
    }

    @Test
    public void commonProperties() {
        pom.assertCommonProperties();
    }

    @Test
    public void artifactId() {
        pom.assertArtifactId("equalsverifier-nodep");
    }

    @Test
    public void noDependencies() {
        pom.assertNumberOfDependencies(0);
    }
}
