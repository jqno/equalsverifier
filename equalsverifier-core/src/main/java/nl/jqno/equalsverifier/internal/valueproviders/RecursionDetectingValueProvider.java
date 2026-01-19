package nl.jqno.equalsverifier.internal.valueproviders;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * A ValueProvider that detects recursion, and throws a {@link RecursionException} if it does.
 */
public class RecursionDetectingValueProvider implements ValueProvider {

    private ValueProvider inner;

    public void setValueProvider(ValueProvider newValueProvider) {
        this.inner = newValueProvider;
    }

    /** {@inheritDoc}} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        if (attributes.typeStackContains(tag)) {
            throw new RecursionException(attributes.typeStack());
        }
        return inner.provide(tag, attributes.addToStack(tag));
    }
}
