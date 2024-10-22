package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
    public void aValueRegisteredAsATupleCanBeFound() {
        sut.register(INT.getType(), null, Tuple.of(3, 2, 3));
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
    public void getFieldNames() {
        sut.register(INT.getType(), "field1", 3, 2, 3);
        sut.register(INT.getType(), "field2", 3, 2, 3);
        assertEquals(set("field1", "field2"), sut.getFieldNames());

        sut.register(INT.getType(), "field3", 3, 2, 3);
        assertEquals(set("field1", "field2", "field3"), sut.getFieldNames());
    }

    @Test
    public void copy() {
        sut.register(INT.getType(), "original", 1, 2, 1);
        PrefabValueProvider anotherSut = sut.copy();
        sut.register(INT.getType(), "invisibleToOther", 3, 4, 3);

        assertEquals(Tuple.of(1, 2, 1), anotherSut.provide(INT, "original").get());
        assertEquals(Optional.empty(), anotherSut.provide(INT, "invisibleToOther"));
    }

    private Set<String> set(String... strings) {
        Set<String> result = new HashSet<>();
        for (String s : strings) {
            result.add(s);
        }
        return result;
    }
}
