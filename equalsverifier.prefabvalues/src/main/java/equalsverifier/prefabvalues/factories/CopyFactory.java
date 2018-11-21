package equalsverifier.prefabvalues.factories;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabservice.Tuple;

import java.util.LinkedHashSet;
import java.util.function.Function;

public class CopyFactory<T, S> extends AbstractGenericFactory<T> {
    private final Class<S> source;
    private Function<S, T> copy;

    public CopyFactory(Class<S> source, Function<S, T> copy) {
        this.source = source;
        this.copy = copy;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag sourceTag = copyGenericTypesInto(source, tag);
        prefabAbstract.realizeCacheFor(sourceTag, clone);

        S redSource = prefabAbstract.giveRed(sourceTag);
        S blackSource = prefabAbstract.giveBlack(sourceTag);
        S redCopySource = prefabAbstract.giveRedCopy(sourceTag);

        return Tuple.of(copy.apply(redSource), copy.apply(blackSource), copy.apply(redCopySource));
    }
}
