package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;

public class InstanceCreatorTest {

    @Test
    public void instanceCreator() throws NoSuchFieldException {
        ClassProbe<SomeClass> probe = new ClassProbe<>(SomeClass.class);
        InstanceCreator<SomeClass> sut = new InstanceCreator<>(probe);

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

    static class SomeClass {

        private final int x;
        private final int y;
        private final String z;

        public SomeClass(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
