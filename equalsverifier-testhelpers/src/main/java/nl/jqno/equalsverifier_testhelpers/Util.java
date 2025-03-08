package nl.jqno.equalsverifier_testhelpers;

import java.lang.reflect.Constructor;

public final class Util {

    private Util() {}

    public static void coverThePrivateConstructor(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }
        catch (Exception e) {
            throw new AssertionError("Could not call constructor of " + type.getName(), e);
        }
    }
}
