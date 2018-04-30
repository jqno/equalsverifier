package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

/**
 * Implementation of {@link PrefabValueFactory} that specializes in creating
 * implementations of {@link Collection}, taking generics into account.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CollectionFactory<T extends Collection> extends AbstractReflectiveGenericFactory<T> {
    private final Supplier<T> createEmpty;

    public CollectionFactory(Supplier<T> createEmpty) {
        this.createEmpty = createEmpty;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);

        T red = createEmpty.get();
        Object redElem = prefabValues.giveRed(entryTag);
        red.add(redElem);

        T black = createEmpty.get();
        Object blackElem = prefabValues.giveBlack(entryTag);
        if (!redElem.equals(blackElem)) { // This happens with single-element enums
            black.add(blackElem);
        }

        T redCopy = createEmpty.get();
        redCopy.add(redElem);

        return new Tuple<>(red, black, redCopy);
    }
}
