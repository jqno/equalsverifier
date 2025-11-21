package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueCaches;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class RecordFallbackFactoryTest {

    private FallbackFactory<?> factory;
    private VintageValueProvider valueProvider;
    private LinkedHashSet<TypeTag> typeStack;

    @BeforeEach
    void setUp() {
        Objenesis objenesis = new ObjenesisStd();
        factory = new FallbackFactory<>(objenesis);
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(int.class, values(42, 1337, 42));
        var prefabs = new UserPrefabValueProvider(new UserPrefabValueCaches());
        valueProvider = new VintageValueProvider(prefabs, factoryCache, objenesis);
        typeStack = new LinkedHashSet<>();
    }

    @Test
    void redCopyHasTheSameValuesAsRed_whenSutContainsGenericValueThatNeedsToBeIdenticalInRedAndRedCopy() {
        Tuple<?> tuple = factory.createValues(new TypeTag(GenericRecordContainer.class), valueProvider, typeStack);

        assertThat(tuple.redCopy()).isEqualTo(tuple.red());
        assertThat(tuple.redCopy()).isNotSameAs(tuple.red());
    }

    record GenericRecord<T>(T t) {}

    record GenericRecordContainer(GenericRecord<?> bgr) {}
}
