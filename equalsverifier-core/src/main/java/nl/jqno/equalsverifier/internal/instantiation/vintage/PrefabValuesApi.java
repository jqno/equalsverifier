package nl.jqno.equalsverifier.internal.instantiation.vintage;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.simple;
import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.values;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.util.Validations;
import org.objenesis.Objenesis;

public final class PrefabValuesApi {

    private PrefabValuesApi() {}

    public static <T> void addPrefabValues(
            FactoryCache factoryCache,
            Objenesis objenesis,
            Class<T> otherType,
            T red,
            T blue) {
        Validations.validateRedAndBluePrefabValues(otherType, red, blue);

        if (red.getClass().isArray()) {
            factoryCache.put(otherType, values(red, blue, red));
        }
        else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                factoryCache.put(otherType, values(red, blue, redCopy));
            }
            catch (RuntimeException ignored) {
                /* specifically, on Java 9+: InacessibleObjectException */
                factoryCache.put(otherType, values(red, blue, red));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void addPrefabValuesForField(
            FieldCache fieldCache,
            Objenesis objenesis,
            Class<?> type,
            String fieldName,
            T red,
            T blue) {
        Validations.validateRedAndBluePrefabValues((Class<T>) red.getClass(), red, blue);
        Validations.validateFieldTypeMatches(type, fieldName, red.getClass());

        if (red.getClass().isArray()) {
            fieldCache.put(fieldName, Tuple.of(red, blue, red));
        }
        else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                fieldCache.put(fieldName, Tuple.of(red, blue, redCopy));
            }
            catch (RuntimeException ignored) {
                /* specifically, on Java 9+: InacessibleObjectException */
                fieldCache.put(fieldName, Tuple.of(red, blue, red));
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
