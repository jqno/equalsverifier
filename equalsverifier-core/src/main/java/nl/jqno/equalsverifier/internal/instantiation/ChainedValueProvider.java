package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Provider of prefabricated instances of classes, delegating to other ValueProviders in sequence.
 */
public class ChainedValueProvider implements ValueProvider {

    private final List<ValueProvider> providers;

    /**
     * Constructor.
     *
     * @param providers A list of ValueProviders that are checked in turn for a value.
     */
    public ChainedValueProvider(ValueProvider... providers) {
        this.providers = Arrays.asList(providers);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        return providers
                .stream()
                .map(vp -> vp.<T>provide(tag, attributes))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
