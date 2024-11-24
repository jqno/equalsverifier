package nl.jqno.equalsverifier.internal.reflection.vintage;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.Objects;
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

public class SealedTypesFallbackFactoryTest {

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
    public void redCopyHasTheSameValuesAsRed_whenSutIsAbstractSealedAndPermittedTypeAddsField() {
        Tuple<?> tuple = factory.createValues(
            new TypeTag(SealedParentWithFinalChild.class),
            valueProvider,
            attributes
        );

        assertEquals(tuple.getRed(), tuple.getRedCopy());
        assertNotSame(tuple.getRed(), tuple.getRedCopy());
    }

    public abstract static sealed class SealedParentWithFinalChild permits FinalSealedChild {

        private final int i;

        public SealedParentWithFinalChild(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SealedParentWithFinalChild other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    public static final class FinalSealedChild extends SealedParentWithFinalChild {

        private final int j;

        public FinalSealedChild(int i, int j) {
            super(i);
            this.j = j;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FinalSealedChild other && super.equals(obj) && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), j);
        }
    }
}
