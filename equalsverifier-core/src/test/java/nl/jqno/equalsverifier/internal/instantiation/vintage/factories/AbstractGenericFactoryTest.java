package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractGenericFactoryTest {

    private String receiver;
    private AbstractGenericFactory<String> factory;

    @BeforeEach
    void setUp() {
        receiver = "";
        factory = new AbstractGenericFactory<String>() {
            @Override
            public Tuple<String> createValues(
                    TypeTag tag,
                    VintageValueProvider valueProvider,
                    LinkedHashSet<TypeTag> typeStack) {
                return new Tuple<>("red", "blue", new String("red"));
            }
        };
    }

    @Test
    void throwTheUnthrowableException() {
        assertThatExceptionOfType(ReflectionException.class)
                .isThrownBy(
                    () -> factory.invoke(String.class, receiver, "this method does not exist", classes(), objects()));
    }
    // The rest of this class is tested indirectly through its subclasses.
}
