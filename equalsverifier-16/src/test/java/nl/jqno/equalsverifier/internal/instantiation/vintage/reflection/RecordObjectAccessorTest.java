package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;
import java.util.Objects;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.instantiation.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class RecordObjectAccessorTest {

    private static final LinkedHashSet<TypeTag> EMPTY_TYPE_STACK = new LinkedHashSet<>();
    private Objenesis objenesis;
    private Object recordInstance;

    @BeforeEach
    public void setUp() throws Exception {
        Constructor<?> constructor =
            SimpleRecord.class.getDeclaredConstructor(int.class, String.class);
        constructor.setAccessible(true);
        objenesis = new ObjenesisStd();
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
    public void fail_whenConstructorThrowsNpe() {
        Object instance = Instantiator
            .of(NpeThrowingConstructorRecord.class, objenesis)
            .instantiate();

        ExpectedException
            .when(() -> accessorFor(instance).copy(objenesis))
            .assertThrows(ReflectionException.class)
            .assertMessageContains("Record:", "failed to run constructor", "Warning.NULL_FIELDS");
    }

    @Test
    public void fail_whenConstructorThrowsOnZero() {
        Object instance = Instantiator
            .of(ZeroThrowingConstructorRecord.class, objenesis)
            .instantiate();

        ExpectedException
            .when(() -> accessorFor(instance).copy(objenesis))
            .assertThrows(ReflectionException.class)
            .assertMessageContains("Record:", "failed to run constructor", "Warning.ZERO_FIELDS");
    }

    @Test
    public void fail_whenConstructorThrowsOnSomethingElse() {
        Object instance = Instantiator
            .of(OtherThrowingConstructorRecord.class, objenesis)
            .instantiate();

        VintageValueProvider vp = new VintageValueProvider(JavaApiPrefabValues.build(), objenesis);
        ExpectedException
            .when(() -> accessorFor(instance).scramble(vp, TypeTag.NULL, EMPTY_TYPE_STACK))
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
