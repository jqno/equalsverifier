package nl.jqno.equalsverifier.internal.exceptions;

/** Signals that no value could be provided for a given type. */
public class NoValueException extends MessagingException {

    public NoValueException(String description) {
        super(description);
    }

    public NoValueException(String description, Throwable cause) {
        super(description, cause);
    }
}
