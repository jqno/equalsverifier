package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

@SuppressWarnings("rawtypes")
public final class EnumMapFactory<T> extends AbstractGenericFactory<T> {

    private final Function<Map, T> factory;

    public EnumMapFactory(Function<Map, T> factory) {
        this.factory = factory;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, valueProvider, clone, Enum.class);

        Map red = Map.of(valueProvider.giveRed(keyTag), valueProvider.giveBlue(valueTag));
        Map blue = Map.of(valueProvider.giveBlue(keyTag), valueProvider.giveBlue(valueTag));
        Map redCopy = Map.of(valueProvider.giveRed(keyTag), valueProvider.giveBlue(valueTag));

        return new Tuple<>(factory.apply(red), factory.apply(blue), factory.apply(redCopy));
    }
}
