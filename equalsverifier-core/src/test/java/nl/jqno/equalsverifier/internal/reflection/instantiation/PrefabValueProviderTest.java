package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.Test;

public class PrefabValueProviderTest {

    private static final TypeTag INT = new TypeTag(int.class);

    private PrefabValueProvider sut = new PrefabValueProvider();

    @Test
    public void aRegisteredValueCanBeFound() {
        sut.register(INT, null, 3, 2, 3);
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, null).get());
    }

    @Test
    public void aValueRegisteredWithALabelCanBeFoundUnderThatLabel() {
        sut.register(INT, "label", 3, 2, 3);
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, "label").get());
    }

    @Test
    public void aValueRegisteredWithALabelCanNotBeFoundWithoutThatLabel() {
        sut.register(INT, "label", 3, 2, 3);
        assertEquals(Optional.empty(), sut.provide(INT, null));
    }

    @Test
    public void aValueRegisteredWithoutALabelCanNotBeFoundWithALabel() {
        sut.register(INT, null, 3, 2, 3);
        assertEquals(Optional.empty(), sut.provide(INT, "label"));
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
