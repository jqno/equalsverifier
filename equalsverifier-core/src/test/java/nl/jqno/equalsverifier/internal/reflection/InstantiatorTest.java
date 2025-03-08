package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
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
        Instantiator<Point> instantiator = Instantiator.of(Point.class, objenesis);
        Point p = instantiator.instantiateAnonymousSubclass();
        assertThat(p.getClass() == Point.class).isFalse();
        assertThat(Point.class.isAssignableFrom(p.getClass())).isTrue();
    }

    @Test
    void instantiateAnNonToplevelClass() {
        class Something {}
        Instantiator<Something> instantiator = Instantiator.of(Something.class, objenesis);
        instantiator.instantiateAnonymousSubclass();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void instantiateJavaApiClassWhichHasBootstrapClassLoader() {
        Instantiator instantiator = Instantiator.of(List.class, objenesis);
        instantiator.instantiateAnonymousSubclass();
    }

    @Test
    void instantiateOrgW3cDomClassWhichHasBootstrapClassLoader() {
        Instantiator<Element> instantiator = Instantiator.of(Element.class, objenesis);
        instantiator.instantiateAnonymousSubclass();
    }

    @Test
    void instantiateTheSameSubclass() {
        Instantiator<Point> instantiator = Instantiator.of(Point.class, objenesis);
        Class<?> expected = instantiator.instantiateAnonymousSubclass().getClass();
        Class<?> actual = instantiator.instantiateAnonymousSubclass().getClass();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void giveDynamicSubclass() throws Exception {
        class Super {}
        Class<?> sub = Instantiator
                .giveDynamicSubclass(
                    Super.class,
                    "dynamicField",
                    b -> b.defineField("dynamicField", int.class, Visibility.PRIVATE));
        Field f = sub.getDeclaredField("dynamicField");
        assertThat(f).isNotNull();
    }

    @Test
    void giveDynamicSubclassForClassWithNoPackage() {
        Class<?> type = new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .subclass(Object.class)
                .name("NoPackage")
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        Instantiator.giveDynamicSubclass(type, "X", b -> b);
    }
}
