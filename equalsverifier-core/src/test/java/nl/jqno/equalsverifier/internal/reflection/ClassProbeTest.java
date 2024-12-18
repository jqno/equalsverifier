package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.*;

import nl.jqno.equalsverifier.testhelpers.types.ColorPoint3D;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractEqualsAndHashCode;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Empty;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.NoFieldsSubWithFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassProbeTest {

    private ClassProbe<PointContainer> pointProbe;
    private ClassProbe<AbstractEqualsAndHashCode> abstractProbe;

    @BeforeEach
    public void setup() {
        pointProbe = ClassProbe.of(PointContainer.class);
        abstractProbe = ClassProbe.of(AbstractEqualsAndHashCode.class);
    }

    @Test
    public void getType() {
        assertSame(PointContainer.class, pointProbe.getType());
    }

    /* Tests the false case. The true case is tested in {@link ClassProbeCompilerTest}. */
    @Test
    public void isRecord() {
        assertFalse(pointProbe.isRecord());
    }

    /* Tests the false case. The true case is tested in {@link ClassProbeSealedTest}. */
    @Test
    public void isSealed() {
        assertFalse(pointProbe.isSealed());
    }

    @Test
    public void declaresEquals() {
        assertTrue(pointProbe.declaresEquals());
        assertTrue(abstractProbe.declaresEquals());
    }

    @Test
    public void doesNotDeclareEquals() {
        ClassProbe<?> accessor = ClassProbe.of(Empty.class);
        assertFalse(accessor.declaresEquals());
    }

    @Test
    public void declaresHashCode() {
        assertTrue(pointProbe.declaresHashCode());
        assertTrue(abstractProbe.declaresHashCode());
    }

    @Test
    public void doesNotDeclareHashCode() {
        ClassProbe<?> accessor = ClassProbe.of(Empty.class);
        assertFalse(accessor.declaresHashCode());
    }

    @Test
    public void hasMethod() {
        ClassProbe<?> accessor = ClassProbe.of(MethodContainer.class);
        assertTrue(accessor.hasMethod("m"));
    }

    @Test
    public void hasProtectedMethod() {
        ClassProbe<?> accessor = ClassProbe.of(MethodContainer.class);
        assertTrue(accessor.hasMethod("m_protected"));
    }

    @Test
    public void hasMethodInSuper() {
        ClassProbe<?> accessor = ClassProbe.of(ChildOfMethodContainer.class);
        assertTrue(accessor.hasMethod("m"));
    }

    @Test
    public void hasProtectedMethodInSuper() {
        ClassProbe<?> accessor = ClassProbe.of(ChildOfMethodContainer.class);
        assertTrue(accessor.hasMethod("m_protected"));
    }

    @Test
    public void doesNotHaveMethod() {
        ClassProbe<?> accessor = ClassProbe.of(MethodContainer.class);
        assertFalse(accessor.hasMethod("doesNotExist"));
    }

    @Test
    public void equalsIsNotAbstract() {
        assertFalse(pointProbe.isEqualsAbstract());
    }

    @Test
    public void equalsIsAbstract() {
        assertTrue(abstractProbe.isEqualsAbstract());
    }

    @Test
    public void hashCodeIsNotAbstract() {
        assertFalse(pointProbe.isHashCodeAbstract());
    }

    @Test
    public void hashCodeIsAbstract() {
        assertTrue(abstractProbe.isHashCodeAbstract());
    }

    @Test
    public void equalsIsInheritedFromObject() {
        ClassProbe<NoFieldsSubWithFields> accessor = ClassProbe.of(NoFieldsSubWithFields.class);
        assertTrue(accessor.isEqualsInheritedFromObject());
    }

    @Test
    public void equalsIsNotInheritedFromObject() {
        assertFalse(pointProbe.isEqualsInheritedFromObject());
    }

    @Test
    public void getSuperAccessorForPojo() {
        ClassProbe<? super PointContainer> superAccessor = pointProbe.getSuperProbe();
        assertEquals(Object.class, superAccessor.getType());
    }

    @Test
    public void getSuperAccessorInHierarchy() {
        ClassProbe<ColorPoint3D> accessor = ClassProbe.of(ColorPoint3D.class);
        ClassProbe<? super ColorPoint3D> superAccessor = accessor.getSuperProbe();
        assertEquals(Point3D.class, superAccessor.getType());
    }

    static class MethodContainer {

        public void m() {}

        protected void m_protected() {}
    }

    static class ChildOfMethodContainer extends MethodContainer {}
}
