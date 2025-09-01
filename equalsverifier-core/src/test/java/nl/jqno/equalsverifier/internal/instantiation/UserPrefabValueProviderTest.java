package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class UserPrefabValueProviderTest {

    private static final String SOME_FIELDNAME = "someFieldName";
    private static final TypeTag INT = new TypeTag(int.class);
    private static final TypeTag INTEGER = new TypeTag(Integer.class);

    private UserPrefabValueProvider sut = new UserPrefabValueProvider();

    @Test
    public void aRegisteredValueCanBeFound() {
        sut.register(INT.getType(), 3, 2, 3);
        assertThat(sut.provide(INT, SOME_FIELDNAME)).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    public void aRegisteredResettableValueCanBeFound() {
        sut.registerResettable(INT.getType(), () -> 3, () -> 2, () -> 3);
        assertThat(sut.provide(INT, SOME_FIELDNAME)).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    void aNormalValueCanBeCached() {
        sut.register(INT.getType(), 3, 2, 3);
        assertThat(sut.canBeCached(INT.getType())).isTrue();
    }

    @Test
    void aResettableValueCannotBeCached() {
        sut.registerResettable(INT.getType(), () -> 3, () -> 2, () -> 3);
        assertThat(sut.canBeCached(INT.getType())).isFalse();
    }

    @Test
    void anUnregisteredValueCanBeCached() {
        assertThat(sut.canBeCached(INT.getType())).isTrue();
    }

    @Test
    public void fieldNameIsIgnoredWhenFindingAValue() {
        sut.register(INT.getType(), 3, 2, 3);
        assertThat(sut.provide(INT, "label")).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    public void itFallsBackToBoxedType() {
        sut.register(INTEGER.getType(), 3, 2, 3);
        assertThat(sut.provide(INT, SOME_FIELDNAME)).contains(new Tuple<>(3, 2, 3));
    }

    @Test
    public void anUnregisteredValueCanNotBeFound() {
        assertThat(sut.provide(INT, SOME_FIELDNAME)).isEmpty();
    }

    @Test
    public void copy() {
        sut.register(INT.getType(), 1, 2, 1);
        UserPrefabValueProvider anotherSut = sut.copy();

        assertThat(anotherSut.provide(INT, SOME_FIELDNAME)).contains(new Tuple<>(1, 2, 1));
    }

    @Test
    public void copyResettable() {
        sut.registerResettable(INT.getType(), () -> 1, () -> 2, () -> 1);
        UserPrefabValueProvider anotherSut = sut.copy();

        assertThat(anotherSut.provide(INT, SOME_FIELDNAME)).contains(new Tuple<>(1, 2, 1));
    }

    @Test
    public void copyDoesNotReflectLaterChangesToOriginal() {
        sut.register(INT.getType(), 1, 2, 1);
        UserPrefabValueProvider anotherSut = sut.copy();
        sut.register(INT.getType(), 3, 4, 3);

        assertThat(anotherSut.provide(INT, SOME_FIELDNAME)).contains(new Tuple<>(1, 2, 1));
        assertThat(sut.provide(INT, SOME_FIELDNAME)).contains(new Tuple<>(3, 4, 3));
    }
}
