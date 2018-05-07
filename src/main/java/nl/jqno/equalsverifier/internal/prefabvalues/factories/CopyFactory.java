package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;
import java.util.function.Function;

public class CopyFactory<T, S> extends AbstractReflectiveGenericFactory<T> {
    private final Class<S> source;
    private Function<S, T> copy;

    public CopyFactory(Class<S> source, Function<S, T> copy) {
        this.source = source;
        this.copy = copy;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag sourceTag = copyGenericTypesInto(source, tag);
        prefabValues.realizeCacheFor(sourceTag, clone);

        S redSource = prefabValues.giveRed(sourceTag);
        S blackSource = prefabValues.giveBlack(sourceTag);
        S redCopySource = prefabValues.giveRedCopy(sourceTag);

        return Tuple.of(copy.apply(redSource), copy.apply(blackSource), copy.apply(redCopySource));
    }
}
