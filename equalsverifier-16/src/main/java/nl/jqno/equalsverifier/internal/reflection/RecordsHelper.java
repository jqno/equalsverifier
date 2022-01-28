package nl.jqno.equalsverifier.internal.reflection;

public final class RecordsHelper {

    private RecordsHelper() {}

    public static boolean isRecord(Class<?> type) {
        return type.isRecord();
    }
}
