package nl.jqno.equalsverifier.testhelpers;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

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
                if (isRelevant(f)) {
                    f.setAccessible(true);
                    Object x = f.get(here);
                    Object y = f.get(there);
                    equals &= Objects.equals(x, y);
                }
            }
        } catch (IllegalAccessException e) {
            fail(e.toString());
        }
        return equals;
    }

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
            fail(e.toString());
        }
        return hash;
    }

    private static boolean isRelevant(Field f) {
        return FieldAccessor.of(f).canBeModifiedReflectively();
    }

    public static <T extends Throwable> T assertThrows(
            Class<T> expectedType, String partialMessage, Executable executable) {
        T result = Assertions.assertThrows(expectedType, executable);
        assertMessageContains(result, partialMessage);
        return result;
    }

    public static void assertMessageContains(Throwable e, String... needles) {
        String haystack = e.getMessage();
        for (String needle : needles) {
            if (!haystack.contains(needle)) {
                fail("Message [" + haystack + "] does not contain [" + needle + "]");
            }
        }
    }

    public static void assertMessageDoesNotContain(Throwable e, String... needles) {
        String haystack = e.getMessage();
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                fail("Message [" + haystack + "] contains [" + needle + "]");
            }
        }
    }

    public static void coverThePrivateConstructor(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            fail("Could not call constructor of " + type.getName());
        }
    }
}
