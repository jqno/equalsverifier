package nl.jqno.equalsverifier.internal.exceptions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "EqualsVerifier doesn't serialize.")
public class NoValueException extends MessagingException {

    private final TypeTag tag;

    public NoValueException(TypeTag tag) {
        super();
        this.tag = tag;
    }

    @Override
    public String getDescription() {
        return "Could not find a value for " + tag + ". Please add prefab values for this type.";
    }
}
