package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

/**
 * Contains the results of an {@link nl.jqno.equalsverifier.EqualsVerifier} run.
 *
 * <p>
 * When the run was successful, should contain an empty message and a null cause. When the run was unsuccessful, the
 * message is identical to the message of the exception that {@link SingleTypeEqualsVerifierApi#verify()} would throw,
 * and the cause would be identical to its cause.
 *
 * @since 3.0
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
     *
     * @since 3.2
     */
    public static EqualsVerifierReport success(Class<?> type) {
        return new EqualsVerifierReport(type, true, "", null);
    }

    /**
     * Factory method for an unsuccessful run of {@code EqualsVerifier}.
     *
     * @param type    The class that was tested.
     * @param message Error message when the run is unsuccessful.
     * @param cause   Exception when the run is unsuccessful.
     * @return an {@code EqualsVerifierReport} representing the failed result of a run of {@code
     *     EqualsVerifier}.
     *
     * @since 3.2
     */
    public static EqualsVerifierReport failure(Class<?> type, String message, Throwable cause) {
        return new EqualsVerifierReport(type, false, message, cause);
    }

    /** Private constructor. Use {@link #success(Class)} or {@link #failure(Class, String, Throwable)} instead. */
    private EqualsVerifierReport(Class<?> type, boolean successful, String message, Throwable cause) {
        this.type = type;
        this.successful = successful;
        this.message = message;
        this.cause = cause;
    }

    /**
     * Returns the class that was tested.
     *
     * @return the class that was tested.
     *
     * @since 3.2
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Returns whether the tested class conforms to the {@code equals()} and {@code hashCode()} contracts.
     *
     * @return whether the class tested by {@link SingleTypeEqualsVerifierApi#report()} conforms to the
     *             {@link Object#equals(Object)} and {@link Object#hashCode()} contracts.
     *
     * @since 3.0
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Returns a detailed error message if the tested class does not conform to the {@code equals()} and
     * {@code hashCode()} contracts.
     *
     * @return a detailed error message if the class tested by {@link SingleTypeEqualsVerifierApi#report()} does not
     *             conform to the {@link Object#equals(Object)} and {@link Object#hashCode()} contracts; or an empty
     *             string if it does.
     *
     * @since 3.0
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the exception that detected the failure of the tested class to conform to the {@code equals()} and
     * {@code hashCode()} contracts.
     *
     * @return an exception indicating the source of the failure if the class tested by
     *             {@link SingleTypeEqualsVerifierApi#report()} does not conform to the {@link Object#equals(Object)}
     *             and {@link Object#hashCode()} contracts; or null if it does.
     *
     * @since 3.0
     */
    public Throwable getCause() {
        return cause;
    }
}
