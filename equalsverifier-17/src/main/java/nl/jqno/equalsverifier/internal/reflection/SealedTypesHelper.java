package nl.jqno.equalsverifier.internal.reflection;

public final class SealedTypesHelper {

    private SealedTypesHelper() {}

    public static boolean isSealed(Class<?> type) {
        return type.isSealed();
    }
}
