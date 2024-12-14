package nl.jqno.equalsverifier.internal.exceptions;

/** Signals that a class was inaccessible. */
public class ModuleException extends MessagingException {

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
