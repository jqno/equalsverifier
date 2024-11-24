package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.util.function.Function;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;

public class CopyFactory<T, S> extends AbstractGenericFactory<T> {

    private final Class<S> type;
    private final Function<S, T> copy;

    public CopyFactory(Class<S> source, Function<S, T> copy) {
        this.type = source;
        this.copy = copy;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, ValueProvider valueProvider, Attributes attributes) {
        Attributes clone = attributes.cloneAndAdd(tag);
        TypeTag sourceTag = copyGenericTypesInto(type, tag);

        Tuple<S> source = valueProvider.provideOrThrow(sourceTag, clone);
        Tuple<T> copied = source.map(copy::apply);

        return Tuple.of(copied.getRed(), copied.getBlue(), copied.getRedCopy());
    }
}
