package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.Func;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

public class SimpleGenericFactory<T> extends AbstractGenericFactory<T> {

    private final Func<T> factory;
    private final Supplier<T> emptyFactory;

    public SimpleGenericFactory(Func<T> factory, Supplier<T> emptyFactory) {
        this.factory = factory;
        this.emptyFactory = emptyFactory;
    }

    @Override
    public Tuple<T> createValues(
            TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);

        List<Object> redValues = new ArrayList<>();
        List<Object> blackValues = new ArrayList<>();

        boolean useEmpty = false;
        int n = tag.getType().getTypeParameters().length;
        for (int i = 0; i < n; i++) {
            TypeTag paramTag = determineAndCacheActualTypeTag(i, tag, prefabValues, clone);

            Object redValue = prefabValues.giveRed(paramTag);
            Object blackValue = prefabValues.giveBlack(paramTag);
            if (redValue.equals(blackValue)) { // This happens with single-element enums
                useEmpty = true;
            }
            redValues.add(redValue);
            blackValues.add(blackValue);
        }

        Object red = factory.apply(redValues);
        Object black =
                useEmpty && emptyFactory != null ? emptyFactory.get() : factory.apply(blackValues);
        Object redCopy = factory.apply(redValues);

        return Tuple.of(red, black, redCopy);
    }
}
