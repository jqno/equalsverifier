package equalsverifier.utils.exceptions;

import equalsverifier.prefabvalues.MessagingException;
import equalsverifier.utils.Formatter;

/**
 * Signals that an EqualsVerfier assertion has failed.
 */
@SuppressWarnings("serial")
public class AssertionException extends MessagingException {
    public AssertionException(Formatter message) {
        super(message.format());
    }

    public AssertionException(Formatter message, Throwable cause) {
        super(message.format(), cause);
    }
}
