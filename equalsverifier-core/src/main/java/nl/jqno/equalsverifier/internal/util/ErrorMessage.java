package nl.jqno.equalsverifier.internal.util;

import java.io.IOException;
import java.util.Properties;

import nl.jqno.equalsverifier.internal.reflection.ModuleProbe;

public final class ErrorMessage {

    private ErrorMessage() {}

    private static final String WEBSITE_URL =
            "For more information, go to: https://www.jqno.nl/equalsverifier/errormessages";

    public static String suffix() {
        return Formatter
                .of(
                    "%%\n(EqualsVerifier %%, JDK %% running on %%, on %%. Mockito: %%.)",
                    WEBSITE_URL,
                    getVersion(),
                    System.getProperty("java.version"),
                    ModuleProbe.runsOnModulePath() ? "modulepath" : "classpath",
                    System.getProperty("os.name"),
                    ExternalLibs.isMockitoAvailable() ? "available" : "not available")
                .format();
    }

    private static String getVersion() {
        var props = new Properties();
        try (var is = ErrorMessage.class.getResourceAsStream("/version.properties")) {
            props.load(is);
            return props.getProperty("version");
        }
        catch (IOException ignored) {
            return "unknown";
        }
    }
}
