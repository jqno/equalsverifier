package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import nl.jqno.equalsverifier.testhelpers.types.ColorBlindColorPoint;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractClass;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.ArrayContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Interface;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

public class InstantiatorTest {

    @Test
    public void instantiateClass() {
        Instantiator<Point> instantiator = Instantiator.of(Point.class);
        Point p = instantiator.instantiate();
        assertEquals(Point.class, p.getClass());
    }

    @Test
    public void fieldsOfInstantiatedObjectHaveDefaultValues() {
        ColorBlindColorPoint p = Instantiator.of(ColorBlindColorPoint.class).instantiate();
        assertEquals(0, p.x);
        assertEquals(null, p.color);
    }

    @Test
    public void instantiateInterface() {
        Instantiator<Interface> instantiator = Instantiator.of(Interface.class);
        Interface i = instantiator.instantiate();
        assertTrue(Interface.class.isAssignableFrom(i.getClass()));
    }

    @Test
    public void instantiateFinalClass() {
        Instantiator.of(FinalPoint.class);
    }

    @Test
    public void instantiateArrayContainer() {
        Instantiator.of(ArrayContainer.class);
    }

    @Test
    public void instantiateAbstractClass() {
        Instantiator<AbstractClass> instantiator = Instantiator.of(AbstractClass.class);
        AbstractClass ac = instantiator.instantiate();
        assertTrue(AbstractClass.class.isAssignableFrom(ac.getClass()));
    }

    @Test
    public void instantiateSubclass() {
        Instantiator<Point> instantiator = Instantiator.of(Point.class);
        Point p = instantiator.instantiateAnonymousSubclass();
        assertFalse(p.getClass() == Point.class);
        assertTrue(Point.class.isAssignableFrom(p.getClass()));
    }

    @Test
    public void instantiateAnNonToplevelClass() {
        class Something {}
        Instantiator<Something> instantiator = Instantiator.of(Something.class);
        instantiator.instantiateAnonymousSubclass();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void instantiateJavaApiClassWhichHasBootstrapClassLoader() {
        Instantiator instantiator = Instantiator.of(List.class);
        instantiator.instantiateAnonymousSubclass();
    }

    @Test
    public void instantiateOrgW3cDomClassWhichHasBootstrapClassLoader() {
        Instantiator<Element> instantiator = Instantiator.of(Element.class);
        instantiator.instantiateAnonymousSubclass();
    }

    @Test
    public void instantiateTheSameSubclass() {
        Instantiator<Point> instantiator = Instantiator.of(Point.class);
        Class<?> expected = instantiator.instantiateAnonymousSubclass().getClass();
        Class<?> actual = instantiator.instantiateAnonymousSubclass().getClass();
        assertEquals(expected, actual);
    }

    @Test
    public void giveDynamicSubclass() throws Exception {
        class Super {}
        Class<?> sub = Instantiator.giveDynamicSubclass(
            Super.class,
            "dynamicField",
            b -> b.defineField("dynamicField", int.class, Visibility.PRIVATE)
        );
        Field f = sub.getDeclaredField("dynamicField");
        assertNotNull(f);
    }

    @Test
    public void giveDynamicSubclassForClassWithNoPackage() {
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
