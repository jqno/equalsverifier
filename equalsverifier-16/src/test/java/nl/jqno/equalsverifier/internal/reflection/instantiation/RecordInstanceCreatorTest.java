package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;

public class RecordInstanceCreatorTest {

    @Test
    public void instanceCreator() throws NoSuchFieldException {
        ClassProbe<SomeRecord> probe = new ClassProbe<>(SomeRecord.class);
        InstanceCreator<SomeRecord> sut = new InstanceCreator<>(probe);

        Field x = SomeRecord.class.getDeclaredField("x");
        Field z = SomeRecord.class.getDeclaredField("z");
        Map<Field, Object> values = new HashMap<>();
        values.put(x, 42);
        values.put(z, "42");

        SomeRecord actual = sut.instantiate(values);

        assertEquals(42, actual.x);
        assertEquals(0, actual.y);
        assertEquals("42", actual.z);
    }

    record SomeRecord(int x, int y, String z) {}
}
