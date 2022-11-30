package nl.jqno.equalsverifier.internal.util;

public final class ErrorMessage {

    private ErrorMessage() {}

    private static final String WEBSITE_URL =
        "For more information, go to: https://www.jqno.nl/equalsverifier/errormessages";

    public static String suffix() {
        return Formatter
            .of(
                "%%\n(EqualsVerifier %%, JDK %% on %%)",
                WEBSITE_URL,
                ErrorMessage.class.getPackage().getImplementationVersion(),
                System.getProperty("java.version"),
                System.getProperty("os.name")
            )
            .format();
    }
}
