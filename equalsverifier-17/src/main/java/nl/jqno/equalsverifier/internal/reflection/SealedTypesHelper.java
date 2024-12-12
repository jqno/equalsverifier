package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Modifier;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;

public final class SealedTypesHelper {

    private SealedTypesHelper() {}

    public static boolean isSealed(Class<?> type) {
        return type.isSealed();
    }

    public static <T, U extends T> Optional<Class<U>> findInstantiableSubclass(Class<T> type) {
        return findInstantiablePermittedClass(type, false);
    }

    private static <T, U extends T> Optional<Class<U>> findInstantiablePermittedClass(
            Class<T> type,
            boolean checkCurrent) {
        if (checkCurrent && (!isAbstract(type) || !type.isSealed())) {
            @SuppressWarnings("unchecked")
            var result = (Class<U>) type;
            return Optional.of(result);
        }
        var permittedSubclasses = type.getPermittedSubclasses();
        if (permittedSubclasses == null) {
            return Optional.empty();
        }
        for (Class<?> permitted : permittedSubclasses) {
            @SuppressWarnings("unchecked")
            Class<U> subType = (Class<U>) permitted;

            var c = findInstantiablePermittedClass(subType, true);
            if (c.isPresent()) {
                return c;
            }
        }
        throw new EqualsVerifierInternalBugException(
                "Could not find a non-sealed subtype for " + type.getCanonicalName());
    }

    private static boolean isAbstract(Class<?> type) {
        return Modifier.isAbstract(type.getModifiers());
    }
}
