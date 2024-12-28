package nl.jqno.equalsverifier.verify_release.jar;

import static nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter.EV;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MainJavadocJarTest {

    private static final String FILENAME = "equalsverifier-main-javadoc.jar";
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
    void presenceOfCoreSources() {
        jar.assertPresenceOf("/index.html", "/nl.jqno.equalsverifier" + EV + "/EqualsVerifier.html");
    }
}
