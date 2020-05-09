package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

/**
 * Contains the results of an {@link nl.jqno.equalsverifier.EqualsVerifier} run.
 *
 * <p>When the run was successful, should contain an empty message and a null cause. When the run
 * was unsuccessful, the message is identical to the message of the exception that {@link
 * SingleTypeEqualsVerifierApi#verify()} would throw, and the cause would be identical to its cause.
 */
public final class EqualsVerifierReport {

    private final Class<?> type;
    private final boolean successful;
    private final String message;
    private final Throwable cause;

    /**
     * Factory method for a successful run of {@code EqualsVerifier}.
     *
     * @param type The class that was tested.
     * @return an {@code EqualsVerifierReport} representing the successful result of a run of {@code
     *     EqualsVerifier}.
     */
    public static EqualsVerifierReport success(Class<?> type) {
        return new EqualsVerifierReport(type, true, "", null);
    }

    /**
     * Factory method for an unsuccessful run of {@code EqualsVerifier}.
     *
     * @param type The class that was tested.
     * @param message Error message when the run is unsuccessful.
     * @param cause Exception when the run is unsuccessful.
     * @return an {@code EqualsVerifierReport} representing the failed result of a run of {@code
     *     EqualsVerifier}.
     */
    public static EqualsVerifierReport failure(Class<?> type, String message, Throwable cause) {
        return new EqualsVerifierReport(type, false, message, cause);
    }

    /** Private constructor. Use {@link #SUCCESS} or {@link #failure(String, Throwable)} instead. */
    private EqualsVerifierReport(
            Class<?> type, boolean successful, String message, Throwable cause) {
        this.type = type;
        this.successful = successful;
        this.message = message;
        this.cause = cause;
    }

    /** @return the class that was tested. */
    public Class<?> getType() {
        return type;
    }

    /**
     * @return whether the class tested by {@link SingleTypeEqualsVerifierApi#report()} conforms to
     *     the {@link Object#equals(Object)} and {@link Object#hashCode()} contracts.
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * @return a detailed error message if the class tested by {@link
     *     SingleTypeEqualsVerifierApi#report()} does not conform to the {@link
     *     Object#equals(Object)} and {@link Object#hashCode()} contracts; or an empty string if it
     *     does.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return an exception indicating the source of the failure if the class tested by {@link
     *     SingleTypeEqualsVerifierApi#report()} does not conform to the {@link
     *     Object#equals(Object)} and {@link Object#hashCode()} contracts; or null if it does.
     */
    public Throwable getCause() {
        return cause;
    }
}
