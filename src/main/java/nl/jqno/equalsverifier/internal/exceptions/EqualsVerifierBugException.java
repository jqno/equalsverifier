package nl.jqno.equalsverifier.internal.exceptions;

/**
 * Signals a bug in EqualsVerifier.
 */
@SuppressWarnings("serial")
public class EqualsVerifierBugException extends RuntimeException {

    private static final String BUG = "This is a bug in EqualsVerifier. Please report this in the issue tracker at http://www.jqno.nl/equalsverifier";

    public EqualsVerifierBugException() {
        super(BUG);
    }

    public EqualsVerifierBugException(String message) {
        super(BUG + "\n" + message);
    }

    public EqualsVerifierBugException(Throwable cause) {
        super(BUG, cause);
    }

    public EqualsVerifierBugException(String message, Throwable cause) {
        super(BUG + "\n" + message, cause);
    }
}
