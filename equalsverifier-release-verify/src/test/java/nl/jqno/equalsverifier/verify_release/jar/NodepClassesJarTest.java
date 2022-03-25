package nl.jqno.equalsverifier.verify_release.jar;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NodepClassesJarTest {

    private static final String FILENAME = "equalsverifier-nodep.jar";
    private static JarAsserter jar;

    @BeforeAll
    public static void setup() {
        var reader = new JarReader(FILENAME);
        jar = new JarAsserter(reader);
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
}
