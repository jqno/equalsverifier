package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.Arrays;
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
    public ChainedValueProvider(ValueProvider... providers) {
        this.providers = Arrays.asList(providers);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag) {
        return providers
            .stream()
            .map(vp -> vp.<T>provide(tag))
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.empty());
    }
}
