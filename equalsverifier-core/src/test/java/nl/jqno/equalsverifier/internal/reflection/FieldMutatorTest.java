package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class FieldMutatorTest {

    private FieldProbe p;
    private FieldMutator sut;
    private Container o = new Container();

    @Test
    void setPrimitive() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("i"));
        sut = new FieldMutator(p);

        assertThat(o.i).isEqualTo(10);
        sut.setNewValue(o, 1337);
        assertThat(o.i).isEqualTo(1337);
    }

    @Test
    void setObject() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("s"));
        sut = new FieldMutator(p);

        assertThat(o.s).isEqualTo("NON-FINAL");
        sut.setNewValue(o, "changed");
        assertThat(o.s).isEqualTo("changed");
    }

    @Test
    void dontSetConstantPrimitive() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("FINAL_INT"));
        sut = new FieldMutator(p);

        assertThat(Container.FINAL_INT).isEqualTo(42);
        sut.setNewValue(o, 1337);
        assertThat(Container.FINAL_INT).isEqualTo(42);
    }

    @Test
    void dontSetConstantObject() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("FINAL_STRING"));
        sut = new FieldMutator(p);

        assertThat(Container.FINAL_STRING).isEqualTo("FINAL");
        sut.setNewValue(o, "changed");
        assertThat(Container.FINAL_STRING).isEqualTo("FINAL");
    }

    @Test
    @SuppressWarnings("rawtypes")
    void throwsReflectionException_whenClassHasASelfReferenceGenericParameter() throws NoSuchFieldException {
        var obj = new SelfReferringGenericType();
        p = FieldProbe.of(SelfReferringGenericType.class.getDeclaredField("wrapped"));
        sut = new FieldMutator(p);

        ExpectedException
                .when(() -> sut.setNewValue(obj, new Object()))
                .assertThrows(ReflectionException.class)
                .assertMessageContains("try adding a prefab value");
    }

    static class Container {

        private static final int FINAL_INT = 42;
        private static final String FINAL_STRING = "FINAL";
        private final int i;
        private final String s;

        public Container() {
            this.i = 10;
            this.s = "NON-FINAL";
        }
    }

    @SuppressWarnings("unused")
    static class SelfReferringGenericType<T extends SelfReferringGenericType<T>> {
        private T wrapped;
    }
}
