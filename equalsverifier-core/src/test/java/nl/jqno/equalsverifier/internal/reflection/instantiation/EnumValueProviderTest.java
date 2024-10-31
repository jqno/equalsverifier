package nl.jqno.equalsverifier.internal.reflection.instantiation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.jupiter.api.Test;

public class EnumValueProviderTest {

    private EnumValueProvider sut = new EnumValueProvider();

    @Test
    public void multiElementEnum() {
        Tuple<TwoElementEnum> actual = sut.provide(new TypeTag(TwoElementEnum.class));
        assertEquals(Tuple.of(TwoElementEnum.ONE, TwoElementEnum.TWO, TwoElementEnum.ONE), actual);
    }

    @Test
    public void oneElementEnum() {
        Tuple<OneElementEnum> actual = sut.provide(new TypeTag(OneElementEnum.class));
        assertEquals(Tuple.of(OneElementEnum.ONE, OneElementEnum.ONE, OneElementEnum.ONE), actual);
    }

    @Test
    public void noElementEnum() {
        Tuple<EmptyEnum> actual = sut.provide(new TypeTag(EmptyEnum.class));
        assertEquals(Tuple.of(null, null, null), actual);
    }

    @Test
    public void noEnum() {
        Optional<Tuple<Object>> actual = sut.provide(new TypeTag(Object.class), null);
        assertEquals(Optional.empty(), actual);
    }
}
