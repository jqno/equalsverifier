package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.jupiter.api.Test;

public class EnumValueProviderTest {

    private static final Attributes EMPTY_ATTRIBUTES = Attributes.unlabeled();

    private EnumValueProvider sut = new EnumValueProvider();

    @Test
    public void multiElementEnum() {
        Tuple<TwoElementEnum> actual = sut.provideOrThrow(
            new TypeTag(TwoElementEnum.class),
            EMPTY_ATTRIBUTES
        );
        assertEquals(Tuple.of(TwoElementEnum.ONE, TwoElementEnum.TWO, TwoElementEnum.ONE), actual);
    }

    @Test
    public void oneElementEnum() {
        Tuple<OneElementEnum> actual = sut.provideOrThrow(
            new TypeTag(OneElementEnum.class),
            EMPTY_ATTRIBUTES
        );
        assertEquals(Tuple.of(OneElementEnum.ONE, OneElementEnum.ONE, OneElementEnum.ONE), actual);
    }

    @Test
    public void noElementEnum() {
        Tuple<EmptyEnum> actual = sut.provideOrThrow(
            new TypeTag(EmptyEnum.class),
            EMPTY_ATTRIBUTES
        );
        assertEquals(Tuple.of(null, null, null), actual);
    }

    @Test
    public void noEnum() {
        Optional<Tuple<Object>> actual = sut.provide(
            new TypeTag(Object.class),
            Attributes.unlabeled()
        );
        assertEquals(Optional.empty(), actual);
    }
}
