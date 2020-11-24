package nl.jqno.equalsverifier.internal.prefabvalues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
    private final Map<List<Integer>, Map<List<Double>, Map<String, Float>>> fieldWithRidiculousTypeParameters =
        null;

    @SuppressWarnings({ "unused", "rawtypes" })
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

    private static Stream<Arguments> data() {
        return Stream.of(
            Arguments.of("simpleField", new TypeTag(String.class)),
            Arguments.of(
                "fieldWithSingleTypeParameter",
                new TypeTag(List.class, new TypeTag(String.class))
            ),
            Arguments.of(
                "fieldWithTwoTypeParameters",
                new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Integer.class))
            ),
            Arguments.of(
                "fieldWithNestedTypeParameters",
                new TypeTag(
                    Map.class,
                    new TypeTag(String.class),
                    new TypeTag(List.class, new TypeTag(String.class))
                )
            ),
            Arguments.of(
                "fieldWithRidiculousTypeParameters",
                new TypeTag(
                    Map.class,
                    new TypeTag(List.class, new TypeTag(Integer.class)),
                    new TypeTag(
                        Map.class,
                        new TypeTag(List.class, new TypeTag(Double.class)),
                        new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Float.class))
                    )
                )
            ),
            Arguments.of("rawMapField", new TypeTag(Map.class)),
            Arguments.of(
                "fieldWithWildcardParameter",
                new TypeTag(List.class, new TypeTag(Object.class))
            ),
            Arguments.of(
                "fieldWithExtendingWildcardWithTypeVariable",
                new TypeTag(List.class, new TypeTag(Comparable.class, new TypeTag(Object.class)))
            ),
            Arguments.of(
                "fieldWithExtendingWildcardWithWildcard",
                new TypeTag(List.class, new TypeTag(Comparable.class, new TypeTag(Object.class)))
            ),
            Arguments.of(
                "fieldWithSuperingWildcard",
                new TypeTag(List.class, new TypeTag(Point.class))
            ),
            Arguments.of(
                "fieldWithGenericArrayParameter",
                new TypeTag(Class[].class, new TypeTag(String.class))
            ),
            Arguments.of(
                "fieldWithTypeVariable",
                new TypeTag(List.class, new TypeTag(Object.class))
            ),
            // See TypeTagTest for fieldWithBoundedTypeVariable
            Arguments.of("primitiveField", new TypeTag(int.class)),
            Arguments.of("arrayField", new TypeTag(String[].class))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void correctness(String fieldName, TypeTag expected) {
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
