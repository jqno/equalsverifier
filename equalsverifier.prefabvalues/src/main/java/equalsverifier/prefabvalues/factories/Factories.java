package equalsverifier.prefabvalues.factories;

import equalsverifier.gentype.Func.Func1;
import equalsverifier.gentype.Func.Func2;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Factories {
    private Factories() {
        // Do not instantiate
    }

    public static <T> PrefabValueFactory<T> values(T red, T black, T redCopy) {
        return new SimpleFactory<>(red, black, redCopy);
    }

    public static <A, T> PrefabValueFactory<T> simple(Func1<A, T> factory, Supplier<T> emptyFactory) {
        return new SimpleGenericFactory<>(factory, emptyFactory);
    }

    public static <A, B, T> PrefabValueFactory<T> simple(Func2<A, B, T> factory, Supplier<T> emptyFactory) {
        return new SimpleGenericFactory<>(factory, emptyFactory);
    }

    public static <A, T extends Collection<A>> PrefabValueFactory<T> collection(Supplier<T> emptyFactory) {
        return Factories.<A, T>simple(a -> {
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
