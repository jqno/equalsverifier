package nl.jqno.equalsverifier.internal.versionspecific;

import java.lang.reflect.AccessFlag;

public final class ValueClassHelper {
    private ValueClassHelper() {}

    public static boolean isValueClass(Class<?> type) {
        return type.isValue();
    }
}
