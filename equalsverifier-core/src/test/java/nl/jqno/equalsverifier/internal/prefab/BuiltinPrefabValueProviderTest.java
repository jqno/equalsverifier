package nl.jqno.equalsverifier.internal.prefab;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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
        var tag = new TypeTag(int.class);
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isNotEmpty();
    }

    @Test
    void returnsAJavaLangValue() {
        var tag = new TypeTag(String.class);
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isNotEmpty();
    }

    @Test
    void returnsAJavaMathValue() {
        var tag = new TypeTag(BigDecimal.class);
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isNotEmpty();
    }

    @Test
    void returnsAJavaUtilValue() {
        var tag = new TypeTag(OptionalInt.class);
        assertThat(sut.provide(tag, SOME_FIELDNAME)).isNotEmpty();
    }
}
