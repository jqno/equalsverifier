package equalsverifier.prefabvalues.factories;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabservice.Tuple;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.function.Function;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates EnumSets
 * using reflection, while taking generics into account.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumSetFactory<T> extends AbstractGenericFactory<T> {
    private final Function<Collection, T> factory;

    public EnumSetFactory(Function<Collection, T> factory) {
        this.factory = factory;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabAbstract, clone, Enum.class);

        Collection red = new HashSet<>();
        Collection black = new HashSet<>();
        Collection redCopy = new HashSet<>();
        red.add(prefabAbstract.giveRed(entryTag));
        black.add(prefabAbstract.giveBlack(entryTag));
        redCopy.add(prefabAbstract.giveRed(entryTag));

        return new Tuple<>(factory.apply(red), factory.apply(black), factory.apply(redCopy));
    }
}
