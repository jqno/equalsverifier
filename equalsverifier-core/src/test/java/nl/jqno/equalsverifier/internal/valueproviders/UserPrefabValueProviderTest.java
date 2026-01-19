package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class UserPrefabValueProviderTest {

    private static final String SOME_FIELDNAME = "someFieldName";
    private static final TypeTag INT = new TypeTag(int.class);
    private static final TypeTag INTEGER = new TypeTag(Integer.class);

    private UserPrefabValueCaches prefabs = new UserPrefabValueCaches();
    private UserPrefabValueProvider provider = new UserPrefabValueProvider(prefabs);

    @Test
    public void aRegisteredValueCanBeFound() {
        prefabs.register(INT.getType(), 3, 2, 3);
        assertThat(provider.provide(INT, Attributes.named(SOME_FIELDNAME))).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    public void aRegisteredResettableValueCanBeFound() {
        prefabs.registerResettable(INT.getType(), () -> 3, () -> 2, () -> 3);
        assertThat(provider.provide(INT, Attributes.named(SOME_FIELDNAME))).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    void aNormalValueCanBeCached() {
        prefabs.register(INT.getType(), 3, 2, 3);
        assertThat(prefabs.canBeCached(INT.getType())).isTrue();
    }

    @Test
    void aResettableValueCannotBeCached() {
        prefabs.registerResettable(INT.getType(), () -> 3, () -> 2, () -> 3);
        assertThat(prefabs.canBeCached(INT.getType())).isFalse();
    }

    @Test
    void anUnregisteredValueCanBeCached() {
        assertThat(prefabs.canBeCached(INT.getType())).isTrue();
    }

    @Test
    public void fieldNameIsIgnoredWhenFindingAValue() {
        prefabs.register(INT.getType(), 3, 2, 3);
        assertThat(provider.provide(INT, Attributes.named("label"))).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    public void itFallsBackToBoxedType() {
        prefabs.register(INTEGER.getType(), 3, 2, 3);
        assertThat(provider.provide(INT, Attributes.named(SOME_FIELDNAME))).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    public void anUnregisteredValueCanNotBeFound() {
        assertThat(provider.provide(INT, Attributes.named(SOME_FIELDNAME))).isEmpty();
    }

    @Test
    public void copy() {
        prefabs.register(INT.getType(), 1, 2, 1);
        UserPrefabValueProvider anotherSut = provider.copy();

        assertThat(anotherSut.provide(INT, Attributes.named(SOME_FIELDNAME))).contains(new Tuple<>(1, 2, 1));
    }

    @Test
    public void copyResettable() {
        prefabs.registerResettable(INT.getType(), () -> 1, () -> 2, () -> 1);
        UserPrefabValueProvider anotherSut = provider.copy();

        assertThat(anotherSut.provide(INT, Attributes.named(SOME_FIELDNAME))).contains(new Tuple<>(1, 2, 1));
    }

    @Test
    public void copyDoesNotReflectLaterChangesToOriginal() {
        prefabs.register(INT.getType(), 1, 2, 1);
        UserPrefabValueProvider anotherSut = provider.copy();
        prefabs.register(INT.getType(), 3, 4, 3);

        assertThat(anotherSut.provide(INT, Attributes.named(SOME_FIELDNAME))).contains(new Tuple<>(1, 2, 1));
        assertThat(provider.provide(INT, Attributes.named(SOME_FIELDNAME))).contains(new Tuple<>(3, 4, 3));
    }
}
