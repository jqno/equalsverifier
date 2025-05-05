package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.GregorianCalendar;

import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(Util.class);
    }

    @Test
    void forNameReturnsClass_whenTypeExists() {
        Class<?> actual = Util.classForName("java.util.GregorianCalendar");
        assertThat(actual).isEqualTo(GregorianCalendar.class);
    }

    @Test
    void forNameReturnsNull_whenTypeDoesntExist() {
        Class<?> actual = Util.classForName("this.type.does.not.exist");
        assertThat(actual).isNull();
    }

    @Test
    void forNameWithClassLoaderReturnsClass_whenTypeExists() {
        ClassLoader cl = getClass().getClassLoader();
        Class<?> actual = Util.classForName(cl, "java.util.GregorianCalendar");
        assertThat(actual).isEqualTo(GregorianCalendar.class);
    }

    @Test
    void forNameWithClassLoaderReturnsNull_whenTypeDoesntExist() {
        ClassLoader cl = getClass().getClassLoader();
        Class<?> actual = Util.classForName(cl, "this.type.does.not.exist");
        assertThat(actual).isNull();
    }

    @Test
    void classesReturnsItsArguments() {
        Class<?>[] expected = new Class<?>[] { String.class, Object.class };
        Class<?>[] actual = Util.classes(String.class, Object.class);
        assertThat(actual).containsExactly(expected);
    }

    @Test
    void objectsReturnsItsArguments() {
        Object[] expected = new Object[] { "x", new Point(1, 2) };
        Object[] actual = Util.objects("x", new Point(1, 2));
        assertThat(actual).containsExactly(expected);
    }
}
