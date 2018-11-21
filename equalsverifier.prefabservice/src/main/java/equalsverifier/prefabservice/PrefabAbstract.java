package equalsverifier.prefabservice;

import equalsverifier.gentype.TypeTag;

import java.util.LinkedHashSet;

public abstract class PrefabAbstract {
    public abstract <T> T giveRed(TypeTag tag);

    public abstract <T> T giveBlack(TypeTag tag);

    public abstract <T> T giveRedCopy(TypeTag tag);

    public abstract <T> T giveOther(TypeTag tag, T value);

    public abstract <T> Tuple<T> giveTuple(TypeTag tag);

    public abstract <T> void realizeCacheFor(TypeTag tag, LinkedHashSet<TypeTag> typeStack);
}
