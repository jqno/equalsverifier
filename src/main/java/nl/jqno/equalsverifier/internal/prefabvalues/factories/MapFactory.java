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

        // Use red for key and blue for value in the Red map to avoid having identical keys and
        // values.
        // But don't do it in the Blue map, or they may cancel each other out again.

        Object redKey = prefabValues.giveRed(keyTag);
        Object blueKey = prefabValues.giveBlue(keyTag);
        Object blueValue = prefabValues.giveBlue(valueTag);

        T red = createEmpty.get();
        red.put(redKey, blueValue);

        T blue = createEmpty.get();
        if (!redKey.equals(blueKey)) { // This happens with single-element enums
            blue.put(prefabValues.giveBlue(keyTag), blueValue);
        }

        T redCopy = createEmpty.get();
        redCopy.put(redKey, blueValue);

        return new Tuple<>(red, blue, redCopy);
    }
}
