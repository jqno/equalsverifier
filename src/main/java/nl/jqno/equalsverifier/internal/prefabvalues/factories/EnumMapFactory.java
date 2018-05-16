package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class EnumMapFactory<T> extends AbstractGenericFactory<T> {
    private final Function<Map, T> factory;

    public EnumMapFactory(Function<Map, T> factory) {
        this.factory = factory;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone, Enum.class);

        Map red = new HashMap<>();
        Map black = new HashMap<>();
        Map redCopy = new HashMap<>();
        red.put(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        black.put(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));
        redCopy.put(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));

        return Tuple.of(factory.apply(red), factory.apply(black), factory.apply(redCopy));
    }
}
