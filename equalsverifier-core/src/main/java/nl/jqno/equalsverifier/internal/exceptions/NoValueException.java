package nl.jqno.equalsverifier.internal.exceptions;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;

/** Signals that no value could be provided for a given type. */
@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "EqualsVerifier doesn't serialize.")
public class NoValueException extends MessagingException {

    public NoValueException(String description) {
        super(description);
    }

    public NoValueException(String description, Throwable cause) {
        super(description, cause);
    }
}
