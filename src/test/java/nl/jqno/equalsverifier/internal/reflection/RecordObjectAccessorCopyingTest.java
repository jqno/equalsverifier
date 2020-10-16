package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
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
        Object original = instantiate(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        Object copy = copyOf(original);

        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    public void shallowCopy() {
        Object original = instantiate(RECORD_CONTAINER_CLASS_NAME, RECORD_CONTAINER_CLASS);
        Object copy = copyOf(original);
        ObjectAccessor<?> originalAccessor = create(original);
        ObjectAccessor<?> copyAccessor = create(copy);

        assertNotSame(original, copy);
        for (Field f : FieldIterable.of(original.getClass())) {
            Object a = originalAccessor.getField(f);
            Object b = copyAccessor.getField(f);
            assertSame(a, b);
        }
    }

    @Test
    public void copyIntoSubclass() {
        expectException(
                EqualsVerifierInternalBugException.class,
                "Can't copy a record into a subclass of itself.");
        Object object = instantiate(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        create(object).copyIntoSubclass(null);
    }

    @Test
    public void copyIntoAnonymousSubclass() {
        expectException(
                EqualsVerifierInternalBugException.class,
                "Can't copy a record into an anonymous subclass of itself.");
        Object object = instantiate(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        create(object).copyIntoAnonymousSubclass();
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(String className, String code) {
        Class<T> type = (Class<T>) compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        return Instantiator.of(type).instantiate();
    }

    private <T> T copyOf(T from) {
        return create(from).copy();
    }

    private static final String SIMPLE_RECORD_CLASS_NAME = "SimpleRecord";
    private static final String SIMPLE_RECORD_CLASS =
            "public record SimpleRecord(int i, String s) {}";

    private static final String RECORD_CONTAINER_CLASS_NAME = "RecordContainer";
    private static final String RECORD_CONTAINER_CLASS =
            "record SimpleRecord(int i) {}" + "\npublic record RecordContainer(SimpleRecord r) {}";
}
