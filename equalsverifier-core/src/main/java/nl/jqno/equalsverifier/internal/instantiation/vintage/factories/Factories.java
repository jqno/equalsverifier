package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.Collection;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;

public final class Factories {

    private Factories() {
        // Do not instantiate
    }

    public static <T> PrefabValueFactory<T> values(T red, T blue, T redCopy) {
        return new SimpleFactory<>(red, blue, redCopy);
    }

    public static <A, T> PrefabValueFactory<T> simple(Func1<A, T> factory, Supplier<T> emptyFactory) {
        return new SimpleGenericFactory<>(factory, emptyFactory);
    }

    public static <A, B, T> PrefabValueFactory<T> simple(Func2<A, B, T> factory, Supplier<T> emptyFactory) {
        return new SimpleGenericFactory<>(factory, emptyFactory);
    }

    public static <A, T extends Collection<A>> PrefabValueFactory<T> collection(Supplier<T> emptyFactory) {
        Func1<A, T> f = a -> {
            T coll = emptyFactory.get();
            coll.add(a);
            return coll;
        };
        return Factories.<A, T>simple(f, emptyFactory);
    }
}
