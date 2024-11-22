package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class EnumMapFactory<T> extends AbstractGenericFactory<T> {

    private final Function<Map, T> factory;

    public EnumMapFactory(Function<Map, T> factory) {
        this.factory = factory;
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        Attributes clone = attributes.cloneAndAdd(tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, valueProvider, clone, Enum.class);

        Map red = new HashMap<>();
        Map blue = new HashMap<>();
        Map redCopy = new HashMap<>();
        red.put(valueProvider.giveRed(keyTag), valueProvider.giveBlue(valueTag));
        blue.put(valueProvider.giveBlue(keyTag), valueProvider.giveBlue(valueTag));
        redCopy.put(valueProvider.giveRed(keyTag), valueProvider.giveBlue(valueTag));

        return Tuple.of(factory.apply(red), factory.apply(blue), factory.apply(redCopy));
    }
}
