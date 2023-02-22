package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Modifier;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;

public final class SealedTypesHelper {

    private SealedTypesHelper() {}

    public static boolean isSealed(Class<?> type) {
        return type.isSealed();
    }

    public static Optional<Class<?>> findInstantiableSubclass(Class<?> type) {
        return findInstantiablePermittedClass(type, false);
    }

    private static Optional<Class<?>> findInstantiablePermittedClass(
        Class<?> type,
        boolean checkCurrent
    ) {
        if (checkCurrent && (!isAbstract(type) || !type.isSealed())) {
            return Optional.of(type);
        }
        var permittedSubclasses = type.getPermittedSubclasses();
        if (permittedSubclasses == null) {
            return Optional.empty();
        }
        for (Class<?> subType : permittedSubclasses) {
            var c = findInstantiablePermittedClass(subType, true);
            if (c.isPresent()) {
                return c;
            }
        }
        throw new EqualsVerifierInternalBugException(
            "Could not find a non-sealed subtype for " + type.getCanonicalName()
        );
    }

    private static boolean isAbstract(Class<?> type) {
        return Modifier.isAbstract(type.getModifiers());
    }
}
