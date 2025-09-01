package nl.jqno.equalsverifier.internal;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.simple;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
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

    public static <T> void addResettablePrefabValues(
            UserPrefabValueProvider userPrefabs,
            Objenesis objenesis,
            Class<T> otherType,
            Supplier<T> red,
            Supplier<T> blue) {
        Validations.validateNotNull(red, "red supplier is null");
        Validations.validateNotNull(blue, "blue supplier is null");
        Validations.validateRedAndBluePrefabValues(otherType, red.get(), blue.get());
        Validations.validateEqual(red.get(), red.get(), "red prefab value is not equal to itself after reset.");
        Validations.validateEqual(blue.get(), blue.get(), "blue prefab value is not equal to itself after reset.");

        userPrefabs.registerResettable(otherType, red, blue, red);
    }

    public static <T> void addPrefabValuesForField(
            FieldCache fieldCache,
            Objenesis objenesis,
            Class<?> type,
            String fieldName,
            T red,
            T blue) {
        Validations.validateRedAndBluePrefabValues(fieldName, red, blue);
        Field f = Validations.validateFieldTypeMatches(type, fieldName, red.getClass());
        TypeTag tag = TypeTag.of(f, new TypeTag(type));

        if (red.getClass().isArray()) {
            fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, red));
        }
        else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, redCopy));
            }
            catch (InaccessibleObjectException ignored) {
                fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, red));
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
