package nl.jqno.equalsverifier.internal.reflection.vintage;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.reflection.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.FallbackFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class RecordFallbackFactoryTest {

    private FallbackFactory<?> factory;
    private VintageValueProvider valueProvider;
    private LinkedHashSet<TypeTag> typeStack;

    @BeforeEach
    public void setUp() {
        Objenesis objenesis = new ObjenesisStd();
        factory = new FallbackFactory<>(objenesis);
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(int.class, values(42, 1337, 42));
        valueProvider = new VintageValueProvider(factoryCache, objenesis);
        typeStack = new LinkedHashSet<>();
    }

    @Test
    public void redCopyHasTheSameValuesAsRed_whenSutContainsGenericValueThatNeedsToBeIdenticalInRedAndRedCopy() {
        Tuple<?> tuple = factory.createValues(
            new TypeTag(GenericRecordContainer.class),
            valueProvider,
            typeStack
        );

        assertEquals(tuple.getRed(), tuple.getRedCopy());
        assertNotSame(tuple.getRed(), tuple.getRedCopy());
    }

    record GenericRecord<T>(T t) {}

    record GenericRecordContainer(GenericRecord<?> bgr) {}
}
