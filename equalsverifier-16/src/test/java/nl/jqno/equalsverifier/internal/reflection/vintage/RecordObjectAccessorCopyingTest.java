package nl.jqno.equalsverifier.internal.reflection.vintage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import org.junit.jupiter.api.Test;

public class RecordObjectAccessorCopyingTest {

    @Test
    public void copyHappyPath() {
        Object original = instantiate(SimpleRecord.class);
        Object copy = copyOf(original);

        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    public void shallowCopy() {
        Object original = instantiate(RecordContainer.class);
        Object copy = copyOf(original);
        RecordObjectAccessor<?> originalAccessor = create(original);
        RecordObjectAccessor<?> copyAccessor = create(copy);

        assertNotSame(original, copy);
        for (Field f : FieldIterable.of(original.getClass())) {
            Object a = originalAccessor.getField(f);
            Object b = copyAccessor.getField(f);
            assertSame(a, b);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private <T> T instantiate(Class<T> type) {
        return Instantiator.of(type).instantiate();
    }

    private <T> T copyOf(T from) {
        return create(from).copy();
    }

    record SimpleRecord(int i, String s) {}

    record RecordContainer(SimpleRecord r) {}
}
