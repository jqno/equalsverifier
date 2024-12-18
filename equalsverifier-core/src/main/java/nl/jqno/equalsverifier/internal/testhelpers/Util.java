package nl.jqno.equalsverifier.internal.testhelpers;

import java.lang.reflect.Constructor;
import java.util.Objects;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

public final class Util {

    private Util() {}

    @SuppressFBWarnings(
            value = "DP_DO_INSIDE_DO_PRIVILEGED",
            justification = "Only called in test code, not production.")
    public static boolean defaultEquals(Object here, Object there) {
        Class<?> type = here.getClass();
        if (there == null || !there.getClass().isAssignableFrom(type)) {
            return false;
        }
        boolean equals = true;
        try {
            for (FieldProbe p : FieldIterable.of(type)) {
                if (isRelevant(p)) {
                    Object x = p.getValue(here);
                    Object y = p.getValue(there);
                    equals &= Objects.equals(x, y);
                }
            }
        }
        catch (ReflectionException e) {
            throw new AssertionError(e.toString(), e);
        }
        return equals;
    }

    @SuppressFBWarnings(
            value = "DP_DO_INSIDE_DO_PRIVILEGED",
            justification = "Only called in test code, not production.")
    public static int defaultHashCode(Object x) {
        int hash = 59;
        try {
            for (FieldProbe p : FieldIterable.of(x.getClass())) {
                if (isRelevant(p)) {
                    Object val = p.getValue(x);
                    hash += 59 * Objects.hashCode(val);
                }
            }
        }
        catch (ReflectionException e) {
            throw new AssertionError(e.toString(), e);
        }
        return hash;
    }

    private static boolean isRelevant(FieldProbe p) {
        return p.canBeModifiedReflectively();
    }

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
