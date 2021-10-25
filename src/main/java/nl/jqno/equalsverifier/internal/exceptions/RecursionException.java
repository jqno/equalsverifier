package nl.jqno.equalsverifier.internal.exceptions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Iterator;
import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

/** Signals that a recursion has been detected while traversing the fields of a data structure. */
@SuppressWarnings("serial")
@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "EqualsVerifier doesn't serialize.")
public class RecursionException extends MessagingException {

    private final LinkedHashSet<TypeTag> typeStack;

    /**
     * Constructor.
     *
     * @param typeStack A collection of types that have been encountered prior to detecting the
     *     recursion.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "There's no such thing as an UnmodifiableLinkedHashSet and we need the ordering."
    )
    public RecursionException(LinkedHashSet<TypeTag> typeStack) {
        super();
        this.typeStack = typeStack;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recursive datastructure.\nAdd prefab values for one of the following types: ");
        Iterator<TypeTag> i = typeStack.iterator();
        sb.append(i.next().toString());
        while (i.hasNext()) {
            sb.append(", ");
            sb.append(i.next().toString());
        }
        sb.append(".");
        return sb.toString();
    }
}
