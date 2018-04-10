package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;

/**
 * Implementation of {@link PrefabValueFactory} that holds on to two instances
 * that have already been created.
 */
public class SimpleFactory<T> implements PrefabValueFactory<T> {
    private Tuple<T> tuple;

    public SimpleFactory(T red, T black, T redCopy) {
        this.tuple = new Tuple<>(red, black, redCopy);
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        return tuple;
    }
}
