package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Implementation of {@link PrefabValueFactory} that holds on to two instances that have already been created.
 */
public class SimpleFactory<T> implements PrefabValueFactory<T> {

    private final Tuple<T> tuple;

    public SimpleFactory(T red, T blue, T redCopy) {
        this.tuple = new Tuple<>(red, blue, redCopy);
    }

    @Override
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    public Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        return tuple;
    }
}
