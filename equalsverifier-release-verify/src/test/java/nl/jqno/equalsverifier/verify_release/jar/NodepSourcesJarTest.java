package nl.jqno.equalsverifier.verify_release.jar;

import static nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter.EV;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NodepSourcesJarTest {

    private static final String FILENAME = "equalsverifier-nodep-sources.jar";
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
    public void presenceOfCoreSources() {
        jar.assertPresenceOf(EV + "/EqualsVerifier.java");
    }
}
