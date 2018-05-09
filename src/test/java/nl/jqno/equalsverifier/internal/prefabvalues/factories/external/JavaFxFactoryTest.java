package nl.jqno.equalsverifier.internal.prefabvalues.factories.external;

import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.external.JavaFxFactory.PropertyFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("rawtypes")
public class JavaFxFactoryTest {
    private PrefabValues prefabValues;

    @Before
    public void setUp() {
        prefabValues = new PrefabValues();
        JavaApiPrefabValues.addTo(prefabValues);
    }

    @Test
    public void maintainCoverageOnJdksThatDontHaveJavafx() {
        assertNotNull(JavaFxFactory.getFactoryCache());
    }

    @Test
    public void createInstancesWithCorrectSingleGenericParameter() {
        TypeTag tag = new TypeTag(GenericContainer.class, new TypeTag(String.class));
        TypeTag listTag = new TypeTag(List.class, new TypeTag(String.class));

        PrefabValueFactory<GenericContainer> factory =
            new PropertyFactory<>(GenericContainer.class.getName(), List.class);
        Tuple<GenericContainer> tuple = factory.createValues(tag, prefabValues, null);

        assertEquals(prefabValues.giveRed(listTag), tuple.getRed().t);
        assertEquals(prefabValues.giveBlack(listTag), tuple.getBlack().t);
        assertEquals(String.class, tuple.getRed().t.get(0).getClass());
    }

    @Test
    public void createInstancesWithCorrectMultipleGenericParameter() {
        TypeTag tag = new TypeTag(GenericMultiContainer.class, new TypeTag(String.class), new TypeTag(Point.class));
        TypeTag mapTag = new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Point.class));

        PrefabValueFactory<GenericMultiContainer> factory =
            new PropertyFactory<>(GenericMultiContainer.class.getName(), Map.class);
        Tuple<GenericMultiContainer> tuple = factory.createValues(tag, prefabValues, null);

        assertEquals(prefabValues.giveRed(mapTag), tuple.getRed().t);
        assertEquals(prefabValues.giveBlack(mapTag), tuple.getBlack().t);

        Map.Entry next = (Map.Entry)tuple.getRed().t.entrySet().iterator().next();
        assertEquals(String.class, next.getKey().getClass());
        assertEquals(Point.class, next.getValue().getClass());
    }

    private static final class GenericContainer<T> {
        private final List<T> t;

        @SuppressWarnings("unused")
        public GenericContainer(List<T> t) { this.t = t; }
    }

    private static final class GenericMultiContainer<K, V> {
        private final Map<K, V> t;

        @SuppressWarnings("unused")
        public GenericMultiContainer(Map<K, V> t) { this.t = t; }
    }
}
