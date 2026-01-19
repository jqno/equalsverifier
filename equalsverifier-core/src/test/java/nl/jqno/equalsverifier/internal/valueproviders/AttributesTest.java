package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

class AttributesTest {

    private static final TypeTag SOME_TAG = new TypeTag(String.class);
    private static final TypeTag ANOTHER_TAG = new TypeTag(Integer.class);

    @Test
    void emptyFactory_createsCorrectInstance() {
        var attrs = Attributes.empty();
        assertThat(attrs.cacheKey()).isNull();
        assertThat(attrs.fieldName()).isNull();
    }

    @Test
    void namedFactory_createsCorrectInstance() {
        var fieldName = "testField";
        var attrs = Attributes.named(fieldName);
        assertThat(attrs.cacheKey()).isEqualTo(fieldName);
        assertThat(attrs.fieldName()).isEqualTo(fieldName);
    }

    @Test
    void clearCacheKey_clearsCacheKeyButNotFieldName() {
        var fieldName = "testField";
        var attrs = Attributes.named(fieldName).clearCacheKey();
        assertThat(attrs.cacheKey()).isNull();
        assertThat(attrs.fieldName()).isEqualTo(fieldName);
    }

    @Test
    void addToStack_returnsNewInstanceWithAddedType() {
        var original = Attributes.empty();

        var actual = original.addToStack(SOME_TAG);

        assertThat(actual).isNotSameAs(original);
        assertThat(actual.typeStackContains(SOME_TAG)).isTrue();
    }

    @Test
    void addToStack_onNamedInstance_returnsNewInstanceWithSameFieldName() {
        var original = Attributes.named("test");
        var tag = new TypeTag(String.class);

        var actual = original.addToStack(tag);

        assertThat(actual.cacheKey()).isEqualTo("test");
    }

    @Test
    void addToStack_preservesOriginalStack() {
        var original = Attributes.empty().addToStack(SOME_TAG);

        var actual = original.addToStack(ANOTHER_TAG);

        assertThat(actual.typeStackContains(SOME_TAG)).isTrue();
        assertThat(actual.typeStackContains(ANOTHER_TAG)).isTrue();
        assertThat(original.typeStackContains(SOME_TAG)).isTrue();
        assertThat(original.typeStackContains(ANOTHER_TAG)).isFalse();
    }

    @Test
    void typeStackContains() {
        var empty = Attributes.empty();
        assertThat(empty.typeStackContains(SOME_TAG)).isFalse();

        var withOne = empty.addToStack(SOME_TAG);
        assertThat(withOne.typeStackContains(SOME_TAG)).isTrue();
    }

    @Test
    void typeStack_returnsACopy() {
        var original = Attributes.empty();
        var stack = original.typeStack();

        stack.add(SOME_TAG);

        assertThat(original.typeStackContains(SOME_TAG)).isFalse();
        assertThat(original.typeStack()).isEmpty();
    }
}
