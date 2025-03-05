package nl.jqno.equalsverifier.verify_release.pom;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(pom)
                .satisfies(
                    p -> p.assertNumberOfDependencies(7),
                    p -> p.assertDependencyIsOptional(1),
                    p -> p.assertDependencyIsOptional(2),
                    p -> p.assertDependencyIsOptional(3),
                    p -> p.assertDependencyIsOptional(4),
                    p -> p.assertDependencyIsOptional(5),
                    p -> p.assertDependency(6, "joda-time", "joda-time"),
                    p -> p.assertDependencyIsOptional(6),
                    p -> p.assertDependency(7, "com.google.guava", "guava"),
                    p -> p.assertDependencyIsOptional(7));
    }
}
