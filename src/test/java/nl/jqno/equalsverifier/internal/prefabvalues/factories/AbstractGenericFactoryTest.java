package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AbstractGenericFactoryTest {
    @Rule public ExpectedException thrown = ExpectedException.none();

    private String receiver;
    private AbstractGenericFactory<String> factory;

    @Before
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
        thrown.expect(ReflectionException.class);
        factory.invoke(String.class, receiver, "this method does not exist", classes(), objects());
    }

    // The rest of this class is tested indirectly through its subclasses.
}
