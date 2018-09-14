package nl.jqno.equalsverifier;

public class EqualsVerifierReport {

    public static final EqualsVerifierReport SUCCESS = new EqualsVerifierReport(true, "", null);

    private final boolean successful;
    private final String message;
    private final Throwable cause;

    /* package protected */ EqualsVerifierReport(boolean successful, String message, Throwable cause) {
        this.successful = successful;
        this.message = message;
        this.cause = cause;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }
}
