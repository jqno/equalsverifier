package nl.jqno.equalsverifier.internal.prefab;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class BuiltinPrefabValueProviderTest {

    private static final String SOME_FIELDNAME = "someFieldName";
    private BuiltinPrefabValueProvider sut = new BuiltinPrefabValueProvider();

    @Test
    void returnsEmptyWhenTagIsUnknown() {
        var tag = new TypeTag(getClass());
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isEmpty();
    }

    @Test
    void returnsEmptyWhenTagIsGeneric() {
        var tag = new TypeTag(List.class, new TypeTag(Integer.class));
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isEmpty();
    }

    @Test
    void returnsExceptionalClass() {
        check(Class.class);
    }

    @Test
    void returnsAPrimitiveValue() {
        check(int.class);
    }

    @Test
    void returnsAJavaIo() {
        check(File.class);
    }

    @Test
    void returnsAJavaLangValue() {
        check(String.class);
    }

    @Test
    void returnsAJavaLangReflectValue() {
        check(Field.class);
    }

    @Test
    void returnsAJavaMathValue() {
        check(BigDecimal.class);
    }

    @Test
    void returnsAJavaNetValue() {
        check(URI.class);
    }

    @Test
    void returnsAJavaNioValue() {
        check(ByteBuffer.class);
    }

    @Test
    void returnsAJavaNioCharsetValue() {
        check(Charset.class);
    }

    @Test
    void returnsAJavaTimeValue() {
        check(Instant.class);
    }

    @Test
    void returnsAJavaUtilValue() {
        check(OptionalInt.class);
    }

    @Test
    void returnsAJavaUtilConcurrentValue() {
        check(Semaphore.class);
    }

    @Test
    void returnsAJavaUtilConcurrentAtomicValue() {
        check(AtomicInteger.class);
    }

    @Test
    void returnsAJavaUtilConcurrentLocksValue() {
        check(StampedLock.class);
    }

    private void check(Class<?> type) {
        var tag = new TypeTag(type);
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isNotEmpty();
    }
}
