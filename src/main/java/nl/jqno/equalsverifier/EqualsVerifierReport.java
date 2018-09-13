package nl.jqno.equalsverifier;

public class EqualsVerifierReport {

    public static final EqualsVerifierReport SUCCESS = new EqualsVerifierReport(true, "", null);

    private final boolean successful;
    private final String message;
    private final Throwable exception;

    /* package protected */ EqualsVerifierReport(boolean successful, String message, Throwable exception) {
        this.successful = successful;
        this.message = message;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getException() {
        return exception;
    }
}
