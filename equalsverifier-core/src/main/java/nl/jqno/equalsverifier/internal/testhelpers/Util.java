package nl.jqno.equalsverifier.internal.testhelpers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;

public final class Util {

    private Util() {}

    @SuppressFBWarnings(
        value = "DP_DO_INSIDE_DO_PRIVILEGED",
        justification = "Only called in test code, not production."
    )
    public static boolean defaultEquals(Object here, Object there) {
        Class<?> type = here.getClass();
        if (there == null || !there.getClass().isAssignableFrom(type)) {
            return false;
        }
        boolean equals = true;
        try {
            for (Field f : FieldIterable.of(type)) {
                if (isRelevant(f)) {
                    f.setAccessible(true);
                    Object x = f.get(here);
                    Object y = f.get(there);
                    equals &= Objects.equals(x, y);
                }
            }
        } catch (IllegalAccessException e) {
            throw new AssertionError(e.toString(), e);
        }
        return equals;
    }

    @SuppressFBWarnings(
        value = "DP_DO_INSIDE_DO_PRIVILEGED",
        justification = "Only called in test code, not production."
    )
    public static int defaultHashCode(Object x) {
        int hash = 59;
        try {
            for (Field f : FieldIterable.of(x.getClass())) {
                if (isRelevant(f)) {
                    f.setAccessible(true);
                    Object val = f.get(x);
                    hash += 59 * Objects.hashCode(val);
                }
            }
        } catch (IllegalAccessException e) {
            throw new AssertionError(e.toString(), e);
        }
        return hash;
    }

    private static boolean isRelevant(Field f) {
        return FieldAccessor.of(f).canBeModifiedReflectively();
    }

    public static void coverThePrivateConstructor(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            throw new AssertionError("Could not call constructor of " + type.getName(), e);
        }
    }
}
