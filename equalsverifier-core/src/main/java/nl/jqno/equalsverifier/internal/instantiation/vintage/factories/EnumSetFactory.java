package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates EnumSets using reflection, while taking generics into
 * account.
 */
@SuppressWarnings("rawtypes")
public class EnumSetFactory<T> extends AbstractGenericFactory<T> {

    private final Function<Collection, T> factory;

    public EnumSetFactory(Function<Collection, T> factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    public Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone, Enum.class);

        Tuple<?> tuple = valueProvider.provideOrThrow(entryTag, null);
        return tuple.map(v -> factory.apply(Set.of(v)));
    }
}
