package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class EnumMapFactory<T> extends AbstractGenericFactory<T> {

    private final Function<Map, T> factory;

    public EnumMapFactory(Function<Map, T> factory) {
        this.factory = factory;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, ValueProvider valueProvider, Attributes attributes) {
        Attributes clone = attributes.cloneAndAdd(tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, valueProvider, clone, Enum.class);

        Tuple<?> key = valueProvider.provideOrThrow(keyTag, clone);
        Tuple<?> value = valueProvider.provideOrThrow(valueTag, clone);

        Map red = new HashMap<>();
        Map blue = new HashMap<>();
        Map redCopy = new HashMap<>();
        red.put(key.getRed(), value.getBlue());
        blue.put(key.getBlue(), value.getBlue());
        redCopy.put(key.getRedCopy(), value.getBlue());

        return Tuple.of(factory.apply(red), factory.apply(blue), factory.apply(redCopy));
    }
}
