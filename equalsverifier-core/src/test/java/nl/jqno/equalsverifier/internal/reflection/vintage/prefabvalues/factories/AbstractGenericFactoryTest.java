package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.AbstractGenericFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractGenericFactoryTest {

    private String receiver;
    private AbstractGenericFactory<String> factory;

    @BeforeEach
    public void setUp() {
        receiver = "";
        factory =
            new AbstractGenericFactory<String>() {
                @Override
                public Tuple<String> createValues(
                    TypeTag tag,
                    PrefabValues prefabValues,
                    LinkedHashSet<TypeTag> typeStack
                ) {
                    return Tuple.of("red", "blue", new String("red"));
                }
            };
    }

    @Test
    public void throwTheUnthrowableException() {
        assertThrows(
            ReflectionException.class,
            () ->
                factory.invoke(
                    String.class,
                    receiver,
                    "this method does not exist",
                    classes(),
                    objects()
                )
        );
    }
    // The rest of this class is tested indirectly through its subclasses.
}
