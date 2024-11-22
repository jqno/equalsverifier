package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.util.function.Function;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;

public class CopyFactory<T, S> extends AbstractGenericFactory<T> {

    private final Class<S> source;
    private final Function<S, T> copy;

    public CopyFactory(Class<S> source, Function<S, T> copy) {
        this.source = source;
        this.copy = copy;
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        Attributes clone = attributes.cloneAndAdd(tag);
        TypeTag sourceTag = copyGenericTypesInto(source, tag);
        valueProvider.realizeCacheFor(sourceTag, clone);

        S redSource = valueProvider.giveRed(sourceTag);
        S blueSource = valueProvider.giveBlue(sourceTag);
        S redCopySource = valueProvider.giveRedCopy(sourceTag);

        return Tuple.of(copy.apply(redSource), copy.apply(blueSource), copy.apply(redCopySource));
    }
}
