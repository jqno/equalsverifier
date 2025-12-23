package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier_testhelpers.types.ColorBlindColorPoint;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.ArrayContainer;
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
    void instantiateFinalClass() {
        Instantiator.of(FinalPoint.class, objenesis);
    }

    @Test
    void instantiateArrayContainer() {
        Instantiator.of(ArrayContainer.class, objenesis);
    }

    @Test
    void instantiateANonToplevelClass() {
        class Something {}
        Something s = Instantiator.of(Something.class, objenesis).instantiate();
        assertThat(s.getClass()).isEqualTo(Something.class);
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
