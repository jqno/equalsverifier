package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.jupiter.api.Test;

public class EnumValueProviderTest {

    private static final Attributes SOME_ATTRIBUTES = Attributes.named("someFieldName");
    private EnumValueProvider sut = new EnumValueProvider();

    @Test
    void returnsEmpty_givenNonEnum() {
        var actual = sut.provide(new TypeTag(Integer.class), SOME_ATTRIBUTES);
        assertThat(actual).isEmpty();
    }

    @Test
    void returnsNull_givenEmptyEnum() {
        var actual = sut.provideOrThrow(new TypeTag(EmptyEnum.class), SOME_ATTRIBUTES);
        assertThat(actual.red()).isNull();
    }

    @Test
    void returnsSameRedAndBlue_givenSingleElementEnum() {
        var actual = sut.provideOrThrow(new TypeTag(OneElementEnum.class), SOME_ATTRIBUTES);
        assertThat(actual.red()).isEqualTo(OneElementEnum.ONE).isEqualTo(actual.blue());
    }

    @Test
    void returnsDifferentRedAndBlue_givenMultiElementEnum() {
        var actual = sut.provideOrThrow(new TypeTag(TwoElementEnum.class), SOME_ATTRIBUTES);
        assertThat(actual.red()).isEqualTo(TwoElementEnum.ONE);
        assertThat(actual.blue()).isEqualTo(TwoElementEnum.TWO);
    }
}
