package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.testhelpers.types.ColorPoint3D;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractEqualsAndHashCode;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Empty;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.NoFieldsSubWithFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassProbeTest {

    private ClassProbe<PointContainer> pointProbe;
    private ClassProbe<AbstractEqualsAndHashCode> abstractProbe;

    @BeforeEach
    void setup() {
        pointProbe = ClassProbe.of(PointContainer.class);
        abstractProbe = ClassProbe.of(AbstractEqualsAndHashCode.class);
    }

    @Test
    void getType() {
        assertThat(pointProbe.getType()).isSameAs(PointContainer.class);
    }

    @Test
    void isNotRecord() {
        assertThat(pointProbe.isRecord()).isFalse();
    }

    @Test
    void isRecord() {
        record SimpleRecord(int i) {}
        assertThat(SimpleRecord.class.isRecord()).isTrue();
    }

    /* Tests the false case. The true case is tested in {@link ClassProbeSealedTest}. */
    @Test
    void isSealed() {
        assertThat(pointProbe.isSealed()).isFalse();
    }

    @Test
    void declaresEquals() {
        assertThat(pointProbe.declaresEquals()).isTrue();
        assertThat(abstractProbe.declaresEquals()).isTrue();
    }

    @Test
    void doesNotDeclareEquals() {
        ClassProbe<?> accessor = ClassProbe.of(Empty.class);
        assertThat(accessor.declaresEquals()).isFalse();
    }

    @Test
    void declaresHashCode() {
        assertThat(pointProbe.declaresHashCode()).isTrue();
        assertThat(abstractProbe.declaresHashCode()).isTrue();
    }

    @Test
    void doesNotDeclareHashCode() {
        ClassProbe<?> accessor = ClassProbe.of(Empty.class);
        assertThat(accessor.declaresHashCode()).isFalse();
    }

    @Test
    void hasMethod() {
        ClassProbe<?> accessor = ClassProbe.of(MethodContainer.class);
        assertThat(accessor.hasMethod("m")).isTrue();
    }

    @Test
    void hasProtectedMethod() {
        ClassProbe<?> accessor = ClassProbe.of(MethodContainer.class);
        assertThat(accessor.hasMethod("m_protected")).isTrue();
    }

    @Test
    void hasMethodInSuper() {
        ClassProbe<?> accessor = ClassProbe.of(ChildOfMethodContainer.class);
        assertThat(accessor.hasMethod("m")).isTrue();
    }

    @Test
    void hasProtectedMethodInSuper() {
        ClassProbe<?> accessor = ClassProbe.of(ChildOfMethodContainer.class);
        assertThat(accessor.hasMethod("m_protected")).isTrue();
    }

    @Test
    void doesNotHaveMethod() {
        ClassProbe<?> accessor = ClassProbe.of(MethodContainer.class);
        assertThat(accessor.hasMethod("doesNotExist")).isFalse();
    }

    @Test
    void equalsIsNotAbstract() {
        assertThat(pointProbe.isEqualsAbstract()).isFalse();
    }

    @Test
    void equalsIsAbstract() {
        assertThat(abstractProbe.isEqualsAbstract()).isTrue();
    }

    @Test
    void hashCodeIsNotAbstract() {
        assertThat(pointProbe.isHashCodeAbstract()).isFalse();
    }

    @Test
    void hashCodeIsAbstract() {
        assertThat(abstractProbe.isHashCodeAbstract()).isTrue();
    }

    @Test
    void equalsIsInheritedFromObject() {
        ClassProbe<NoFieldsSubWithFields> accessor = ClassProbe.of(NoFieldsSubWithFields.class);
        assertThat(accessor.isEqualsInheritedFromObject()).isTrue();
    }

    @Test
    void equalsIsNotInheritedFromObject() {
        assertThat(pointProbe.isEqualsInheritedFromObject()).isFalse();
    }

    @Test
    void getSuperAccessorForPojo() {
        ClassProbe<? super PointContainer> superAccessor = pointProbe.getSuperProbe();
        assertThat(superAccessor.getType()).isEqualTo(Object.class);
    }

    @Test
    void getSuperAccessorInHierarchy() {
        ClassProbe<ColorPoint3D> accessor = ClassProbe.of(ColorPoint3D.class);
        ClassProbe<? super ColorPoint3D> superAccessor = accessor.getSuperProbe();
        assertThat(superAccessor.getType()).isEqualTo(Point3D.class);
    }

    static class MethodContainer {

        public void m() {}

        protected void m_protected() {}
    }

    static class ChildOfMethodContainer extends MethodContainer {}
}
