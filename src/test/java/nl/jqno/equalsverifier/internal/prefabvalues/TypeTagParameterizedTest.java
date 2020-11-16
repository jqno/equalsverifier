package nl.jqno.equalsverifier.internal.prefabvalues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TypeTagParameterizedTest<T> {
    @SuppressWarnings("unused")
    private final String simpleField = null;

    @SuppressWarnings("unused")
    private final List<String> fieldWithSingleTypeParameter = null;

    @SuppressWarnings("unused")
    private final Map<String, Integer> fieldWithTwoTypeParameters = null;

    @SuppressWarnings("unused")
    private final Map<String, List<String>> fieldWithNestedTypeParameters = null;

    @SuppressWarnings("unused")
    private final Map<List<Integer>, Map<List<Double>, Map<String, Float>>>
            fieldWithRidiculousTypeParameters = null;

    @SuppressWarnings({"unused", "rawtypes"})
    private final Map rawMapField = null;

    @SuppressWarnings("unused")
    private final List<?> fieldWithWildcardParameter = null;

    @SuppressWarnings("unused")
    private final List<? extends Comparable<T>> fieldWithExtendingWildcardWithTypeVariable = null;

    @SuppressWarnings("unused")
    private final List<? extends Comparable<?>> fieldWithExtendingWildcardWithWildcard = null;

    @SuppressWarnings("unused")
    private final List<? super Point> fieldWithSuperingWildcard = null;

    @SuppressWarnings("unused")
    private final Class<String>[] fieldWithGenericArrayParameter = null;

    @SuppressWarnings("unused")
    private final List<T> fieldWithTypeVariable = null;

    @SuppressWarnings("unused")
    private final int primitiveField = 0;

    @SuppressWarnings("unused")
    private final String[] arrayField = null;

    private final String fieldName;
    private final TypeTag expected;

    public TypeTagParameterizedTest(String fieldName, TypeTag expected) {
        this.fieldName = fieldName;
        this.expected = expected;
    }

    @Parameters(name = "Field {0} should be {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                    {"simpleField", new TypeTag(String.class)},
                    {
                        "fieldWithSingleTypeParameter",
                        new TypeTag(List.class, new TypeTag(String.class))
                    },
                    {
                        "fieldWithTwoTypeParameters",
                        new TypeTag(
                                Map.class, new TypeTag(String.class), new TypeTag(Integer.class))
                    },
                    {
                        "fieldWithNestedTypeParameters",
                        new TypeTag(
                                Map.class,
                                new TypeTag(String.class),
                                new TypeTag(List.class, new TypeTag(String.class)))
                    },
                    {
                        "fieldWithRidiculousTypeParameters",
                        new TypeTag(
                                Map.class,
                                new TypeTag(List.class, new TypeTag(Integer.class)),
                                new TypeTag(
                                        Map.class,
                                        new TypeTag(List.class, new TypeTag(Double.class)),
                                        new TypeTag(
                                                Map.class,
                                                new TypeTag(String.class),
                                                new TypeTag(Float.class))))
                    },
                    {"rawMapField", new TypeTag(Map.class)},
                    {
                        "fieldWithWildcardParameter",
                        new TypeTag(List.class, new TypeTag(Object.class))
                    },
                    {
                        "fieldWithExtendingWildcardWithTypeVariable",
                        new TypeTag(
                                List.class,
                                new TypeTag(Comparable.class, new TypeTag(Object.class)))
                    },
                    {
                        "fieldWithExtendingWildcardWithWildcard",
                        new TypeTag(
                                List.class,
                                new TypeTag(Comparable.class, new TypeTag(Object.class)))
                    },
                    {
                        "fieldWithSuperingWildcard",
                        new TypeTag(List.class, new TypeTag(Point.class))
                    },
                    {
                        "fieldWithGenericArrayParameter",
                        new TypeTag(Class[].class, new TypeTag(String.class))
                    },
                    {"fieldWithTypeVariable", new TypeTag(List.class, new TypeTag(Object.class))},
                    // See TypeTagTest for fieldWithBoundedTypeVariable
                    {"primitiveField", new TypeTag(int.class)},
                    {"arrayField", new TypeTag(String[].class)}
                });
    }

    @Test
    public void correctness() {
        TypeTag actual = TypeTag.of(getField(fieldName), TypeTag.NULL);
        assertEquals(expected, actual);
    }

    private Field getField(String name) {
        try {
            return getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            fail(e.toString());
            return null;
        }
    }
}
