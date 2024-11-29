package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.VintageValueProvider;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates EnumSets using reflection, while
 * taking generics into account.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnumSetFactory<T> extends AbstractGenericFactory<T> {

    private final Function<Collection, T> factory;

    public EnumSetFactory(Function<Collection, T> factory) {
        this.factory = factory;
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        Attributes clone = attributes.cloneAndAdd(tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, valueProvider, clone, Enum.class);

        Collection red = new HashSet<>();
        Collection blue = new HashSet<>();
        Collection redCopy = new HashSet<>();
        red.add(valueProvider.giveRed(entryTag, clone));
        blue.add(valueProvider.giveBlue(entryTag, clone));
        redCopy.add(valueProvider.giveRed(entryTag, clone));

        return new Tuple<>(factory.apply(red), factory.apply(blue), factory.apply(redCopy));
    }
}
