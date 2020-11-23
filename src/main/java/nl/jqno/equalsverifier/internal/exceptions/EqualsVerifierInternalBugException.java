package nl.jqno.equalsverifier.internal.exceptions;

/** Signals a bug in EqualsVerifier. */
@SuppressWarnings("serial")
public class EqualsVerifierInternalBugException extends RuntimeException {

    private static final String BUG =
        "This is a bug in EqualsVerifier. Please report this in the issue tracker at http://www.jqno.nl/equalsverifier";

    public EqualsVerifierInternalBugException() {
        super(BUG);
    }

    public EqualsVerifierInternalBugException(String message) {
        super(BUG + "\n" + message);
    }

    public EqualsVerifierInternalBugException(Throwable cause) {
        super(BUG, cause);
    }

    public EqualsVerifierInternalBugException(String message, Throwable cause) {
        super(BUG + "\n" + message, cause);
    }
}
