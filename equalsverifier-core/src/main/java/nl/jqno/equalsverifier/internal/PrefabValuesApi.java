package nl.jqno.equalsverifier.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.instantiation.InstanceCreator;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueCaches;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.Validations;
import org.objenesis.Objenesis;

public final class PrefabValuesApi {

    private PrefabValuesApi() {}

    public static <T> void addPrefabValues(
            UserPrefabValueCaches prefabs,
            Objenesis objenesis,
            Class<T> otherType,
            T red,
            T blue) {
        Validations.validateRedAndBluePrefabValues(otherType, red, blue);

        if (red.getClass().isArray()) {
            prefabs.register(otherType, red, blue, red);
        }
        else {
            try {
                @SuppressWarnings("unchecked")
                ClassProbe<T> probe = ClassProbe.of((Class<T>) red.getClass());
                T redCopy = InstanceCreator.ofExact(probe, objenesis).copy(red);
                prefabs.register(otherType, red, blue, redCopy);
            }
            catch (InaccessibleObjectException ignored) {
                prefabs.register(otherType, red, blue, red);
            }
        }
    }

    public static <T> void addResettablePrefabValues(
            UserPrefabValueCaches prefabs,
            Objenesis objenesis,
            Class<T> otherType,
            Supplier<T> red,
            Supplier<T> blue) {
        Validations.validateNotNull(red, "red supplier is null");
        Validations.validateNotNull(blue, "blue supplier is null");
        Validations.validateRedAndBluePrefabValues(otherType, red.get(), blue.get());
        Validations.validateEqual(red.get(), red.get(), "red prefab value is not equal to itself after reset.");
        Validations.validateEqual(blue.get(), blue.get(), "blue prefab value is not equal to itself after reset.");

        prefabs.registerResettable(otherType, red, blue, red);
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
        Validations.validateCanProbeKotlinLazyDelegate(type, f);
        TypeTag tag = TypeTag.of(f, new TypeTag(type));

        if (red.getClass().isArray()) {
            fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, red));
        }
        else {
            try {
                @SuppressWarnings("unchecked")
                ClassProbe<T> probe = ClassProbe.of((Class<T>) red.getClass());
                T redCopy = InstanceCreator.ofExact(probe, objenesis).copy(red);
                fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, redCopy));
            }
            catch (InaccessibleObjectException ignored) {
                fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, red));
            }
        }
    }

    public static <T> void addGenericPrefabValues(
            UserPrefabValueCaches prefabs,
            Class<T> otherType,
            Func1<?, T> factory) {
        Validations.validateNotNull(factory, "factory is null.");
        Validations.validateGenericPrefabValues(otherType, 1);
        prefabs.registerGeneric(otherType, factory);
    }

    public static <T> void addGenericPrefabValues(
            UserPrefabValueCaches prefabs,
            Class<T> otherType,
            Func2<?, ?, T> factory) {
        Validations.validateNotNull(factory, "factory is null.");
        Validations.validateGenericPrefabValues(otherType, 2);
        prefabs.registerGeneric(otherType, factory);
    }
}
