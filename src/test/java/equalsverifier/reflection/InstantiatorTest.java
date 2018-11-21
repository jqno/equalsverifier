package equalsverifier.reflection;

import equalsverifier.testhelpers.types.ColorBlindColorPoint;
import equalsverifier.testhelpers.types.FinalPoint;
import equalsverifier.testhelpers.types.Point;
import equalsverifier.testhelpers.types.TypeHelper.AbstractClass;
import equalsverifier.testhelpers.types.TypeHelper.ArrayContainer;
import equalsverifier.testhelpers.types.TypeHelper.Interface;
import org.junit.Test;
import org.w3c.dom.Element;

import java.util.List;

import static org.junit.Assert.*;

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
}
