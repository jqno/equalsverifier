package nl.jqno.equalsverifier.verify_release.jar;

import nl.jqno.equalsverifier.verify_release.jar.helper.JarAsserter;
import nl.jqno.equalsverifier.verify_release.jar.helper.JarReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MainClassesJarTest {

    private static final String FILENAME = "equalsverifier-main.jar";
    private static JarAsserter jar;

    @BeforeAll
    public static void setup() {
        var reader = new JarReader(FILENAME);
        jar = new JarAsserter(reader);
    }

    @Test
    public void presenceOfMultiReleaseClasses() {
        jar.assertPresenceOfMultiReleaseClasses();
    }
}
