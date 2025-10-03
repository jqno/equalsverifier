package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Implementation of {@link PrefabValueFactory} that specializes in creating implementations of {@link Map}, taking
 * generics into account.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapFactory<T extends Map> extends AbstractGenericFactory<T> {

    private final Supplier<T> createEmpty;

    public MapFactory(Supplier<T> createEmpty) {
        this.createEmpty = createEmpty;
    }

    @Override
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    public Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, valueProvider, clone);
        Tuple<?> keyTuple = valueProvider.provideOrThrow(keyTag, null);
        Tuple<?> valueTuple = valueProvider.provideOrThrow(valueTag, null);

        // Use red for key and blue for value in the Red map to avoid having identical keys and
        // values.
        // But don't do it in the Blue map, or they may cancel each other out again.

        Object redKey = keyTuple.red();
        Object blueKey = keyTuple.blue();
        Object blueValue = valueTuple.blue();

        T red = createEmpty.get();
        red.put(redKey, blueValue);

        T blue = createEmpty.get();
        if (!redKey.equals(blueKey)) { // This happens with single-element enums
            blue.put(blueKey, blueValue);
        }

        T redCopy = createEmpty.get();
        redCopy.put(redKey, blueValue);

        return new Tuple<>(red, blue, redCopy);
    }
}
