package equalsverifier.prefabvalues.factoryproviders;

import equalsverifier.prefabvalues.JavaApiPrefabValues;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabvalues.PrefabValues;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabvalues.factories.PrefabValueFactory;
import equalsverifier.prefabvalues.factoryproviders.JavaFxFactoryProvider.PropertyFactory;
import equalsverifier.testhelpers.types.Point;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("rawtypes")
public class JavaFxFactoryProviderTest {
    private PrefabAbstract prefabAbstract;

    @Before
    public void setUp() {
        prefabAbstract = new PrefabValues(JavaApiPrefabValues.build());
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
        Tuple<GenericContainer> tuple = factory.createValues(tag, prefabAbstract, null);

        assertEquals(prefabAbstract.giveRed(listTag), tuple.getRed().t);
        assertEquals(prefabAbstract.giveBlack(listTag), tuple.getBlack().t);
        assertEquals(String.class, tuple.getRed().t.get(0).getClass());
    }

    @Test
    public void createInstancesWithCorrectMultipleGenericParameter() {
        TypeTag tag = new TypeTag(GenericMultiContainer.class, new TypeTag(String.class), new TypeTag(Point.class));
        TypeTag mapTag = new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Point.class));

        PrefabValueFactory<GenericMultiContainer> factory =
            new PropertyFactory<>(GenericMultiContainer.class.getName(), Map.class);
        Tuple<GenericMultiContainer> tuple = factory.createValues(tag, prefabAbstract, null);

        assertEquals(prefabAbstract.giveRed(mapTag), tuple.getRed().t);
        assertEquals(prefabAbstract.giveBlack(mapTag), tuple.getBlack().t);

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
