package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
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
        Object object = instantiate(SimpleRecord.class);
        ExpectedException
            .when(() -> create(object).copyIntoSubclass(null))
            .assertThrows(EqualsVerifierInternalBugException.class)
            .assertMessageContains("Can't copy a record into a subclass of itself.");
    }

    @Test
    public void copyIntoAnonymousSubclass() {
        Object object = instantiate(SimpleRecord.class);
        ExpectedException
            .when(() -> create(object).copyIntoAnonymousSubclass())
            .assertThrows(EqualsVerifierInternalBugException.class)
            .assertMessageContains("Can't copy a record into an anonymous subclass of itself.");
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(Class<T> type) {
        return Instantiator.of(type).instantiate();
    }

    private <T> T copyOf(T from) {
        return create(from).copy();
    }

    record SimpleRecord(int i, String s) {}

    record RecordContainer(SimpleRecord r) {}
}
