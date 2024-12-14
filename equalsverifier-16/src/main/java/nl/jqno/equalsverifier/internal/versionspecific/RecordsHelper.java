package nl.jqno.equalsverifier.internal.versionspecific;

public final class RecordsHelper {

    private RecordsHelper() {}

    public static boolean isRecord(Class<?> type) {
        return type.isRecord();
    }
}
