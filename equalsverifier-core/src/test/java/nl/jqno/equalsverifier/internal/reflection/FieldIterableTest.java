package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.*;

import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Test;

public class FieldIterableTest {

    private static final Set<Field> FIELD_CONTAINER_FIELDS = createFieldContainerFields();
    private static final Set<Field> NONSTATIC_FIELD_CONTAINER_FIELDS = createNonStaticFieldContainerFields();
    private static final Set<Field> SUB_FIELD_CONTAINER_FIELDS = createSubFieldContainerFields();
    private static final Set<Field> FIELD_AND_SUB_FIELD_CONTAINER_FIELDS = createFieldAndSubFieldContainerFields();

    @Test
    public void simpleFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(DifferentAccessModifiersFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertEquals(FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void simpleFieldsWithoutStatics() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.ofIgnoringStatic(DifferentAccessModifiersFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertEquals(NONSTATIC_FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void subAndSuperClassFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(DifferentAccessModifiersSubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertEquals(FIELD_AND_SUB_FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void onlySubClassFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.ofIgnoringSuper(DifferentAccessModifiersSubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertEquals(SUB_FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void noFields() {
        FieldIterable iterable = FieldIterable.of(NoFields.class);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void superHasNoFields() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<>();
        expected.add(NoFieldsSubWithFields.class.getField("field"));

        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(NoFieldsSubWithFields.class)) {
            actual.add(probe.getField());
        }

        assertEquals(expected, actual);
    }

    @Test
    public void subHasNoFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(EmptySubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertEquals(FIELD_CONTAINER_FIELDS, actual);
    }

    @Test
    public void classInTheMiddleHasNoFields() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<>();
        expected.addAll(FIELD_CONTAINER_FIELDS);
        expected.add(SubEmptySubFieldContainer.class.getDeclaredField("field"));

        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(SubEmptySubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertEquals(expected, actual);
    }

    @Test
    public void orderingTest() {
        FieldIterable iterable = FieldIterable.of(UnorderedFieldContainer.class);
        List<String> actual = new ArrayList<>();
        for (FieldProbe probe : iterable) {
            actual.add(probe.getName());
        }

        assertEquals(Arrays.asList("one", "two", "THREE", "FOUR"), actual);
    }

    @Test
    public void interfaceTest() {
        FieldIterable iterable = FieldIterable.of(Interface.class);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void nextAfterLastElement() {
        Iterator<FieldProbe> iterator = FieldIterable.of(DifferentAccessModifiersFieldContainer.class).iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }

        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void objectHasNoElements() {
        FieldIterable iterable = FieldIterable.of(Object.class);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void ignoreSyntheticFields() {
        FieldIterable iterable = FieldIterable.of(Outer.Inner.class);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    public void ignoreNonSyntheticCoberturaFields() {
        FieldIterable iterable = FieldIterable.of(CoberturaContainer.class);
        List<Field> fields = new ArrayList<>();
        for (FieldProbe probe : iterable) {
            fields.add(probe.getField());
        }
        assertEquals(1, fields.size());
        assertEquals("i", fields.get(0).getName());
    }

    private static Set<Field> createFieldContainerFields() {
        Set<Field> result = new HashSet<>();
        Class<DifferentAccessModifiersFieldContainer> type = DifferentAccessModifiersFieldContainer.class;
        try {
            result.add(type.getDeclaredField("i"));
            result.add(type.getDeclaredField("j"));
            result.add(type.getDeclaredField("k"));
            result.add(type.getDeclaredField("l"));
            result.add(type.getDeclaredField("I"));
            result.add(type.getDeclaredField("J"));
            result.add(type.getDeclaredField("K"));
            result.add(type.getDeclaredField("L"));
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private static Set<Field> createNonStaticFieldContainerFields() {
        Set<Field> result = new HashSet<>();
        Class<DifferentAccessModifiersFieldContainer> type = DifferentAccessModifiersFieldContainer.class;
        try {
            result.add(type.getDeclaredField("i"));
            result.add(type.getDeclaredField("j"));
            result.add(type.getDeclaredField("k"));
            result.add(type.getDeclaredField("l"));
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private static Set<Field> createSubFieldContainerFields() {
        Set<Field> result = new HashSet<>();
        Class<DifferentAccessModifiersSubFieldContainer> type = DifferentAccessModifiersSubFieldContainer.class;
        try {
            result.add(type.getDeclaredField("a"));
            result.add(type.getDeclaredField("b"));
            result.add(type.getDeclaredField("c"));
            result.add(type.getDeclaredField("d"));
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    private static Set<Field> createFieldAndSubFieldContainerFields() {
        Set<Field> result = new HashSet<>();
        result.addAll(FIELD_CONTAINER_FIELDS);
        result.addAll(SUB_FIELD_CONTAINER_FIELDS);
        return result;
    }

    public static final class CoberturaContainer {

        // CHECKSTYLE OFF: StaticVariableName
        public static transient int[] __cobertura_counters;

        // CHECKSTYLE ON: StaticVariableName

        @SuppressWarnings("unused")
        private final int i;

        public CoberturaContainer(int i) {
            this.i = i;
        }
    }
}
