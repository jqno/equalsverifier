package nl.jqno.equalsverifier.internal.prefab;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.OptionalInt;

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
    void returnsAPrimitiveValue() {
        check(int.class);
    }

    @Test
    void returnsAJavaLangValue() {
        check(String.class);
    }

    @Test
    void returnsAJavaMathValue() {
        check(BigDecimal.class);
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
    void returnsAJavaUtilValue() {
        check(OptionalInt.class);
    }

    private void check(Class<?> type) {
        var tag = new TypeTag(type);
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isNotEmpty();
    }
}
