package equalsverifier.prefabvalues.factories;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.reflection.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Abstract implementation of {@link PrefabValueFactory} that provides helper
 * functions for dealing with generics.
 */
public abstract class AbstractGenericFactory<T> implements PrefabValueFactory<T> {
    public static final TypeTag OBJECT_TYPE_TAG = new TypeTag(Object.class);

    protected LinkedHashSet<TypeTag> cloneWith(LinkedHashSet<TypeTag> typeStack, TypeTag tag) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>)typeStack.clone();
        clone.add(tag);
        return clone;
    }

    protected TypeTag copyGenericTypesInto(Class<?> type, TypeTag source) {
        List<TypeTag> genericTypes = new ArrayList<>();
        for (TypeTag tag : source.getGenericTypes()) {
            genericTypes.add(tag);
        }
        return new TypeTag(type, genericTypes.toArray(new TypeTag[genericTypes.size()]));
    }

    protected TypeTag determineAndCacheActualTypeTag(int n, TypeTag tag, PrefabAbstract prefabAbstract,
            LinkedHashSet<TypeTag> typeStack) {
        return determineAndCacheActualTypeTag(n, tag, prefabAbstract, typeStack, null);
    }

    protected TypeTag determineAndCacheActualTypeTag(int n, TypeTag tag, PrefabAbstract prefabAbstract,
            LinkedHashSet<TypeTag> typeStack, Class<?> bottomType) {
        TypeTag result = determineActualTypeTagFor(n, tag);
        if (bottomType != null && result.getType().equals(Object.class)) {
            result = new TypeTag(bottomType);
        }
        prefabAbstract.realizeCacheFor(result, typeStack);
        return result;
    }

    protected TypeTag determineActualTypeTagFor(int n, TypeTag typeTag) {
        List<TypeTag> genericTypes = typeTag.getGenericTypes();
        if (genericTypes.size() <= n) {
            return OBJECT_TYPE_TAG;
        }

        return genericTypes.get(n);
    }

    @SuppressFBWarnings(value = "DP_DO_INSIDE_DO_PRIVILEGED", justification = "EV is run only from within unit tests")
    protected void invoke(Class<?> type, Object receiver, String methodName, Class<?>[] classes, Object[] values) {
        try {
            Method method = type.getMethod(methodName, classes);
            // Not necessary in the common case, but required for https://bugs.java.com/view_bug.do?bug_id=6924232.
            method.setAccessible(true);
            method.invoke(receiver, values);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }
}
