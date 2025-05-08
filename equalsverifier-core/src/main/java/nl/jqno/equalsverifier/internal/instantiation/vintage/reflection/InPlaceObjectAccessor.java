package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.*;
import org.objenesis.Objenesis;

/**
 * Implementation of ObjectAccessor that modifies its wrapped object in-place through reflection.
 *
 * @param <T> The specified object's class.
 */
final class InPlaceObjectAccessor<T> extends ObjectAccessor<T> {

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */ InPlaceObjectAccessor(T object, Class<T> type) {
        super(object, type);
    }

    /** {@inheritDoc} */
    @Override
    public T copy(Objenesis objenesis) {
        T copy = Instantiator.of(type(), objenesis).instantiate();
        for (FieldProbe probe : FieldIterable.of(type())) {
            fieldModifierFor(probe).copyTo(copy);
        }
        return copy;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    public ObjectAccessor<T> scramble(
            VintageValueProvider valueProvider,
            TypeTag enclosingType,
            LinkedHashSet<TypeTag> typeStack) {
        for (FieldProbe probe : FieldIterable.of(type())) {
            fieldModifierFor(probe).changeField(valueProvider, enclosingType, typeStack);
        }
        return this;
    }

    private FieldModifier fieldModifierFor(FieldProbe probe) {
        return FieldModifier.of(probe, get());
    }
}
