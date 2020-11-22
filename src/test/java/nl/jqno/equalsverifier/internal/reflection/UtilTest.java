package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.Util.setOf;
import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;
import static org.junit.jupiter.api.Assertions.*;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;

public class UtilTest {
    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Util.class);
    }

    @Test
    public void forNameReturnsClass_whenTypeExists() {
        Class<?> actual = Util.classForName("java.util.GregorianCalendar");
        assertEquals(actual, GregorianCalendar.class);
    }

    @Test
    public void forNameReturnsNull_whenTypeDoesntExist() {
        Class<?> actual = Util.classForName("this.type.does.not.exist");
        assertNull(actual);
    }

    @Test
    public void classesReturnsItsArguments() {
        Class<?>[] expected = new Class<?>[] {String.class, Object.class};
        Class<?>[] actual = Util.classes(String.class, Object.class);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void objectsReturnsItsArguments() {
        Object[] expected = new Object[] {"x", new Point(1, 2)};
        Object[] actual = Util.objects("x", new Point(1, 2));
        assertArrayEquals(expected, actual);
    }

    @Test
    public void setOfReturnsItsArguments() {
        Set<String> expected = new HashSet<>();
        expected.add("one");
        expected.add("two");

        Set<String> actual = setOf("one", "two");
        assertEquals(expected, actual);
    }
}
