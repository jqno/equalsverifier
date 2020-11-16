package nl.jqno.equalsverifier.coverage;

import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CoverageWithInheritanceTest {

    public static Stream<Arguments> data() {
        return Stream.of(Classes.of(HandwrittenCanEqual.class), Classes.of(LombokCanEqual.class));
    }

    @ParameterizedTest
    @MethodSource("data")
    public <T> void testSuperCoverage(Classes<T> classes) {
        EqualsVerifier.forClass(classes.superType).withRedefinedSubclass(classes.subType).verify();
    }

    @ParameterizedTest
    @MethodSource("data")
    public <T> void testSubCoverage(Classes<T> classes) {
        EqualsVerifier.forClass(classes.subType)
                .withRedefinedSuperclass()
                .withRedefinedSubclass(classes.endpointType)
                .verify();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void callTheConstructors(Classes<?> classes) throws Exception {
        classes.containerType.getConstructor().newInstance();
        classes.superType.getConstructor(int.class, int.class).newInstance(0, 0);
        classes.subType
                .getConstructor(int.class, int.class, Color.class)
                .newInstance(0, 0, Color.INDIGO);
        classes.endpointType
                .getConstructor(int.class, int.class, Color.class)
                .newInstance(0, 0, Color.INDIGO);
    }

    private static final class Classes<T> {
        private final Class<?> containerType;
        private final Class<? super T> superType;
        private final Class<T> subType;
        private final Class<? extends T> endpointType;

        private Classes(
                Class<?> containerType,
                Class<? super T> superType,
                Class<T> subType,
                Class<? extends T> endpointType) {
            this.containerType = containerType;
            this.superType = superType;
            this.subType = subType;
            this.endpointType = endpointType;
        }

        @SuppressWarnings("unchecked")
        public static <T> Arguments of(Class<?> containerType) {
            Class<?>[] containingTypes = containerType.getClasses();
            Class<? super T> superType = find(containingTypes, "Point");
            Class<T> subType = find(containingTypes, "ColorPoint");
            Class<? extends T> endpointType = find(containingTypes, "EndPoint");
            Classes<T> result = new Classes<>(containerType, superType, subType, endpointType);
            return Arguments.of(result);
        }

        @SuppressWarnings("rawtypes")
        private static Class find(Class[] types, String name) {
            Class result = null;
            for (Class type : types) {
                if (type.getSimpleName().equals(name)) {
                    result = type;
                }
            }
            return result;
        }
    }
}
