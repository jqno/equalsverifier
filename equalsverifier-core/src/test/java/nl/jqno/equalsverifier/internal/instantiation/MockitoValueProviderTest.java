package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.*;
import org.junit.jupiter.api.Test;

public class MockitoValueProviderTest {

    private static final String SOME_FIELD_NAME = "someFieldname";

    private final MockitoValueProvider sut = new MockitoValueProvider();

    @Test
    void providesSimpleClass() {
        check(Point.class);
    }

    @Test
    void provideFinalClass() {
        check(FinalPoint.class);
    }

    @Test
    void provideRecord() {
        check(RecordPoint.class);
    }

    @Test
    void provideRecursiveClass() {
        check(RecursiveTypeHelper.Node.class);
    }

    @Test
    void provideTwoStepRecursiveClass() {
        check(RecursiveTypeHelper.TwoStepNodeA.class);
    }

    private void check(Class<?> type) {
        var tuple = sut.provide(new TypeTag(type), SOME_FIELD_NAME).get();
        assertThat(tuple.red()).isNotEqualTo(tuple.blue()).isEqualTo(tuple.redCopy());
    }
}
