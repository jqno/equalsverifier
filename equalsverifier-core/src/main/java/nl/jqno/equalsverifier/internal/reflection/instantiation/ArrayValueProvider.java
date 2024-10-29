package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.lang.reflect.Array;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class ArrayValueProvider implements ValueProvider {

    private final ValueProvider provider;

    public ArrayValueProvider(ValueProvider provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        Class<T> type = tag.getType();
        if (!type.isArray()) {
            return Optional.empty();
        }

        Class<?> componentType = type.getComponentType();
        TypeTag componentTag = new TypeTag(componentType);

        Tuple<?> tuple = componentType.isArray()
            ? provide(componentTag)
            : provider.provide(componentTag);

        T red = (T) Array.newInstance(componentType, 1);
        Array.set(red, 0, tuple.getRed());
        T blue = (T) Array.newInstance(componentType, 1);
        Array.set(blue, 0, tuple.getBlue());
        T redCopy = (T) Array.newInstance(componentType, 1);
        Array.set(redCopy, 0, tuple.getRedCopy());

        return Optional.of(Tuple.of(red, blue, redCopy));
    }
}
