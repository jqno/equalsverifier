package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import org.junit.jupiter.api.Test;

public class CachedValueProviderTest {

    private static final TypeTag INT = new TypeTag(int.class);

    private CachedValueProvider sut = new CachedValueProvider();

    @Test
    public void aRegisteredValueCanBeFound() {
        sut.put(INT, null, Tuple.of(3, 2, 3));
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, Attributes.unlabeled()).get());
    }

    @Test
    public void aValueRegisteredWithALabelCanBeFoundUnderThatLabel() {
        sut.put(INT, "label", Tuple.of(3, 2, 3));
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, Attributes.labeled("label")).get());
    }

    @Test
    public void aValueRegisteredWithALabelCanNotBeFoundWithoutThatLabel() {
        sut.put(INT, "label", Tuple.of(3, 2, 3));
        assertEquals(Optional.empty(), sut.provide(INT, Attributes.unlabeled()));
    }

    @Test
    public void aQueryWithLabelFallsBackToRegisteredValueWithoutLabel() {
        sut.put(INT, null, Tuple.of(3, 2, 3));
        assertEquals(Tuple.of(3, 2, 3), sut.provide(INT, Attributes.labeled("label")).get());
    }

    @Test
    public void aQueryWithLabelPrefersRegisteredValueWithThatLabel() {
        sut.put(INT, null, Tuple.of(3, 2, 3));
        sut.put(INT, "label", Tuple.of(4, 3, 4));
        assertEquals(Tuple.of(4, 3, 4), sut.provide(INT, Attributes.labeled("label")).get());
    }

    @Test
    public void anUnregisteredValueCanNotBeFound() {
        assertEquals(Optional.empty(), sut.provide(INT, Attributes.unlabeled()));
    }
}
