package nl.jqno.equalsverifier.internal.exceptions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "EqualsVerifier doesn't serialize.")
public class NoValueException extends MessagingException {

    private final TypeTag tag;
    private final String label;

    public NoValueException(TypeTag tag) {
        this(tag, null);
    }

    public NoValueException(TypeTag tag, String label) {
        super();
        this.tag = tag;
        this.label = label;
    }

    @Override
    public String getDescription() {
        return (
            "Could not find a value for " +
            tag +
            (label == null ? "" : " and label " + label) +
            ". Please add prefab values for this type."
        );
    }
}
