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
                    p -> p.assertNumberOfDependencies(8),
                    p -> p.assertDependencyIsOptional(1), // core
                    p -> p.assertDependencyIsOptional(2), // JDK 21
                    p -> p.assertDependencyIsOptional(3), // JDK 25
                    p -> p.assertDependency(4, "org.objenesis", "objenesis"),
                    p -> p.assertDependency(5, "net.bytebuddy", "byte-buddy"),
                    p -> p.assertDependencyIsOptional(6),
                    p -> p.assertDependency(6, "org.mockito", "mockito-core"),
                    p -> p.assertDependencyIsOptional(7),
                    p -> p.assertDependency(7, "org.jetbrains.kotlin", "kotlin-stdlib"),
                    p -> p.assertDependencyIsOptional(8),
                    p -> p.assertDependency(8, "org.jetbrains.kotlin", "kotlin-reflect"));
    }
}
