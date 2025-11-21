package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.Objects;

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

class SealedTypesFallbackFactoryTest {

    private FallbackFactory<?> factory;
    private VintageValueProvider valueProvider;
    private LinkedHashSet<TypeTag> typeStack;

    @BeforeEach
    void setUp() {
        Objenesis objenesis = new ObjenesisStd();
        factory = new FallbackFactory<>(objenesis);
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(int.class, values(42, 1337, 42));
        var prefabs = new UserPrefabValueCaches();
        valueProvider = new VintageValueProvider(new UserPrefabValueProvider(prefabs), factoryCache, objenesis);
        typeStack = new LinkedHashSet<>();
    }

    @Test
    void redCopyHasTheSameValuesAsRed_whenSutIsAbstractSealedAndPermittedTypeAddsField() {
        Tuple<?> tuple = factory.createValues(new TypeTag(SealedParentWithFinalChild.class), valueProvider, typeStack);

        assertThat(tuple.redCopy()).isEqualTo(tuple.red());
        assertThat(tuple.redCopy()).isNotSameAs(tuple.red());
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
