package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provider of prefabricated instances of classes, delegating to other ValueProviders in sequence.
 */
public class ChainedValueProvider implements ValueProvider {

    private final List<ValueProvider> providers;

    /** Constructor.
     *
     * @param providers ValueProviders to delegate to when providing a value.
     */
    public ChainedValueProvider(List<ValueProvider> providers) {
        this.providers = new ArrayList<>(providers);
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
