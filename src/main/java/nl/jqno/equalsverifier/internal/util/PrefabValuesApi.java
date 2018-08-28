package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.simple;
import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;

public final class PrefabValuesApi {
    private PrefabValuesApi() {}

    public static <T> void addPrefabValues(FactoryCache factoryCache, Class<T> otherType, T red, T black) {
        if (otherType == null) {
            throw new NullPointerException("Type is null");
        }
        if (red == null || black == null) {
            throw new NullPointerException("One or both values are null.");
        }
        if (red.equals(black)) {
            throw new IllegalArgumentException("Both values are equal.");
        }

        if (red.getClass().isArray()) {
            factoryCache.put(otherType, values(red, black, red));
        }
        else {
            T redCopy = ObjectAccessor.of(red).copy();
            factoryCache.put(otherType, values(red, black, redCopy));
        }
    }

    public static <T> void addGenericPrefabValues(FactoryCache factoryCache, Class<T> otherType, Func1<?, T> factory) {
        if (factory == null) {
            throw new NullPointerException("Factory is null");
        }
        addGenericPrefabValueFactory(factoryCache, otherType, simple(factory, null), 1);
    }

    public static <T> void addGenericPrefabValues(FactoryCache factoryCache, Class<T> otherType, Func2<?, ?, T> factory) {
        if (factory == null) {
            throw new NullPointerException("Factory is null");
        }
        addGenericPrefabValueFactory(factoryCache, otherType, simple(factory, null), 2);
    }

    private static <S> void addGenericPrefabValueFactory(FactoryCache factoryCache, Class<S> otherType, PrefabValueFactory<S> factory, int arity) {
        if (otherType == null) {
            throw new NullPointerException("Type is null");
        }
        int n = otherType.getTypeParameters().length;
        if (n != arity) {
            throw new IllegalArgumentException("Number of generic type parameters doesn't match:\n  " +
                otherType.getName() + " has " + n + "\n  Factory has " + arity);
        }

        factoryCache.put(otherType, factory);
    }
}
