package nl.jqno.equalsverifier.internal.reflection.vintage;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.CachedValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.FallbackFactory;
import nl.jqno.equalsverifier.internal.testhelpers.TestValueProviders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class RecordFallbackFactoryTest {

    private FallbackFactory<?> factory;
    private VintageValueProvider valueProvider;
    private Attributes attributes;

    @BeforeEach
    public void setUp() {
        Objenesis objenesis = new ObjenesisStd();
        factory = new FallbackFactory<>(objenesis);
        CachedValueProvider cache = new CachedValueProvider();
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(int.class, values(42, 1337, 42));
        valueProvider =
            new VintageValueProvider(TestValueProviders.empty(), cache, factoryCache, objenesis);
        attributes = Attributes.unlabeled();
    }

    @Test
    public void redCopyHasTheSameValuesAsRed_whenSutContainsGenericValueThatNeedsToBeIdenticalInRedAndRedCopy() {
        Tuple<?> tuple = factory.createValues(
            new TypeTag(GenericRecordContainer.class),
            valueProvider,
            attributes
        );

        assertEquals(tuple.getRed(), tuple.getRedCopy());
        assertNotSame(tuple.getRed(), tuple.getRedCopy());
    }

    record GenericRecord<T>(T t) {}

    record GenericRecordContainer(GenericRecord<?> bgr) {}
}
