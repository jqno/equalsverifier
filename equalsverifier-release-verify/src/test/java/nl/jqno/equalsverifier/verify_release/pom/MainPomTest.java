package nl.jqno.equalsverifier.verify_release.pom;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(pom)
                .satisfies(
                    p -> p.assertNumberOfDependencies(9),
                    p -> p.assertDependencyIsOptional(1),
                    p -> p.assertDependencyIsOptional(2),
                    p -> p.assertDependencyIsOptional(3),
                    p -> p.assertDependencyIsOptional(4),
                    p -> p.assertDependencyIsOptional(5),
                    p -> p.assertDependency(6, "org.objenesis", "objenesis"),
                    p -> p.assertDependency(7, "net.bytebuddy", "byte-buddy"),
                    p -> p.assertDependency(8, "joda-time", "joda-time"),
                    p -> p.assertDependencyIsOptional(8),
                    p -> p.assertDependency(9, "com.google.guava", "guava"),
                    p -> p.assertDependencyIsOptional(9));
    }
}
