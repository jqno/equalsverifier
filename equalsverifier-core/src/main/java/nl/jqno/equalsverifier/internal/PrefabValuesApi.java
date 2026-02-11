package nl.jqno.equalsverifier.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.Func.Func2;
import nl.jqno.equalsverifier.internal.exceptions.InstantiatorException;
import nl.jqno.equalsverifier.internal.instantiators.InstantiatorFactory;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.util.Validations;
import nl.jqno.equalsverifier.internal.valueproviders.UserPrefabValueCaches;
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
                T redCopy = InstantiatorFactory.of(probe, objenesis, false).copy(red);
                prefabs.register(otherType, red, blue, redCopy);
            }
            catch (InaccessibleObjectException ignored) {
                prefabs.register(otherType, red, blue, red);
            }
            catch (InstantiatorException ignored) {
                var msg = Formatter
                        .of("""
                            Cannot copy prefab value %%.
                            Use the overload of #withPrefabValues() to provide an explicit red copy.""", red)
                        .format();
                throw new IllegalStateException(msg);
            }
        }
    }

    public static <T> void addPrefabValues(
            UserPrefabValueCaches prefabs,
            Class<T> otherType,
            T red,
            T blue,
            T redCopy) {
        Validations.validateRedAndBluePrefabValues(otherType, red, blue);
        Validations.validateRedAndRedCopyPrefabValues(otherType, red, redCopy);
        prefabs.register(otherType, red, blue, redCopy);
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
                T redCopy = InstantiatorFactory.of(probe, objenesis, false).copy(red);
                fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, redCopy));
            }
            catch (InaccessibleObjectException ignored) {
                fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, red));
            }
            catch (InstantiatorException ignored) {
                var msg = Formatter
                        .of("""
                            Cannot copy prefab value %%.
                            Use the overload of #withPrefabValuesForField() to provide an explicit red copy.""", red)
                        .format();
                throw new IllegalStateException(msg);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void addPrefabValuesForField(
            FieldCache fieldCache,
            Class<?> type,
            String fieldName,
            T red,
            T blue,
            T redCopy) {
        Validations.validateRedAndBluePrefabValues(fieldName, red, blue);
        Field f = Validations.validateFieldTypeMatches(type, fieldName, red.getClass());
        Validations.validateRedAndRedCopyPrefabValues((Class<T>) f.getType(), red, redCopy);
        Validations.validateCanProbeKotlinLazyDelegate(type, f);
        TypeTag tag = TypeTag.of(f, new TypeTag(type));

        fieldCache.put(f.getName(), tag, new Tuple<>(red, blue, redCopy));
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
