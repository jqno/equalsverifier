package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provider of prefabricated instances of classes, delegating to other ValueProviders in sequence.
 */
public class ChainedValueProvider implements ValueProvider {

    private boolean locked = false;
    private final List<ValueProvider> providers = new ArrayList<>();

    /**
     * Adds providers to the chain, so they can be delegated to when providing a value.
     *
     * @param valueProviders ValueProviders to add to the chain.
     */
    public void register(ValueProvider... valueProviders) {
        if (locked) {
            throw new EqualsVerifierInternalBugException(
                "Provider is locked; can't add any new ones."
            );
        }
        for (ValueProvider p : valueProviders) {
            providers.add(p);
        }
        locked = true;
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        return providers
            .stream()
            .map(vp -> vp.<T>provide(tag, label))
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.empty());
    }
}
