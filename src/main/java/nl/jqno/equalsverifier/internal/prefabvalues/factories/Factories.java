package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Factories {
    private Factories() {
        // Do not instantiate
    }

    @SuppressWarnings("unchecked")
    public static <A, T> PrefabValueFactory<T> arity1(Function<A, T> factory, Supplier<T> emptyFactory) {
        return new SimpleGenericFactory<>(list -> factory.apply((A)list.get(0)), emptyFactory);
    }

    @SuppressWarnings("unchecked")
    public static <A, B, T> PrefabValueFactory<T> arity2(BiFunction<A, B, T> factory, Supplier<T> emptyFactory) {
        return new SimpleGenericFactory<>(list -> factory.apply((A)list.get(0), (B)list.get(1)), emptyFactory);
    }

    public static <A, T extends Collection<A>> PrefabValueFactory<T> collection(Supplier<T> emptyFactory) {
        return Factories.<A, T>arity1(a -> {
            T coll = emptyFactory.get();
            coll.add(a);
            return coll;
        }, emptyFactory);
    }

    public static <T, S> PrefabValueFactory<T> copy(Class<S> source, Function<S, T> copy) {
        return new CopyFactory<>(source, copy);
    }

    public static <K, V, T extends Map<K, V>> PrefabValueFactory<T> map(Supplier<T> emptyFactory) {
        return new MapFactory<>(emptyFactory);
    }
}
