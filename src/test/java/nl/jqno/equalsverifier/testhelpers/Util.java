package nl.jqno.equalsverifier.testhelpers;

import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

import static org.junit.Assert.fail;

public final class Util {
    private Util() {}

    public static boolean defaultEquals(Object here, Object there) {
        Class<?> type = here.getClass();
        if (there == null || !there.getClass().isAssignableFrom(type)) {
            return false;
        }
        boolean equals = true;
        try {
            for (Field f : FieldIterable.of(type)) {
                if (isRelevant(here, f)) {
                    f.setAccessible(true);
                    Object x = f.get(here);
                    Object y = f.get(there);
                    equals &= Objects.equals(x, y);
                }
            }
        }
        catch (IllegalAccessException e) {
            fail(e.toString());
        }
        return equals;
    }

    public static int defaultHashCode(Object x) {
        int hash = 59;
        try {
            for (Field f : FieldIterable.of(x.getClass())) {
                if (isRelevant(x, f)) {
                    f.setAccessible(true);
                    Object val = f.get(x);
                    hash += 59 * Objects.hashCode(val);
                }
            }
        }
        catch (IllegalAccessException e) {
            fail(e.toString());
        }
        return hash;
    }

    private static boolean isRelevant(Object x, Field f) {
        FieldAccessor acc = new FieldAccessor(x, f);
        return acc.canBeModifiedReflectively();
    }

    public static void coverThePrivateConstructor(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }
        catch (Exception e) {
            fail("Could not call constructor of " + type.getName());
        }
    }
}
