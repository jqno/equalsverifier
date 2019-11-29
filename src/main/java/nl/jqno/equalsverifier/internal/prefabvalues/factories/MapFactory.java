package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

/**
 * Implementation of {@link PrefabValueFactory} that specializes in creating implementations of
 * {@link Map}, taking generics into account.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MapFactory<T extends Map> extends AbstractGenericFactory<T> {
    private final Supplier<T> createEmpty;

    public MapFactory(Supplier<T> createEmpty) {
        this.createEmpty = createEmpty;
    }

    @Override
    public Tuple<T> createValues(
            TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);

        // Use red for key and black for value in the Red map to avoid having identical keys and
        // values.
        // But don't do it in the Black map, or they may cancel each other out again.

        Object redKey = prefabValues.giveRed(keyTag);
        Object blackKey = prefabValues.giveBlack(keyTag);
        Object blackValue = prefabValues.giveBlack(valueTag);

        T red = createEmpty.get();
        red.put(redKey, blackValue);

        T black = createEmpty.get();
        if (!redKey.equals(blackKey)) { // This happens with single-element enums
            black.put(prefabValues.giveBlack(keyTag), blackValue);
        }

        T redCopy = createEmpty.get();
        redCopy.put(redKey, blackValue);

        return new Tuple<>(red, black, redCopy);
    }
}
