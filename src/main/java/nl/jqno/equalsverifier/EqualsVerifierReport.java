package nl.jqno.equalsverifier;

/**
 * Contains the results of an {@link EqualsVerifier} run.
 *
 * <p>When the run was successful, should contain an empty message and a null cause. When the run
 * was unsuccessful, the message is identical to the message of the exception that {@link
 * EqualsVerifierApi#verify()} would throw, and the cause would be identical to its cause.
 */
public class EqualsVerifierReport {

    /** Represents a successful run of EqualsVerifier. */
    public static final EqualsVerifierReport SUCCESS = new EqualsVerifierReport(true, "", null);

    private final boolean successful;
    private final String message;
    private final Throwable cause;

    /**
     * Constructor.
     *
     * @param successful Whether the {@code EqualsVerifier} run was successful.
     * @param message Error message when the run is unsuccessful.
     * @param cause Exception when the run is unsuccessful.
     */
    public EqualsVerifierReport(boolean successful, String message, Throwable cause) {
        this.successful = successful;
        this.message = message;
        this.cause = cause;
    }

    /**
     * @return whether the class tested by {@link EqualsVerifierApi#report()} conforms to the {@link
     *     Object#equals(Object)} and {@link Object#hashCode()} contracts.
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * @return a detailed error message if the class tested by {@link EqualsVerifierApi#report()}
     *     does not conform to the {@link Object#equals(Object)} and {@link Object#hashCode()}
     *     contracts; or an empty string if it does.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return an exception indicating the source of the failure if the class tested by {@link
     *     EqualsVerifierApi#report()} does not conform to the {@link Object#equals(Object)} and
     *     {@link Object#hashCode()} contracts; or null if it does.
     */
    public Throwable getCause() {
        return cause;
    }
}
