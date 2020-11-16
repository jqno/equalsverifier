package nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders.JavaFxFactoryProvider.PropertyFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
public class JavaFxFactoryProviderTest {
    private PrefabValues prefabValues;

    @BeforeEach
    public void setUp() {
        prefabValues = new PrefabValues(JavaApiPrefabValues.build());
    }

    @Test
    public void maintainCoverageOnJdksThatDontHaveJavafx() {
        assertNotNull(new JavaFxFactoryProvider().getFactoryCache());
    }

    @Test
    public void createInstancesWithCorrectSingleGenericParameter() {
        TypeTag tag = new TypeTag(GenericContainer.class, new TypeTag(String.class));
        TypeTag listTag = new TypeTag(List.class, new TypeTag(String.class));

        PrefabValueFactory<GenericContainer> factory =
                new PropertyFactory<>(GenericContainer.class.getName(), List.class);
        Tuple<GenericContainer> tuple = factory.createValues(tag, prefabValues, null);

        assertEquals(prefabValues.giveRed(listTag), tuple.getRed().t);
        assertEquals(prefabValues.giveBlue(listTag), tuple.getBlue().t);
        assertEquals(String.class, tuple.getRed().t.get(0).getClass());
    }

    @Test
    public void createInstancesWithCorrectMultipleGenericParameter() {
        TypeTag tag =
                new TypeTag(
                        GenericMultiContainer.class,
                        new TypeTag(String.class),
                        new TypeTag(Point.class));
        TypeTag mapTag =
                new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Point.class));

        PrefabValueFactory<GenericMultiContainer> factory =
                new PropertyFactory<>(GenericMultiContainer.class.getName(), Map.class);
        Tuple<GenericMultiContainer> tuple = factory.createValues(tag, prefabValues, null);

        assertEquals(prefabValues.giveRed(mapTag), tuple.getRed().t);
        assertEquals(prefabValues.giveBlue(mapTag), tuple.getBlue().t);

        Map.Entry next = (Map.Entry) tuple.getRed().t.entrySet().iterator().next();
        assertEquals(String.class, next.getKey().getClass());
        assertEquals(Point.class, next.getValue().getClass());
    }

    private static final class GenericContainer<T> {
        private final List<T> t;

        @SuppressWarnings("unused")
        public GenericContainer(List<T> t) {
            this.t = t;
        }
    }

    private static final class GenericMultiContainer<K, V> {
        private final Map<K, V> t;

        @SuppressWarnings("unused")
        public GenericMultiContainer(Map<K, V> t) {
            this.t = t;
        }
    }
}
