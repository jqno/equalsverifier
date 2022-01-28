package nl.jqno.equalsverifier.internal.reflection;

public final class SealedClassesHelper {

    private SealedClassesHelper() {}

    public static boolean isSealed(Class<?> type) {
        return false;
    }
}
