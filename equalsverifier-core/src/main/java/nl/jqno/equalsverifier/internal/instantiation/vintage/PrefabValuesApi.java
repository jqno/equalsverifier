package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.simple;

import java.lang.reflect.InaccessibleObjectException;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.util.Validations;
import org.objenesis.Objenesis;

public final class PrefabValuesApi {

    private PrefabValuesApi() {}

    public static <T> void addPrefabValues(
            UserPrefabValueProvider userPrefabs,
            Objenesis objenesis,
            Class<T> otherType,
            T red,
            T blue) {
        Validations.validateRedAndBluePrefabValues(otherType, red, blue);

        if (red.getClass().isArray()) {
            userPrefabs.register(otherType, red, blue, red);
        }
        else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                userPrefabs.register(otherType, red, blue, redCopy);
            }
            catch (InaccessibleObjectException ignored) {
                userPrefabs.register(otherType, red, blue, red);
            }
        }
    }

    public static <T> void addPrefabValuesForField(
            FieldCache fieldCache,
            Objenesis objenesis,
            Class<?> type,
            String fieldName,
            T red,
            T blue) {
        Validations.validateRedAndBluePrefabValues(fieldName, red, blue);
        Validations.validateFieldTypeMatches(type, fieldName, red.getClass());

        if (red.getClass().isArray()) {
            fieldCache.put(fieldName, new Tuple<>(red, blue, red));
        }
        else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                fieldCache.put(fieldName, new Tuple<>(red, blue, redCopy));
            }
            catch (InaccessibleObjectException ignored) {
                fieldCache.put(fieldName, new Tuple<>(red, blue, red));
            }
        }
    }

    public static <T> void addGenericPrefabValues(FactoryCache factoryCache, Class<T> otherType, Func1<?, T> factory) {
        Validations.validateNotNull(factory, "factory is null.");
        addGenericPrefabValueFactory(factoryCache, otherType, simple(factory, null), 1);
    }

    public static <T> void addGenericPrefabValues(
            FactoryCache factoryCache,
            Class<T> otherType,
            Func2<?, ?, T> factory) {
        Validations.validateNotNull(factory, "factory is null.");
        addGenericPrefabValueFactory(factoryCache, otherType, simple(factory, null), 2);
    }

    private static <T> void addGenericPrefabValueFactory(
            FactoryCache factoryCache,
            Class<T> otherType,
            PrefabValueFactory<T> factory,
            int arity) {
        Validations.validateGenericPrefabValues(otherType, factory, arity);
        factoryCache.put(otherType, factory);
    }
}
