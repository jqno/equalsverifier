package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
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
                            LinkedHashSet<TypeTag> typeStack) {
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
                                objects()));
    }

    // The rest of this class is tested indirectly through its subclasses.
}
