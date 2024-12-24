package nl.jqno.equalsverifier.internal.reflection;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuperclassIterableTest {

    private List<Class<?>> actual;

    @BeforeEach
    void setUp() {
        actual = new ArrayList<>();
    }

    @Test
    void simpleClassExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleClass.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(emptyList());
    }

    @Test
    void simpleClassIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleClass.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(List.of(SimpleClass.class));
    }

    @Test
    void hierarchyExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubSubclass.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(asList(SimpleSubclass.class, SimpleClass.class));
    }

    @Test
    void hierarchyIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleSubSubclass.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(asList(SimpleSubSubclass.class, SimpleSubclass.class, SimpleClass.class));
    }

    @Test
    void interfaceExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleInterface.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(emptyList());
    }

    @Test
    void interfaceIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleInterface.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(List.of(SimpleInterface.class));
    }

    @Test
    void subInterfaceExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubInterface.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(emptyList());
    }

    @Test
    void subInterfaceIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleSubInterface.class)) {
            actual.add(type);
        }
        assertThat(actual).isEqualTo(List.of(SimpleSubInterface.class));
    }

    static class SimpleClass {}

    static class SimpleSubclass extends SimpleClass {}

    static class SimpleSubSubclass extends SimpleSubclass {}

    interface SimpleInterface {}

    static class SimpleSubInterface implements SimpleInterface {}
}
