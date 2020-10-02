package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Before;
import org.junit.Test;

public class RecordObjectAccessorCopyingTest extends StringCompilerTestBase {

    @Before
    public void setUp() {
        assumeTrue(isRecordsAvailable());
    }

    @Test
    public void copyHappyPath() {
        Class<?> type = compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        Object original = Instantiator.of(type).instantiate();
        Object copy = copyOf(original);

        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    public void shallowCopy() {
        Class<?> type = compile(RECORD_CONTAINER_CLASS_NAME, RECORD_CONTAINER_CLASS);
        Object original = Instantiator.of(type).instantiate();
        Object copy = copyOf(original);
        ObjectAccessor<?> originalAccessor = create(original);
        ObjectAccessor<?> copyAccessor = create(copy);

        assertNotSame(original, copy);
        for (Field f : FieldIterable.of(type)) {
            Object a = originalAccessor.fieldAccessorFor(f).get();
            Object b = copyAccessor.fieldAccessorFor(f).get();
            assertSame(a, b);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private <T> T copyOf(T from) {
        return create(from).copy();
    }

    private static final String SIMPLE_RECORD_CLASS_NAME = "SimpleRecord";
    private static final String SIMPLE_RECORD_CLASS =
            "public record SimpleRecord(int i, String s) {}";

    private static final String RECORD_CONTAINER_CLASS_NAME = "RecordContainer";
    private static final String RECORD_CONTAINER_CLASS =
            "record SimpleRecord(int i) {}"
                    + "\npublic record RecordContainer(SimpleRecord r) {}";
}
