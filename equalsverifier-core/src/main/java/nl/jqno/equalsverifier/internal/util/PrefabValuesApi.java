package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.reflection.instantiation.GenericPrefabValueProvider.GenericFactories;
import nl.jqno.equalsverifier.internal.reflection.instantiation.PrefabValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.ObjectAccessor;
import org.objenesis.Objenesis;

public final class PrefabValuesApi {

    private PrefabValuesApi() {}

    public static <T> void addPrefabValues(
        PrefabValueProvider provider,
        Objenesis objenesis,
        Class<T> otherType,
        T red,
        T blue
    ) {
        Validations.validateRedAndBluePrefabValues(otherType, red, blue);

        if (red.getClass().isArray()) {
            provider.register(otherType, null, red, blue, red);
        } else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                provider.register(otherType, null, red, blue, redCopy);
            } catch (RuntimeException ignored) {
                /* specifically, on Java 9+: InacessibleObjectException */
                provider.register(otherType, null, red, blue, red);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void addPrefabValuesForField(
        PrefabValueProvider provider,
        Objenesis objenesis,
        Class<?> enclosingType,
        String fieldName,
        T red,
        T blue
    ) {
        Class<T> type = (Class<T>) red.getClass();

        Validations.validateRedAndBluePrefabValues(type, red, blue);
        Validations.validateFieldTypeMatches(enclosingType, fieldName, red.getClass());

        if (type.isArray()) {
            provider.register(type, fieldName, red, blue, red);
        } else {
            try {
                T redCopy = ObjectAccessor.of(red).copy(objenesis);
                provider.register(type, fieldName, red, blue, redCopy);
            } catch (RuntimeException ignored) {
                /* specifically, on Java 9+: InacessibleObjectException */
                provider.register(type, fieldName, red, blue, red);
            }
        }
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
