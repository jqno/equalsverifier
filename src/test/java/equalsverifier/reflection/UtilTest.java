package equalsverifier.reflection;

import equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import java.util.GregorianCalendar;

import static equalsverifier.testhelpers.Util.coverThePrivateConstructor;
import static org.junit.Assert.*;

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
        Class<?>[] expected = new Class<?>[] { String.class, Object.class };
        Class<?>[] actual = Util.classes(String.class, Object.class);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void objectsReturnsItsArguments() {
        Object[] expected = new Object[] { "x", new Point(1, 2) };
        Object[] actual = Util.objects("x", new Point(1, 2));
        assertArrayEquals(expected, actual);
    }
}
