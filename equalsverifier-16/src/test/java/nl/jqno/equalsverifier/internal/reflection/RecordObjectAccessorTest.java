package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecordObjectAccessorTest {

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
        RecordObjectAccessor<Object> accessor = accessorFor(recordInstance);
        assertSame(recordInstance, accessor.get());
    }

    @Test
    public void getField() throws Exception {
        Field f = SimpleRecord.class.getDeclaredField("i");
        RecordObjectAccessor<?> accessor = accessorFor(recordInstance);
        assertEquals(42, accessor.getField(f));
    }

    @Test
    public void fail_whenConstructorThrowsNpe() {
        Object instance = Instantiator.of(NpeThrowingConstructorRecord.class).instantiate();

        ExpectedException
            .when(() -> accessorFor(instance).copy())
            .assertThrows(ReflectionException.class)
            .assertMessageContains("Record:", "failed to run constructor", "Warning.NULL_FIELDS");
    }

    @Test
    public void fail_whenConstructorThrowsOnZero() {
        Object instance = Instantiator.of(ZeroThrowingConstructorRecord.class).instantiate();

        ExpectedException
            .when(() -> accessorFor(instance).copy())
            .assertThrows(ReflectionException.class)
            .assertMessageContains("Record:", "failed to run constructor", "Warning.ZERO_FIELDS");
    }

    @Test
    public void fail_whenConstructorThrowsOnSomethingElse() {
        Object instance = Instantiator.of(OtherThrowingConstructorRecord.class).instantiate();

        PrefabValues pv = new PrefabValues(JavaApiPrefabValues.build());
        ExpectedException
            .when(() -> accessorFor(instance).scramble(pv, TypeTag.NULL))
            .assertThrows(ReflectionException.class)
            .assertMessageContains("Record:", "failed to run constructor", "prefab values");
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> accessorFor(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    record SimpleRecord(int i, String s) {}

    record NpeThrowingConstructorRecord(String s) {
        public NpeThrowingConstructorRecord {
            Objects.requireNonNull(s);
        }
    }

    record ZeroThrowingConstructorRecord(int i) {
        public ZeroThrowingConstructorRecord {
            if (i == 0) {
                throw new IllegalStateException("i == 0");
            }
        }
    }

    record OtherThrowingConstructorRecord(int i) {
        public OtherThrowingConstructorRecord {
            if (i > 0) {
                throw new IllegalStateException("i > 0");
            }
        }
    }
}
