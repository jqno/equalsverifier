package nl.jqno.equalsverifier.internal.valueproviders;

import java.lang.reflect.Array;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * A ValueProvider for arrays.
 */
public class ArrayValueProvider implements ValueProvider {
    private final ValueProvider vp;

    public ArrayValueProvider(ValueProvider vp) {
        this.vp = vp;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Class<T> type = tag.getType();
        if (!type.isArray()) {
            return Optional.empty();
        }

        Class<?> componentType = type.getComponentType();
        TypeTag componentTag = new TypeTag(componentType);

        return vp.provide(componentTag, attributes).map(tuple -> {
            T red = (T) Array.newInstance(componentType, 1);
            Array.set(red, 0, tuple.red());
            T blue = (T) Array.newInstance(componentType, 2);
            Array.set(blue, 0, tuple.blue());
            Array.set(blue, 1, tuple.red());
            T redCopy = (T) Array.newInstance(componentType, 1);
            Array.set(redCopy, 0, tuple.red());
            return new Tuple<>(red, blue, redCopy);
        });
    }
}
