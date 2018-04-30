package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;
import java.util.function.Function;

public class SimpleGenericFactory<A, T> extends AbstractReflectiveGenericFactory<T> {

    private final Function<A, T> factory;

    public SimpleGenericFactory(Function<A, T> factory) {
        this.factory = factory;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        TypeTag internalTag = determineActualTypeTagFor(0, tag);

        Object red = factory.apply(prefabValues.giveRed(internalTag));
        Object black = factory.apply(prefabValues.giveBlack(internalTag));
        Object redCopy = factory.apply(prefabValues.giveRed(internalTag));

        return Tuple.of(red, black, redCopy);
    }
}
