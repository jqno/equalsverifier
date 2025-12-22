package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import nl.jqno.equalsverifier_testhelpers.types.ColorBlindColorPoint;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.AbstractClass;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.ArrayContainer;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.Interface;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.w3c.dom.Element;

class InstantiatorTest {

    private final Objenesis objenesis = new ObjenesisStd();

    @Test
    void getType() {
        Instantiator<Point> instantiator = Instantiator.of(Point.class, objenesis);
        Class<Point> type = instantiator.getType();
        assertThat(type).isEqualTo(Point.class);
    }

    @Test
    void getType_sealedAbstract() {
        Instantiator<SealedAbstract> instantiator = Instantiator.of(SealedAbstract.class, objenesis);
        Class<SealedAbstract> type = instantiator.getType();
        assertThat(type).isEqualTo(SealedAbstractSub.class);
    }

    sealed static abstract class SealedAbstract permits SealedAbstractSub {}

    static final class SealedAbstractSub extends SealedAbstract {}

    @Test
    void getType_sealedNonAbstract() {
        Instantiator<SealedNonAbstract> instantiator = Instantiator.of(SealedNonAbstract.class, objenesis);
        Class<SealedNonAbstract> type = instantiator.getType();
        assertThat(type).isEqualTo(SealedNonAbstract.class);
    }

    sealed static class SealedNonAbstract permits SealedNonAbstractSub {}

    static final class SealedNonAbstractSub extends SealedNonAbstract {}

    @Test
    void instantiateClass() {
        Instantiator<Point> instantiator = Instantiator.of(Point.class, objenesis);
        Point p = instantiator.instantiate();
        assertThat(p.getClass()).isEqualTo(Point.class);
    }

    @Test
    void fieldsOfInstantiatedObjectHaveDefaultValues() {
        ColorBlindColorPoint p = Instantiator.of(ColorBlindColorPoint.class, objenesis).instantiate();
        assertThat(p.x).isEqualTo(0);
        assertThat(p.color).isNull();
    }

    @Test
    void instantiateInterface() {
        Instantiator<Interface> instantiator = Instantiator.of(Interface.class, objenesis);
        Interface i = instantiator.instantiate();
        assertThat(Interface.class.isAssignableFrom(i.getClass())).isTrue();
    }

    @Test
    void instantiateFinalClass() {
        Instantiator.of(FinalPoint.class, objenesis);
    }

    @Test
    void instantiateArrayContainer() {
        Instantiator.of(ArrayContainer.class, objenesis);
    }

    @Test
    void instantiateAbstractClass() {
        Instantiator<AbstractClass> instantiator = Instantiator.of(AbstractClass.class, objenesis);
        AbstractClass ac = instantiator.instantiate();
        assertThat(AbstractClass.class.isAssignableFrom(ac.getClass())).isTrue();
    }

    @Test
    void instantiateSubclass() {
        Point p = instantiateSub(Point.class);
        assertThat(p.getClass() == Point.class).isFalse();
        assertThat(Point.class.isAssignableFrom(p.getClass())).isTrue();
    }

    @Test
    void instantiateAnNonToplevelClass() {
        class Something {}
        instantiateSub(Something.class);
    }

    @Test
    void instantiateJavaApiClassWhichHasBootstrapClassLoader() {
        instantiateSub(List.class);
    }

    @Test
    void instantiateOrgW3cDomClassWhichHasBootstrapClassLoader() {
        instantiateSub(Element.class);
    }

    @Test
    void instantiateTheSameSubclass() {
        Class<?> expected = instantiateSub(Point.class).getClass();
        Class<?> actual = instantiateSub(Point.class).getClass();
        assertThat(actual).isEqualTo(expected);
    }

    private <T> T instantiateSub(Class<T> type) {
        Class<T> sub = SubtypeManager.giveDynamicSubclass(type);
        var object = Instantiator.of(sub, objenesis).instantiate();
        assertThat(object).isNotNull();
        assertThat(object.getClass()).isAssignableTo(type);
        return object;
    }
}
