package nl.jqno.equalsverifier.internal.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.internal.reflection.Util;
import org.junit.jupiter.api.Test;

public final class TestPresenceTest {

    @Test
    public void javafxTestsArePresentOnJdk11() {
        assertTrue(
            isTestAvailableOnJdk(
                11,
                "nl.jqno.equalsverifier.integration.extended_contract.JavaFxClassesTest"
            )
        );
    }

    @Test
    public void recordTestsArePresentOnJdk16() {
        assertTrue(
            isTestAvailableOnJdk(
                16,
                "nl.jqno.equalsverifier.integration.extended_contract.RecordsTest"
            )
        );
    }

    private boolean isTestAvailableOnJdk(int jdkVersion, String className) {
        boolean isJdk = isAtLeastJdk(jdkVersion);
        boolean available = isClassAvailable(className);
        return isJdk == available;
    }

    private boolean isClassAvailable(String className) {
        return Util.classForName(className) != null;
    }

    private boolean isAtLeastJdk(int requiredVersion) {
        String jdkVersion = System.getProperty("java.version");
        int actualVersion = Integer.parseInt(jdkVersion.split("\\.|-")[0]);
        return requiredVersion <= actualVersion;
    }
}
