package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArrayValueProviderTest {

    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");
    private static final TypeTag STRING_ARRAY_TAG = new TypeTag(String[].class);

    private UserPrefabValueCaches underlying = new UserPrefabValueCaches();
    private ArrayValueProvider sut = new ArrayValueProvider(new UserPrefabValueProvider(underlying));

    @BeforeEach
    public void setUp() {
        underlying.register(String.class, "red", "blue", "red");
    }

    @Test
    void returnsEmpty_givenNonArrayTag() {
        var actual = sut.provide(new TypeTag(Integer.class), SOME_ATTRIBUTES);
        assertThat(actual).isEmpty();
    }

    @Test
    void returnsArray_givenArrayTag() {
        var actual = sut.<String[]>provide(STRING_ARRAY_TAG, SOME_ATTRIBUTES).get();
        assertThat(actual.red()).containsExactly(new String[] { "red" });
        assertThat(actual.blue()).containsExactly(new String[] { "blue", "red" });
        assertThat(actual.redCopy()).containsExactly(new String[] { "red" });
    }

    @Test
    void redCopyIsNotSameAsRed() {
        var actual = sut.provide(STRING_ARRAY_TAG, SOME_ATTRIBUTES).get();
        assertThat(actual.redCopy()).isEqualTo(actual.red()).isNotSameAs(actual.red());
    }
}
