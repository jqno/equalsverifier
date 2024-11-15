package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders.INTS;
import static nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders.STRINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.GenericPrefabValueProvider.GenericFactories;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import org.junit.jupiter.api.Test;

public class GenericPrefabValueProviderTest {

    private static final TypeTag INTEGER = new TypeTag(Integer.class);
    private static final TypeTag STRING = new TypeTag(String.class);
    private static final TypeTag LIST = new TypeTag(List.class, new TypeTag(String.class));
    private static final TypeTag SET = new TypeTag(Set.class, new TypeTag(String.class));
    private static final TypeTag MAP = new TypeTag(Map.class, INTEGER, STRING);
    private static final TypeTag ENTRY = new TypeTag(Map.Entry.class, INTEGER, STRING);

    private ValueProvider prefab = TestValueProviders.simple();
    private GenericFactories factories = new GenericFactories();
    private GenericPrefabValueProvider sut = new GenericPrefabValueProvider(factories, prefab);

    @Test
    public void generic1_aRegisteredValueCanBeFound() {
        factories.register(LIST.getType(), (String s) -> list(s));
        assertEquals(STRINGS.map(this::list), sut.provide(LIST, null).get());
    }

    @Test
    public void generic2_aRegisteredValueCanBeFound() {
        factories.register(MAP.getType(), (Integer k, String v) -> map(k, v));
        assertEquals(Tuple.combine(INTS, STRINGS, this::map), sut.provide(MAP, null).get());
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
        // CHECKSTYLE OFF: VariableDeclarationUsageDistance
        factories.register(LIST.getType(), (String s) -> list(s + "x"));
        GenericFactories otherFactories = factories.copy();
        factories.register(SET.getType(), (String s) -> set(s + "y"));

        assertEquals(STRINGS.map(s -> set(s + "y")), sut.provideOrThrow(SET, null));

        GenericPrefabValueProvider anotherSut = new GenericPrefabValueProvider(
            otherFactories,
            TestValueProviders.simple()
        );
        assertEquals(STRINGS.map(s -> list(s + "x")), anotherSut.provideOrThrow(LIST, null));
        assertEquals(Optional.empty(), anotherSut.provide(SET, null));
        // CHECKSTYLE ON: VariableDeclarationUsageDistance
    }

    @Test
    public void generic2_copy() {
        // CHECKSTYLE OFF: VariableDeclarationUsageDistance
        factories.register(MAP.getType(), (Integer k, String v) -> map(k - 1, v + "x"));
        GenericFactories otherFactories = factories.copy();
        factories.register(ENTRY.getType(), (Integer k, String v) -> entry(k + 1, v + "y"));

        assertEquals(
            Tuple.combine(INTS, STRINGS, (k, v) -> entry(k + 1, v + "y")),
            sut.provideOrThrow(ENTRY, null)
        );

        GenericPrefabValueProvider anotherSut = new GenericPrefabValueProvider(
            otherFactories,
            TestValueProviders.simple()
        );
        assertEquals(
            Tuple.combine(INTS, STRINGS, (k, v) -> map(k - 1, v + "x")),
            anotherSut.provide(MAP, "original").get()
        );
        assertEquals(Optional.empty(), anotherSut.provide(ENTRY, null));
        // CHECKSTYLE ON: VariableDeclarationUsageDistance
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
