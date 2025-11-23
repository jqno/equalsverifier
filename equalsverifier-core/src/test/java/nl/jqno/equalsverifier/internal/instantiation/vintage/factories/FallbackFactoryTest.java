package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueCaches;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.TwoStepNodeA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class FallbackFactoryTest {

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
    void dontGiveRecursiveClass() {
        ExpectedException
                .when(() -> factory.createValues(new TypeTag(Node.class), valueProvider, typeStack))
                .assertThrows(RecursionException.class);
    }

    @Test
    void dontGiveTwoStepRecursiveClass() {
        assertThatThrownBy(() -> factory.createValues(new TypeTag(TwoStepNodeA.class), valueProvider, typeStack))
                .isInstanceOf(RecursionException.class)
                .extracting(e -> ((MessagingException) e).getDescription())
                .asString()
                .contains("TwoStepNodeA")
                .contains("TwoStepNodeB");
    }
}
