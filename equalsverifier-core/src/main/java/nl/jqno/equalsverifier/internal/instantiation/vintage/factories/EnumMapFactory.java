package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
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
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    public Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, valueProvider, clone, Enum.class);

        Tuple<?> keyTuple = valueProvider.provideOrThrow(keyTag, Attributes.empty());
        Tuple<?> valueTuple = valueProvider.provideOrThrow(valueTag, Attributes.empty());

        return keyTuple.map(k -> factory.apply(Map.of(k, valueTuple.blue())));
    }
}
