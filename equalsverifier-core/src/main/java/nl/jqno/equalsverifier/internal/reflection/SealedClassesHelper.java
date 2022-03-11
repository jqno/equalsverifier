package nl.jqno.equalsverifier.internal.reflection;

/**
 * Note: this is a generic implementation for a multi-release jar class.
 * See equalsverifier-17 submodule.
 */
public final class SealedClassesHelper {

    private SealedClassesHelper() {}

    public static boolean isSealed(Class<?> type) {
        return false;
    }
}
