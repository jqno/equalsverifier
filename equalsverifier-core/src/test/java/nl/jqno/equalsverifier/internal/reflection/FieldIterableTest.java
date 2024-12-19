package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.lang.reflect.Field;
import java.util.*;

import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Test;

class FieldIterableTest {

    private static final Set<Field> FIELD_CONTAINER_FIELDS = createFieldContainerFields();
    private static final Set<Field> NONSTATIC_FIELD_CONTAINER_FIELDS = createNonStaticFieldContainerFields();
    private static final Set<Field> SUB_FIELD_CONTAINER_FIELDS = createSubFieldContainerFields();
    private static final Set<Field> FIELD_AND_SUB_FIELD_CONTAINER_FIELDS = createFieldAndSubFieldContainerFields();

    @Test
    void simpleFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(DifferentAccessModifiersFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertThat(actual).isEqualTo(FIELD_CONTAINER_FIELDS);
    }

    @Test
    void simpleFieldsWithoutStatics() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.ofIgnoringStatic(DifferentAccessModifiersFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertThat(actual).isEqualTo(NONSTATIC_FIELD_CONTAINER_FIELDS);
    }

    @Test
    void subAndSuperClassFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(DifferentAccessModifiersSubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertThat(actual).isEqualTo(FIELD_AND_SUB_FIELD_CONTAINER_FIELDS);
    }

    @Test
    void onlySubClassFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.ofIgnoringSuper(DifferentAccessModifiersSubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertThat(actual).isEqualTo(SUB_FIELD_CONTAINER_FIELDS);
    }

    @Test
    void noFields() {
        FieldIterable iterable = FieldIterable.of(NoFields.class);
        assertThat(iterable.iterator().hasNext()).isFalse();
    }

    @Test
    void superHasNoFields() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<>();
        expected.add(NoFieldsSubWithFields.class.getField("field"));

        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(NoFieldsSubWithFields.class)) {
            actual.add(probe.getField());
        }

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void subHasNoFields() {
        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(EmptySubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertThat(actual).isEqualTo(FIELD_CONTAINER_FIELDS);
    }

    @Test
    void classInTheMiddleHasNoFields() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<>();
        expected.addAll(FIELD_CONTAINER_FIELDS);
        expected.add(SubEmptySubFieldContainer.class.getDeclaredField("field"));

        Set<Field> actual = new HashSet<>();
        for (FieldProbe probe : FieldIterable.of(SubEmptySubFieldContainer.class)) {
            actual.add(probe.getField());
        }

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void orderingTest() {
        FieldIterable iterable = FieldIterable.of(UnorderedFieldContainer.class);
        List<String> actual = new ArrayList<>();
        for (FieldProbe probe : iterable) {
            actual.add(probe.getName());
        }

        assertThat(actual).isEqualTo(Arrays.asList("one", "two", "THREE", "FOUR"));
    }

    @Test
    void interfaceTest() {
        FieldIterable iterable = FieldIterable.of(Interface.class);
        assertThat(iterable.iterator().hasNext()).isFalse();
    }

    @Test
    void nextAfterLastElement() {
        Iterator<FieldProbe> iterator = FieldIterable.of(DifferentAccessModifiersFieldContainer.class).iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }

        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> iterator.next());
    }

    @Test
    void objectHasNoElements() {
        FieldIterable iterable = FieldIterable.of(Object.class);
        assertThat(iterable.iterator().hasNext()).isFalse();
    }

    @Test
    void ignoreSyntheticFields() {
        FieldIterable iterable = FieldIterable.of(Outer.Inner.class);
        assertThat(iterable.iterator().hasNext()).isFalse();
    }

    @Test
    void ignoreNonSyntheticCoberturaFields() {
        FieldIterable iterable = FieldIterable.of(CoberturaContainer.class);
        List<Field> fields = new ArrayList<>();
        for (FieldProbe probe : iterable) {
            fields.add(probe.getField());
        }
        assertThat(fields.size()).isEqualTo(1);
        assertThat(fields.get(0).getName()).isEqualTo("i");
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
