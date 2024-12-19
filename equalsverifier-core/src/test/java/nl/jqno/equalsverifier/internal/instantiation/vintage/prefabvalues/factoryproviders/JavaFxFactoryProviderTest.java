package nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factoryproviders;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import nl.jqno.equalsverifier.internal.instantiation.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factoryproviders.JavaFxFactoryProvider.PropertyFactory;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

@SuppressWarnings({ "rawtypes", "unchecked" })
class JavaFxFactoryProviderTest {

    private VintageValueProvider valueProvider;

    @BeforeEach
    void setUp() {
        valueProvider = new VintageValueProvider(JavaApiPrefabValues.build(), new ObjenesisStd());
    }

    @Test
    void maintainCoverageOnJdksThatDontHaveJavafx() {
        assertThat(new JavaFxFactoryProvider().getFactoryCache()).isNotNull();
    }

    @Test
    void createInstancesWithCorrectSingleGenericParameter() {
        TypeTag tag = new TypeTag(GenericContainer.class, new TypeTag(String.class));
        TypeTag listTag = new TypeTag(List.class, new TypeTag(String.class));

        PrefabValueFactory<GenericContainer> factory =
                new PropertyFactory<>(GenericContainer.class.getName(), List.class);
        Tuple<GenericContainer> tuple = factory.createValues(tag, valueProvider, null);

        assertThat(tuple.getRed().t).isEqualTo(valueProvider.giveRed(listTag));
        assertThat(tuple.getBlue().t).isEqualTo(valueProvider.giveBlue(listTag));
        assertThat(tuple.getRed().t.get(0).getClass()).isEqualTo(String.class);
    }

    @Test
    void createInstancesWithCorrectMultipleGenericParameter() {
        TypeTag tag = new TypeTag(GenericMultiContainer.class, new TypeTag(String.class), new TypeTag(Point.class));
        TypeTag mapTag = new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Point.class));

        PrefabValueFactory<GenericMultiContainer> factory =
                new PropertyFactory<>(GenericMultiContainer.class.getName(), Map.class);
        Tuple<GenericMultiContainer> tuple = factory.createValues(tag, valueProvider, null);

        assertThat(tuple.getRed().t).isEqualTo(valueProvider.giveRed(mapTag));
        assertThat(tuple.getBlue().t).isEqualTo(valueProvider.giveBlue(mapTag));

        Map.Entry next = (Map.Entry) tuple.getRed().t.entrySet().iterator().next();
        assertThat(next.getKey().getClass()).isEqualTo(String.class);
        assertThat(next.getValue().getClass()).isEqualTo(Point.class);
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
