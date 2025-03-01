package nl.jqno.equalsverifier.internal.exceptions;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;

@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "EqualsVerifier doesn't serialize.")
public class NoValueException extends MessagingException {

    public NoValueException(String description) {
        super(description);
    }

    public NoValueException(String description, Throwable cause) {
        super(description, cause);
    }
}
