package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;

@SuppressWarnings("rawtypes")
public class ThreadLocalFactory extends AbstractReflectiveGenericFactory<ThreadLocal> {
    @Override
    public Tuple<ThreadLocal> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);

        Object redInitial = prefabValues.giveRed(entryTag);
        Object blackInitial = prefabValues.giveBlack(entryTag);

        ThreadLocal red = create(redInitial);
        ThreadLocal black = create(blackInitial);
        return Tuple.of(red, black, red);
    }

    private static ThreadLocal create(final Object value) {
        return new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return value;
            }
        };
    }
}
