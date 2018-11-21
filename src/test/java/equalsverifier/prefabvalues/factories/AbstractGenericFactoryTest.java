package equalsverifier.prefabvalues.factories;

import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.reflection.ReflectionException;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.gentype.TypeTag;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedHashSet;

import static equalsverifier.reflection.Util.classes;
import static equalsverifier.reflection.Util.objects;

public class AbstractGenericFactoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String receiver;
    private AbstractGenericFactory<String> factory;

    @Before
    public void setUp() {
        receiver = "";
        factory = new AbstractGenericFactory<String>() {
            @Override
            public Tuple<String> createValues(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
                return Tuple.of("red", "black", new String("red"));
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
