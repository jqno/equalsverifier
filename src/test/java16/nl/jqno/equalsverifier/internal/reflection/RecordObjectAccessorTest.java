package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class RecordObjectAccessorTest extends ExpectedExceptionTestBase {

    private Object recordInstance;

    @BeforeEach
    public void setUp() throws Exception {
        Constructor<?> constructor =
                SimpleRecord.class.getDeclaredConstructor(int.class, String.class);
        constructor.setAccessible(true);
        recordInstance = constructor.newInstance(42, "hello");
    }

    @Test
    public void of() {
        ObjectAccessor<?> actual = ObjectAccessor.of(recordInstance);
        assertTrue(actual instanceof RecordObjectAccessor);
    }

    @Test
    public void get() {
        RecordObjectAccessor<Object> accessor = create(recordInstance);
        assertSame(recordInstance, accessor.get());
    }

    @Test
    public void getField() throws Exception {
        Field f = SimpleRecord.class.getDeclaredField("i");
        RecordObjectAccessor<?> accessor = create(recordInstance);
        assertEquals(42, accessor.getField(f));
    }

    @Test
    public void fail_whenConstructorThrowsNpe() {
        Object instance = Instantiator.of(ThrowingConstructorRecord.class).instantiate();

        expectException(ReflectionException.class, "Record:", "failed to invoke constructor");
        create(instance).copy();
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    record SimpleRecord(int i, String s) {}

    record ThrowingConstructorRecord(int i, String s) {
        public ThrowingConstructorRecord {
            throw new IllegalStateException();
        }
    }
}
