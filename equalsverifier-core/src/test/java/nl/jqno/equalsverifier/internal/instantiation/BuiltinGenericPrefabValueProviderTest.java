package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuiltinGenericPrefabValueProviderTest {

    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");
    private static final TypeTag STRING_TAG = new TypeTag(String.class);

    private UserPrefabValueProvider underlying = new UserPrefabValueProvider();
    private BuiltinGenericPrefabValueProvider sut = new BuiltinGenericPrefabValueProvider(underlying);

    @BeforeEach
    public void setUp() {
        underlying.register(String.class, "red", "blue", "red");
    }

    @Test
    void returnsEmptyWhenTypeIsUnknown() {
        var tag = new TypeTag(getClass(), STRING_TAG);
        assertThat(sut.provide(tag, SOME_ATTRIBUTES)).isEmpty();
    }

    @Test
    void returnsEmptyWhenTagIsNotGeneric() {
        assertThat(sut.provide(STRING_TAG, SOME_ATTRIBUTES)).isEmpty();
    }

    @Test
    void returnsEmptyWhenTagIsGenericButRaw() {
        var tag = new TypeTag(List.class);
        assertThat(sut.provide(tag, SOME_ATTRIBUTES)).isEmpty();
    }

    @Test
    void returnsAJavaLangValue() {
        check(ThreadLocal.class);
    }

    @Test
    void returnsAJavaUtilValue() {
        check(Optional.class);
    }

    @Test
    void returnsAJavaUtilConcurrentValue() {
        check(CompletableFuture.class);
    }

    @Test
    void returnsAJavaUtilConcurrentAtomicValue() {
        check(AtomicReference.class);
    }

    @Test
    void returnsAValueFromAnotherPackage() {
        check(Supplier.class);
    }

    private void check(Class<?> type) {
        var tag = new TypeTag(type, STRING_TAG);
        assertThat(sut.provide(tag, SOME_ATTRIBUTES)).isNotEmpty();
    }
}
