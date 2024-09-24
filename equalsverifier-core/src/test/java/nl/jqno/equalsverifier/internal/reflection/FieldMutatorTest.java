package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FieldMutatorTest {

    private FieldProbe p;
    private FieldMutator sut;
    private Container o = new Container();

    @Test
    public void setPrimitive() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("i"));
        sut = new FieldMutator(p);

        assertEquals(10, o.i);
        sut.setNewValue(o, 1337);
        assertEquals(1337, o.i);
    }

    @Test
    public void setObject() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("s"));
        sut = new FieldMutator(p);

        assertEquals("NON-FINAL", o.s);
        sut.setNewValue(o, "changed");
        assertEquals("changed", o.s);
    }

    @Test
    public void dontSetConstantPrimitive() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("FINAL_INT"));
        sut = new FieldMutator(p);

        assertEquals(42, Container.FINAL_INT);
        sut.setNewValue(o, 1337);
        assertEquals(42, Container.FINAL_INT);
    }

    @Test
    public void dontSetConstantObject() throws NoSuchFieldException {
        p = FieldProbe.of(Container.class.getDeclaredField("FINAL_STRING"));
        sut = new FieldMutator(p);

        assertEquals("FINAL", Container.FINAL_STRING);
        sut.setNewValue(o, "changed");
        assertEquals("FINAL", Container.FINAL_STRING);
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
}
