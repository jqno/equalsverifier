package nl.jqno.equalsverifier.internal.reflection;

import java.util.Optional;

/**
 * Note: this is a generic implementation for a multi-release jar class.
 * See equalsverifier-17 submodule.
 */
public final class SealedTypesHelper {

    private SealedTypesHelper() {}

    public static boolean isSealed(Class<?> type) {
        return false;
    }

    public static <T, U extends T> Optional<Class<U>> findInstantiableSubclass(Class<T> type) {
        return Optional.empty();
    }
}
