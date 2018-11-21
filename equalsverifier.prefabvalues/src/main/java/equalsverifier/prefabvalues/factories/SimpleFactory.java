package equalsverifier.prefabvalues.factories;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabservice.Tuple;

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
    public Tuple<T> createValues(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        return tuple;
    }
}
