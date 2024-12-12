package nl.jqno.equalsverifier.internal.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InstanceCreatorTest {

    @Test
    public void instantiate() throws NoSuchFieldException {
        ClassProbe<SomeClass> probe = new ClassProbe<>(SomeClass.class);
        Objenesis objenesis = new ObjenesisStd();
        InstanceCreator<SomeClass> sut = new InstanceCreator<>(probe, objenesis);

        Field x = SomeClass.class.getDeclaredField("x");
        Field z = SomeClass.class.getDeclaredField("z");
        Map<Field, Object> values = new HashMap<>();
        values.put(x, 42);
        values.put(z, "42");

        SomeClass actual = sut.instantiate(values);

        assertEquals(42, actual.x);
        assertEquals(0, actual.y);
        assertEquals("42", actual.z);
    }

    @Test
    public void copy() throws NoSuchFieldException {
        ClassProbe<SomeSubClass> probe = new ClassProbe<>(SomeSubClass.class);
        Objenesis objenesis = new ObjenesisStd();
        InstanceCreator<SomeSubClass> sut = new InstanceCreator<>(probe, objenesis);

        SomeClass original = new SomeClass(42, 1337, "yeah");
        SomeSubClass copy = sut.copy(original);

        assertEquals(original.x, copy.x);
        assertEquals(original.y, copy.y);
        assertEquals(original.z, copy.z);
        assertEquals(0, copy.a);
    }

    static class SomeClass {

        final int x;
        final int y;
        final String z;

        public SomeClass(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    static class SomeSubClass extends SomeClass {

        final int a;

        public SomeSubClass(int x, int y, String z, int a) {
            super(x, y, z);
            this.a = a;
        }
    }
}
