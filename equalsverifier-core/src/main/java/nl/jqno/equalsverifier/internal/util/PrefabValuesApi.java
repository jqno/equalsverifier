package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.reflection.SuperclassIterable;
import nl.jqno.equalsverifier.internal.reflection.instantiation.GenericPrefabValueProvider.GenericFactories;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.vintage.ObjectAccessor;
import org.objenesis.Objenesis;

public final class PrefabValuesApi {

    private PrefabValuesApi() {}

    public static <T> void addPrefabValues(
        FactoryCache factoryCache,
        Objenesis objenesis,
        Class<T> otherType,
        T red,
        T blue
    ) {
        Validations.validateRedAndBluePrefabValues(otherType, red, blue);

        if (red.getClass().isArray()) {
            factoryCache.put(otherType, values(red, blue, red));
        } else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                factoryCache.put(otherType, values(red, blue, redCopy));
            } catch (RuntimeException ignored) {
                /* specifically, on Java 9+: InacessibleObjectException */
                factoryCache.put(otherType, values(red, blue, red));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void addPrefabValuesForField(
        FactoryCache factoryCache,
        Objenesis objenesis,
        Class<?> enclosingType,
        String fieldName,
        T red,
        T blue
    ) {
        Field field = findField(enclosingType, fieldName);
        Class<T> type = (Class<T>) field.getType();

        Validations.validateRedAndBluePrefabValues(type, red, blue);
        Validations.validateFieldTypeMatches(field, red.getClass());

        if (type.isArray()) {
            factoryCache.put(type, fieldName, values(red, blue, red));
        } else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                factoryCache.put(type, fieldName, values(red, blue, redCopy));
            } catch (RuntimeException ignored) {
                /* specifically, on Java 9+: InacessibleObjectException */
                factoryCache.put(type, fieldName, values(red, blue, red));
            }
        }
    }

    private static Field findField(Class<?> type, String fieldName) {
        for (Class<?> c : SuperclassIterable.ofIncludeSelf(type)) {
            try {
                return c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                // Do nothing
            }
        }
        throw new IllegalStateException(
            "Precondition: class " +
            type.getSimpleName() +
            " does not contain field " +
            fieldName +
            "."
        );
    }

    public static <T> void addGenericPrefabValues(
        GenericFactories factories,
        Class<T> otherType,
        Func1<?, T> factory
    ) {
        Validations.validateNotNull(factory, "factory is null.");
        Validations.validateGenericPrefabValues(otherType, 1);
        factories.register(otherType, factory);
    }

    public static <T> void addGenericPrefabValues(
        GenericFactories factories,
        Class<T> otherType,
        Func2<?, ?, T> factory
    ) {
        Validations.validateNotNull(factory, "factory is null.");
        Validations.validateGenericPrefabValues(otherType, 2);
        factories.register(otherType, factory);
    }
}
