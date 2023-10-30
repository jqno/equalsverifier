package nl.jqno.equalsverifier.internal.exceptions;

/** Signals that a class was inaccessible. */
@SuppressWarnings("serial")
public class ModuleException extends MessagingException {

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
