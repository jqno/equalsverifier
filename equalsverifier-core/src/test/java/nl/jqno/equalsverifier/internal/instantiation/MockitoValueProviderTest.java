package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.*;
import org.junit.jupiter.api.Test;

public class MockitoValueProviderTest {

    private static final String SOME_FIELD_NAME = "someFieldname";

    private final MockitoValueProvider sut = new MockitoValueProvider(false);

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

    @Test
    void provideRecordWithPrecondition() {
        check(PreconditionTypeHelper.SinglePreconditionRecord.class);
    }

    @Test
    void provideNothingWhenJavaApiClass() {
        var actual = sut.provide(new TypeTag(List.class), SOME_FIELD_NAME);
        assertThat(actual).isEmpty();
    }

    @Test
    void provideNothingWhenDisabled() {
        var disabled = new MockitoValueProvider(true);
        var actual = disabled.provide(new TypeTag(Point.class), SOME_FIELD_NAME);
        assertThat(actual).isEmpty();
    }

    private void check(Class<?> type) {
        var tuple = sut.provide(new TypeTag(type), SOME_FIELD_NAME).get();
        assertThat(tuple.red()).isNotEqualTo(tuple.blue()).isEqualTo(tuple.redCopy());
    }
}
