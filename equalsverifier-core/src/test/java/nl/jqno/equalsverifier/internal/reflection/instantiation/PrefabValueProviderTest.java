package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class PrefabValueProviderTest {

    private static final TypeTag INT = new TypeTag(int.class);
    private static final TypeTag INTEGER = new TypeTag(Integer.class);

    private PrefabValueProvider sut = new PrefabValueProvider();

    @Test
    public void aRegisteredValueCanBeFound() {
        sut.register(INT.getType(), null, 3, 2, 3);
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, null).get());
    }

    @Test
    public void aValueRegisteredWithALabelCanBeFoundUnderThatLabel() {
        sut.register(INT.getType(), "label", 3, 2, 3);
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, "label").get());
    }

    @Test
    public void aValueRegisteredWithALabelCanNotBeFoundWithoutThatLabel() {
        sut.register(INT.getType(), "label", 3, 2, 3);
        assertEquals(Optional.empty(), sut.provide(INT, null));
    }

    @Test
    public void aQueryWithLabelFallsBackToRegisteredValueWithoutLabel() {
        sut.register(INT.getType(), null, 3, 2, 3);
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, "label").get());
    }

    @Test
    public void aQueryWithLabelPrefersRegisteredValueWithThatLabel() {
        sut.register(INT.getType(), null, 3, 2, 3);
        sut.register(INT.getType(), "label", 4, 3, 4);
        assertEquals(Tuple.of(4, 3, 4), sut.provide(INT, "label").get());
    }

    @Test
    public void itFallsBackToBoxedTypeWithoutLabel() {
        sut.register(INTEGER.getType(), null, 3, 2, 3);
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, null).get());
    }

    @Test
    public void itFallsBackToBoxedTypeWithLabel() {
        sut.register(INTEGER.getType(), "label", 3, 2, 3);
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, "label").get());
    }

    @Test
    public void anUnregisteredValueCanNotBeFound() {
        assertEquals(Optional.empty(), sut.provide(INT, null));
    }

    @Test
    public void keyEqualsAndHashCode() {
        EqualsVerifier
            .forClass(PrefabValueProvider.Key.class)
            .withPrefabValues(TypeTag.class, new TypeTag(Integer.class), new TypeTag(String.class))
            .verify();
    }
}
