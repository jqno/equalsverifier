package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.VintageValueProvider;

/**
 * Abstract implementation of {@link PrefabValueFactory} that provides helper functions for dealing
 * with generics.
 */
public abstract class AbstractGenericFactory<T> implements PrefabValueFactory<T> {

    public static final TypeTag OBJECT_TYPE_TAG = new TypeTag(Object.class);

    protected TypeTag copyGenericTypesInto(Class<?> type, TypeTag source) {
        List<TypeTag> genericTypes = new ArrayList<>();
        for (TypeTag tag : source.getGenericTypes()) {
            genericTypes.add(tag);
        }
        return new TypeTag(type, genericTypes.toArray(new TypeTag[genericTypes.size()]));
    }

    protected TypeTag determineAndCacheActualTypeTag(
        int n,
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        return determineAndCacheActualTypeTag(n, tag, valueProvider, attributes, null);
    }

    protected TypeTag determineAndCacheActualTypeTag(
        int n,
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes,
        Class<?> bottomType
    ) {
        TypeTag result = determineActualTypeTagFor(n, tag);
        if (bottomType != null && result.getType().equals(Object.class)) {
            result = new TypeTag(bottomType);
        }
        return result;
    }

    protected TypeTag determineActualTypeTagFor(int n, TypeTag typeTag) {
        List<TypeTag> genericTypes = typeTag.getGenericTypes();
        if (genericTypes.size() <= n) {
            return OBJECT_TYPE_TAG;
        }

        return genericTypes.get(n);
    }

    @SuppressFBWarnings(
        value = "DP_DO_INSIDE_DO_PRIVILEGED",
        justification = "EV is run only from within unit tests"
    )
    protected void invoke(
        Class<?> type,
        Object receiver,
        String methodName,
        Class<?>[] classes,
        Object[] values
    ) {
        try {
            Method method = type.getMethod(methodName, classes);
            // Not necessary in the common case, but required for
            // https://bugs.java.com/view_bug.do?bug_id=6924232.
            method.setAccessible(true);
            method.invoke(receiver, values);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }
}
