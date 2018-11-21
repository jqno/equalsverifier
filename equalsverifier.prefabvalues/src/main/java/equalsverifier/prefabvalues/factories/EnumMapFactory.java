package equalsverifier.prefabvalues.factories;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabservice.Tuple;

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
    public Tuple<T> createValues(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabAbstract, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabAbstract, clone, Enum.class);

        Map red = new HashMap<>();
        Map black = new HashMap<>();
        Map redCopy = new HashMap<>();
        red.put(prefabAbstract.giveRed(keyTag), prefabAbstract.giveBlack(valueTag));
        black.put(prefabAbstract.giveBlack(keyTag), prefabAbstract.giveBlack(valueTag));
        redCopy.put(prefabAbstract.giveRed(keyTag), prefabAbstract.giveBlack(valueTag));

        return Tuple.of(factory.apply(red), factory.apply(black), factory.apply(redCopy));
    }
}
