package nl.jqno.equalsverifier.internal.exceptions;

/** Signals that an instantiation went awry. */
public class InstantiatorException extends RuntimeException {

    public InstantiatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstantiatorException(String message) {
        super(message);
    }
}
