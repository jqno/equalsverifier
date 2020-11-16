package nl.jqno.equalsverifier.internal.reflection;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SuperclassIterableTest {
    private List<Class<?>> actual;

    @BeforeEach
    public void setUp() {
        actual = new ArrayList<>();
    }

    @Test
    public void simpleClassExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleClass.class)) {
            actual.add(type);
        }
        assertEquals(emptyList(), actual);
    }

    @Test
    public void simpleClassIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleClass.class)) {
            actual.add(type);
        }
        assertEquals(singletonList(SimpleClass.class), actual);
    }

    @Test
    public void hierarchyExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubSubclass.class)) {
            actual.add(type);
        }
        assertEquals(asList(SimpleSubclass.class, SimpleClass.class), actual);
    }

    @Test
    public void hierarchyIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleSubSubclass.class)) {
            actual.add(type);
        }
        assertEquals(
                asList(SimpleSubSubclass.class, SimpleSubclass.class, SimpleClass.class), actual);
    }

    @Test
    public void interfaceExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleInterface.class)) {
            actual.add(type);
        }
        assertEquals(emptyList(), actual);
    }

    @Test
    public void interfaceIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleInterface.class)) {
            actual.add(type);
        }
        assertEquals(singletonList(SimpleInterface.class), actual);
    }

    @Test
    public void subInterfaceExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubInterface.class)) {
            actual.add(type);
        }
        assertEquals(emptyList(), actual);
    }

    @Test
    public void subInterfaceIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleSubInterface.class)) {
            actual.add(type);
        }
        assertEquals(singletonList(SimpleSubInterface.class), actual);
    }

    static class SimpleClass {}

    static class SimpleSubclass extends SimpleClass {}

    static class SimpleSubSubclass extends SimpleSubclass {}

    interface SimpleInterface {}

    static class SimpleSubInterface implements SimpleInterface {}
}
