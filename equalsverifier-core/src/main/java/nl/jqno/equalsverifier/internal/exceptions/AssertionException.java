package nl.jqno.equalsverifier.internal.exceptions;

import nl.jqno.equalsverifier.internal.util.Formatter;

/** Signals that an EqualsVerfier assertion has failed. */
public class AssertionException extends MessagingException {

    public AssertionException(Formatter message) {
        super(message.format());
    }

    public AssertionException(Formatter message, Throwable cause) {
        super(message.format(), cause);
    }
}
