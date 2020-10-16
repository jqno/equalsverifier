package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Before;
import org.junit.Test;

public class RecordObjectAccessorTest extends StringCompilerTestBase {

    private Class<?> recordClass;
    private Object recordInstance;

    @Before
    public void setUp() throws Exception {
        assumeTrue(isRecordsAvailable());
        recordClass = compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
        Constructor<?> constructor = recordClass.getDeclaredConstructor(int.class, String.class);
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
        Field f = recordClass.getDeclaredField("i");
        RecordObjectAccessor<?> accessor = create(recordInstance);
        assertEquals(42, accessor.getField(f));
    }

    @Test
    public void fail_whenConstructorThrowsNpe() {
        recordClass =
                compile(THROWING_CONSTRUCTOR_RECORD_CLASS_NAME, THROWING_CONSTRUCTOR_RECORD_CLASS);
        Object instance = Instantiator.of(recordClass).instantiate();

        expectException(ReflectionException.class, "Record:", "failed to invoke constructor");
        create(instance).copy();
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private static final String SIMPLE_RECORD_CLASS_NAME = "SimpleRecord";
    private static final String SIMPLE_RECORD_CLASS =
            "public record SimpleRecord(int i, String s) {}";

    private static final String THROWING_CONSTRUCTOR_RECORD_CLASS_NAME =
            "ThrowingConstructorRecord";
    private static final String THROWING_CONSTRUCTOR_RECORD_CLASS =
            "public record ThrowingConstructorRecord(int i, String s) {"
                    + "\n    public ThrowingConstructorRecord {"
                    + "\n        throw new IllegalStateException();"
                    + "\n    }"
                    + "\n}";
}
