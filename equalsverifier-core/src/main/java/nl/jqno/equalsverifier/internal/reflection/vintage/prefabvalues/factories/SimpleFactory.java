package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;

/**
 * Implementation of {@link PrefabValueFactory} that holds on to two instances that have already
 * been created.
 */
public class SimpleFactory<T> implements PrefabValueFactory<T> {

    private final Tuple<T> tuple;

    public SimpleFactory(T red, T blue, T redCopy) {
        this.tuple = new Tuple<>(red, blue, redCopy);
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        return tuple;
    }
}
