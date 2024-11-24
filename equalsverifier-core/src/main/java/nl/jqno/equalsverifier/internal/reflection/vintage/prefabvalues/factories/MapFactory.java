package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.util.Map;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;

/**
 * Implementation of {@link PrefabValueFactory} that specializes in creating implementations of
 * {@link Map}, taking generics into account.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapFactory<T extends Map> extends AbstractGenericFactory<T> {

    private final Supplier<T> createEmpty;

    public MapFactory(Supplier<T> createEmpty) {
        this.createEmpty = createEmpty;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, ValueProvider valueProvider, Attributes attributes) {
        Attributes clone = attributes.cloneAndAdd(tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, valueProvider, clone);

        // Use red for key and blue for value in the Red map to avoid having identical keys and
        // values.
        // But don't do it in the Blue map, or they may cancel each other out again.

        Tuple<?> key = valueProvider.provideOrThrow(keyTag, clone);
        Tuple<?> value = valueProvider.provideOrThrow(valueTag, clone);

        T red = createEmpty.get();
        red.put(key.getRed(), value.getBlue());

        T blue = createEmpty.get();
        if (!key.getRed().equals(key.getBlue())) { // This happens with single-element enums
            blue.put(key.getBlue(), value.getBlue());
        }

        T redCopy = createEmpty.get();
        redCopy.put(key.getRed(), value.getBlue());

        return new Tuple<>(red, blue, redCopy);
    }
}
