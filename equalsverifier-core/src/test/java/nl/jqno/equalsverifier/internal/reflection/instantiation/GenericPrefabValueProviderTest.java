package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.GenericPrefabValueProvider.GenericFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericPrefabValueProviderTest {

    private static final TypeTag INTEGER = new TypeTag(Integer.class);
    private static final TypeTag STRING = new TypeTag(String.class);
    private static final TypeTag LIST = new TypeTag(List.class, new TypeTag(String.class));
    private static final TypeTag SET = new TypeTag(Set.class, new TypeTag(String.class));
    private static final TypeTag MAP = new TypeTag(Map.class, INTEGER, STRING);
    private static final TypeTag ENTRY = new TypeTag(Map.Entry.class, INTEGER, STRING);

    private PrefabValueProvider prefab = new PrefabValueProvider();
    private GenericFactories factories = new GenericFactories();
    private GenericPrefabValueProvider sut;

    @BeforeEach
    public void setup() {
        prefab.register(INTEGER.getType(), null, 3, 2, 3);
        prefab.register(STRING.getType(), null, "a", "b", "a");
    }

    @Test
    public void generic1_aRegisteredValueCanBeFound() {
        factories.register(LIST.getType(), (String s) -> list(s));
        construct();
        assertEquals(Tuple.of(list("a"), list("b"), list("a")), sut.provide(LIST, null).get());
    }

    @Test
    public void generic2_aRegisteredValueCanBeFound() {
        factories.register(MAP.getType(), (Integer k, String v) -> map(k, v));
        construct();
        assertEquals(Tuple.of(map(3, "a"), map(2, "b"), map(3, "a")), sut.provide(MAP, null).get());
    }

    @Test
    public void generic1_anUnregisteredValueCanNotBeFound() {
        construct();
        assertEquals(Optional.empty(), sut.provide(LIST, null));
    }

    @Test
    public void generic2_anUnregisteredValueCanNotBeFound() {
        construct();
        assertEquals(Optional.empty(), sut.provide(MAP, null));
    }

    @Test
    public void generic1_copy() {
        // CHECKSTYLE OFF: VariableDeclarationUsageDistance
        factories.register(LIST.getType(), (String s) -> list(s + "x"));
        GenericFactories otherFactories = factories.copy();
        factories.register(SET.getType(), (String s) -> set(s + "y"));

        construct();
        assertEquals(Tuple.of(set("ay"), set("by"), set("ay")), sut.provide(SET));

        GenericPrefabValueProvider anotherSut = new GenericPrefabValueProvider(
            otherFactories,
            prefab
        );
        assertEquals(Tuple.of(list("ax"), list("bx"), list("ax")), anotherSut.provide(LIST));
        assertEquals(Optional.empty(), anotherSut.provide(SET, null));
        // CHECKSTYLE ON: VariableDeclarationUsageDistance
    }

    @Test
    public void generic2_copy() {
        // CHECKSTYLE OFF: VariableDeclarationUsageDistance
        factories.register(MAP.getType(), (Integer k, String v) -> map(k - 1, v + "x"));
        GenericFactories otherFactories = factories.copy();
        factories.register(ENTRY.getType(), (Integer k, String v) -> entry(k + 1, v + "y"));

        construct();
        assertEquals(Tuple.of(entry(4, "ay"), entry(3, "by"), entry(4, "ay")), sut.provide(ENTRY));

        GenericPrefabValueProvider anotherSut = new GenericPrefabValueProvider(
            otherFactories,
            prefab
        );
        assertEquals(
            Tuple.of(map(2, "ax"), map(1, "bx"), map(2, "ax")),
            anotherSut.provide(MAP, "original").get()
        );
        assertEquals(Optional.empty(), anotherSut.provide(ENTRY, null));
        // CHECKSTYLE ON: VariableDeclarationUsageDistance
    }

    private void construct() {
        sut = new GenericPrefabValueProvider(factories, prefab);
    }

    private List<String> list(String s) {
        List<String> result = new ArrayList<>();
        result.add(s);
        return result;
    }

    private Set<String> set(String s) {
        Set<String> result = new HashSet<>();
        result.add(s);
        return result;
    }

    private Map<Integer, String> map(Integer k, String v) {
        Map<Integer, String> result = new HashMap<>();
        result.put(k, v);
        return result;
    }

    private Map.Entry<Integer, String> entry(Integer k, String v) {
        return new AbstractMap.SimpleEntry<>(k, v);
    }
}
