package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericPrefabValueProviderTest {

    private static final TypeTag INTEGER = new TypeTag(Integer.class);
    private static final TypeTag STRING = new TypeTag(String.class);
    private static final TypeTag LIST = new TypeTag(List.class, new TypeTag(String.class));
    private static final TypeTag MAP = new TypeTag(Map.class, INTEGER, STRING);

    private GenericPrefabValueProvider sut = new GenericPrefabValueProvider();

    @BeforeEach
    public void setup() {
        PrefabValueProvider p = new PrefabValueProvider();
        p.register(INTEGER.getType(), null, 3, 2, 3);
        p.register(STRING.getType(), null, "a", "b", "a");
        sut.setProvider(p);
    }

    @Test
    public void generic1_aRegisteredValueCanBeFound() {
        sut.register(LIST.getType(), null, (String s) -> list(s));
        assertEquals(Tuple.of(list("a"), list("b"), list("a")), sut.provide(LIST, null).get());
    }

    @Test
    public void generic2_aRegisteredValueCanBeFound() {
        sut.register(MAP.getType(), null, (Integer k, String v) -> map(k, v));
        assertEquals(Tuple.of(map(3, "a"), map(2, "b"), map(3, "a")), sut.provide(MAP, null).get());
    }

    @Test
    public void generic1_aValueRegisteredWithALabelCanBeFoundUnderThatLabel() {
        sut.register(LIST.getType(), "label", (String s) -> list(s));
        assertEquals(Tuple.of(list("a"), list("b"), list("a")), sut.provide(LIST, "label").get());
    }

    @Test
    public void generic2_aValueRegisteredWithALabelCanBeFoundUnderThatLabel() {
        sut.register(MAP.getType(), "label", (Integer k, String v) -> map(k, v));
        assertEquals(
            Tuple.of(map(3, "a"), map(2, "b"), map(3, "a")),
            sut.provide(MAP, "label").get()
        );
    }

    @Test
    public void generic1_aValueRegisteredWithALabelCanNotBeFoundWithoutThatLabel() {
        sut.register(LIST.getType(), "label", (String s) -> list(s));
        assertEquals(Optional.empty(), sut.provide(LIST, null));
    }

    @Test
    public void generic2_aValueRegisteredWithALabelCanNotBeFoundWithoutThatLabel() {
        sut.register(MAP.getType(), "label", (Integer k, String v) -> map(k, v));
        assertEquals(Optional.empty(), sut.provide(MAP, null));
    }

    @Test
    public void generic1_aQueryWithLabelFallsBackToRegisteredValueWithoutLabel() {
        sut.register(LIST.getType(), null, (String s) -> list(s));
        assertEquals(Tuple.of(list("a"), list("b"), list("a")), sut.provide(LIST, "label").get());
    }

    @Test
    public void generic2_aQueryWithLabelFallsBackToRegisteredValueWithoutLabel() {
        sut.register(MAP.getType(), null, (Integer k, String v) -> map(k, v));
        assertEquals(
            Tuple.of(map(3, "a"), map(2, "b"), map(3, "a")),
            sut.provide(MAP, "label").get()
        );
    }

    @Test
    public void generic1_aQueryWithLabelPrefersRegisteredValueWithThatLabel() {
        sut.register(LIST.getType(), null, (String s) -> list(s));
        sut.register(LIST.getType(), "label", (String s) -> list(s + "x"));
        assertEquals(
            Tuple.of(list("ax"), list("bx"), list("ax")),
            sut.provide(LIST, "label").get()
        );
    }

    @Test
    public void generic2_aQueryWithLabelPrefersRegisteredValueWithThatLabel() {
        sut.register(MAP.getType(), null, (Integer k, String v) -> map(k - 1, v + "x"));
        sut.register(MAP.getType(), "label", (Integer k, String v) -> map(k + 1, v + "y"));
        assertEquals(
            Tuple.of(map(4, "ay"), map(3, "by"), map(4, "ay")),
            sut.provide(MAP, "label").get()
        );
    }

    @Test
    public void generic1_anUnregisteredValueCanNotBeFound() {
        assertEquals(Optional.empty(), sut.provide(LIST, null));
    }

    @Test
    public void generic2_anUnregisteredValueCanNotBeFound() {
        assertEquals(Optional.empty(), sut.provide(MAP, null));
    }

    @Test
    public void generic1_copy() {
        sut.register(LIST.getType(), "original", (String s) -> list(s + "x"));
        GenericPrefabValueProvider anotherSut = sut.copy();
        sut.register(LIST.getType(), "invisibleToOther", (String s) -> list(s + "y"));

        assertEquals(
            Tuple.of(list("ax"), list("bx"), list("ax")),
            anotherSut.provide(LIST, "original").get()
        );
        assertEquals(Optional.empty(), anotherSut.provide(LIST, "invisibleToOther"));
    }

    @Test
    public void generic2_copy() {
        sut.register(MAP.getType(), "original", (Integer k, String v) -> map(k - 1, v + "x"));
        GenericPrefabValueProvider anotherSut = sut.copy();
        sut.register(
            MAP.getType(),
            "invisibleToOther",
            (Integer k, String v) -> map(k + 1, v + "y")
        );

        assertEquals(
            Tuple.of(map(2, "ax"), map(1, "bx"), map(2, "ax")),
            anotherSut.provide(MAP, "original").get()
        );
        assertEquals(Optional.empty(), anotherSut.provide(MAP, "invisibleToOther"));
    }

    private List<String> list(String s) {
        List<String> result = new ArrayList<>();
        result.add(s);
        return result;
    }

    private Map<Integer, String> map(Integer k, String v) {
        Map<Integer, String> result = new HashMap<>();
        result.put(k, v);
        return result;
    }
}
