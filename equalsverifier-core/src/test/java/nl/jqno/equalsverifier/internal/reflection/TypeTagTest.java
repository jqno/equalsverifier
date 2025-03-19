package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class TypeTagTest {

    private static final TypeTag SOME_LONG_TYPETAG =
            new TypeTag(Map.class, new TypeTag(Integer.class), new TypeTag(List.class, new TypeTag(String.class)));

    @Test
    void equalsAndHashCode() {
        EqualsVerifier
                .forClass(TypeTag.class)
                .withPrefabValues(TypeTag.class, new TypeTag(Integer.class), SOME_LONG_TYPETAG)
                .suppress(Warning.NULL_FIELDS)
                .set(Mode.skipMockito())
                .verify();
    }

    @Test
    void typeCannotBeNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new TypeTag(null));
    }

    @Test
    void getType() {
        assertThat(SOME_LONG_TYPETAG.getType()).isEqualTo(Map.class);
    }

    @Test
    void getGenericTypes() {
        List<TypeTag> expected =
                Arrays.asList(new TypeTag(Integer.class), new TypeTag(List.class, new TypeTag(String.class)));
        assertThat(SOME_LONG_TYPETAG.genericTypes()).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(new TypeTag(String.class).toString()).isEqualTo("String");
        assertThat(new TypeTag(List.class, new TypeTag(String.class)).toString()).isEqualTo("List<String>");
        assertThat(SOME_LONG_TYPETAG.toString()).isEqualTo("Map<Integer, List<String>>");
    }

    @Test
    void matchParameterizedField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("t");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertThat(actual).isEqualTo(new TypeTag(String.class));
    }

    @Test
    void matchParameterizedGenericField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("ts");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertThat(actual).isEqualTo(new TypeTag(List.class, new TypeTag(String.class)));
    }

    @Test
    void matchParameterizedArrayField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("tarr");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertThat(actual).isEqualTo(new TypeTag(String[].class));
    }

    @Test
    void matchNestedParameterizedGenericField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("tss");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertThat(actual).isEqualTo(new TypeTag(List.class, new TypeTag(List.class, new TypeTag(String.class))));
    }

    @Test
    void correctnessOfBoundedTypeVariable() throws NoSuchFieldException {
        Field field = BoundedTypeVariable.class.getDeclaredField("fieldWithBoundedTypeVariable");
        TypeTag expected = new TypeTag(Point.class);
        TypeTag actual = TypeTag.of(field, TypeTag.NULL);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void correctnessOfRecursiveBoundedTypeVariable() throws NoSuchFieldException {
        Field field = RecursiveBoundedTypeVariable.class.getDeclaredField("fieldWithBoundedTypeVariable");
        TypeTag expected = new TypeTag(Comparable.class, new TypeTag(Object.class));
        TypeTag actual = TypeTag.of(field, TypeTag.NULL);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void correctnessOfRecursiveBoundedWildcardTypeVariable() throws NoSuchFieldException {
        Field field = RecursiveBoundedWildcardTypeVariable.class.getDeclaredField("fieldWithBoundedTypeVariable");
        TypeTag expected = new TypeTag(Comparable.class, new TypeTag(Object.class));
        TypeTag actual = TypeTag.of(field, TypeTag.NULL);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void correctnessOfWildcardFieldWithBoundedType() throws NoSuchFieldException {
        Field field = WildcardBoundedTypeVariableContainer.class.getDeclaredField("wildcard");
        TypeTag expected = new TypeTag(BoundedTypeVariable.class, new TypeTag(Point.class));
        TypeTag actual = TypeTag.of(field, TypeTag.NULL);
        assertThat(actual).isEqualTo(expected);
    }

    @SuppressWarnings("unused")
    static class ContainerContainer {

        Container<String> stringContainer;
    }

    @SuppressWarnings("unused")
    static class Container<T> {

        T t;
        List<T> ts;
        T[] tarr;
        List<List<T>> tss;
    }

    @SuppressWarnings("unused")
    static class BoundedTypeVariable<T extends Point> {

        private T fieldWithBoundedTypeVariable;
    }

    @SuppressWarnings("unused")
    static class RecursiveBoundedTypeVariable<T extends Comparable<T>> {

        private T fieldWithBoundedTypeVariable;
    }

    @SuppressWarnings("unused")
    static class RecursiveBoundedWildcardTypeVariable<T extends Comparable<? super T>> {

        private T fieldWithBoundedTypeVariable;
    }

    @SuppressWarnings("unused")
    static class WildcardBoundedTypeVariableContainer<T extends Point> {

        private BoundedTypeVariable<?> wildcard;
    }
}
