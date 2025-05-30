package nl.jqno.equalsverifier.coverage;

import java.lang.reflect.Constructor;
import java.util.stream.Stream;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.Color;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CoverageNoInheritanceTest {

    private static Stream<Arguments> data() {
        return Stream
                .of(
                    Arguments.of(EclipseGetClassPoint.class),
                    Arguments.of(EclipseInstanceOfPoint.class),
                    Arguments.of(HandwrittenGetClassPoint.class),
                    Arguments.of(HandwrittenInstanceOfPoint.class),
                    Arguments.of(IntelliJGetClassPoint.class),
                    Arguments.of(IntelliJInstanceOfPoint.class),
                    Arguments.of(LombokInstanceOfPoint.class),
                    Arguments.of(NetBeansGetClassPoint.class),
                    Arguments.of(PatternMatchInstanceofPoint.class));
    }

    @ParameterizedTest
    @MethodSource("data")
    void coverage(Class<?> type) {
        EqualsVerifier.forClass(type).verify();
    }

    @ParameterizedTest
    @MethodSource("data")
    void callTheConstructor(Class<?> type) throws Exception {
        Constructor<?> constructor = type.getConstructor(int.class, int.class, Color.class);
        constructor.newInstance(0, 0, Color.INDIGO);
    }
}
