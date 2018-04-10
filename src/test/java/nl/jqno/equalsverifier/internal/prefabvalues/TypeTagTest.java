package nl.jqno.equalsverifier.internal.prefabvalues;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TypeTagTest {
    private static final TypeTag SOME_LONG_TYPETAG =
            new TypeTag(Map.class, new TypeTag(Integer.class), new TypeTag(List.class, new TypeTag(String.class)));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void equalsAndHashCode() {
        EqualsVerifier.forClass(TypeTag.class)
                .withPrefabValues(TypeTag.class, new TypeTag(Integer.class), SOME_LONG_TYPETAG)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void typeCannotBeNull() {
        thrown.expect(NullPointerException.class);
        new TypeTag(null);
    }

    @Test
    public void getType() {
        assertEquals(Map.class, SOME_LONG_TYPETAG.getType());
    }

    @Test
    public void getGenericTypes() {
        List<TypeTag> expected = Arrays.asList(new TypeTag(Integer.class), new TypeTag(List.class, new TypeTag(String.class)));
        assertEquals(expected, SOME_LONG_TYPETAG.getGenericTypes());
    }

    @Test
    public void testToString() {
        assertEquals("String", new TypeTag(String.class).toString());
        assertEquals("List<String>", new TypeTag(List.class, new TypeTag(String.class)).toString());
        assertEquals("Map<Integer, List<String>>", SOME_LONG_TYPETAG.toString());
    }

    @Test
    public void matchParameterizedField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("t");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertEquals(new TypeTag(String.class), actual);
    }

    @Test
    public void matchParameterizedGenericField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("ts");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertEquals(new TypeTag(List.class, new TypeTag(String.class)), actual);
    }

    @Test
    public void matchParameterizedArrayField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("tarr");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertEquals(new TypeTag(String[].class), actual);
    }

    @Test
    public void matchNestedParameterizedGenericField() throws Exception {
        Field enclosingField = ContainerContainer.class.getDeclaredField("stringContainer");
        TypeTag enclosingType = TypeTag.of(enclosingField, TypeTag.NULL);

        Field f = Container.class.getDeclaredField("tss");
        TypeTag actual = TypeTag.of(f, enclosingType);

        assertEquals(new TypeTag(List.class, new TypeTag(List.class, new TypeTag(String.class))), actual);
    }

    @Test
    public void correctnessOfBoundedTypeVariable() throws NoSuchFieldException {
        Field field = BoundedTypeVariable.class.getDeclaredField("fieldWithBoundedTypeVariable");
        TypeTag expected = new TypeTag(Point.class);
        TypeTag actual = TypeTag.of(field, TypeTag.NULL);
        assertEquals(expected, actual);
    }

    @Test
    public void correctnessOfRecursiveBoundedTypeVariable() throws NoSuchFieldException {
        Field field = RecursiveBoundedTypeVariable.class.getDeclaredField("fieldWithBoundedTypeVariable");
        TypeTag expected = new TypeTag(Comparable.class, new TypeTag(Object.class));
        TypeTag actual = TypeTag.of(field, TypeTag.NULL);
        assertEquals(expected, actual);
    }

    @Test
    public void correctnessOfRecursiveBoundedWildcardTypeVariable() throws NoSuchFieldException {
        Field field = RecursiveBoundedWildcardTypeVariable.class.getDeclaredField("fieldWithBoundedTypeVariable");
        TypeTag expected = new TypeTag(Comparable.class, new TypeTag(Object.class));
        TypeTag actual = TypeTag.of(field, TypeTag.NULL);
        assertEquals(expected, actual);
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
}
