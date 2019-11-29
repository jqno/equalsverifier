package nl.jqno.equalsverifier.internal.exceptions;

/**
 * Superclass for exceptions that exist only to send a message to the user when something goes
 * wrong. These exceptions do not need to be included as a cause in the final stack trace. If they
 * have a cause, this cause will serve directly as the cause for the final stack trace, instead of
 * the exception itself.
 */
@SuppressWarnings("serial")
public abstract class MessagingException extends RuntimeException {
    private final String description;

    public MessagingException() {
        this(null, null);
    }

    public MessagingException(String description) {
        this(description, null);
    }

    public MessagingException(String description, Throwable cause) {
        super(null, cause);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
