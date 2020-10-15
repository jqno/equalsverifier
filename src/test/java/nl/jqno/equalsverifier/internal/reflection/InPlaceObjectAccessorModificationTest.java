package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import org.junit.Before;
import org.junit.Test;

public class InPlaceObjectAccessorModificationTest {
    private static final int INITIAL_INT = 42;
    private static final boolean INITIAL_BOOLEAN = true;
    private static final String INITIAL_STRING = "hello";
    private static final Object INITIAL_OBJECT = new Object();

    private PrefabValues prefabValues;
    private Modifiable m;
    private ObjectAccessor<Modifiable> accessor;
    private Field stringField;

    @Before
    public void setUp() throws Exception {
        prefabValues = new PrefabValues(JavaApiPrefabValues.build());
        m = new Modifiable(INITIAL_INT, INITIAL_BOOLEAN, INITIAL_STRING, INITIAL_OBJECT);
        accessor = new InPlaceObjectAccessor<>(m, Modifiable.class);
        stringField = Modifiable.class.getDeclaredField("s");
    }

    @Test
    public void clearClears() {
        accessor.clear(f -> true, prefabValues, TypeTag.NULL);
        assertEquals(0, m.i);
        assertEquals(false, m.b);
        assertNull(m.s);
        assertNull(m.o);
    }

    @Test
    public void clearClearsExcept() {
        accessor.clear(f -> !f.getName().equals("s"), prefabValues, TypeTag.NULL);
        assertEquals(prefabValues.giveRed(new TypeTag(String.class)), m.s);
        assertEquals(0, m.i);
        assertEquals(false, m.b);
        assertNull(m.o);
    }

    @Test
    public void clearWorksInPlace() {
        ObjectAccessor<Modifiable> modified = accessor.clear(f -> true, prefabValues, TypeTag.NULL);
        assertSame(m, modified.get());
    }

    @Test
    public void withDefaultedField() {
        accessor.withDefaultedField(stringField);
        assertNull(m.s);
        assertEquals(INITIAL_INT, m.i);
        assertEquals(INITIAL_BOOLEAN, m.b);
        assertEquals(INITIAL_OBJECT, m.o);
    }

    @Test
    public void withDefaultedFieldWorksInPlace() {
        ObjectAccessor<Modifiable> modified = accessor.withDefaultedField(stringField);
        assertSame(m, modified.get());
    }

    @Test
    public void withChangedField() {
        accessor.withChangedField(stringField, prefabValues, TypeTag.NULL);
        assertEquals(prefabValues.giveRed(new TypeTag(String.class)), m.s);
        assertEquals(INITIAL_INT, m.i);
        assertEquals(INITIAL_BOOLEAN, m.b);
        assertEquals(INITIAL_OBJECT, m.o);
    }

    @Test
    public void withChangedFieldWorksInPlace() {
        ObjectAccessor<Modifiable> modified =
                accessor.withChangedField(stringField, prefabValues, TypeTag.NULL);
        assertSame(m, modified.get());
    }

    @Test
    public void withFieldSetTo() {
        accessor.withFieldSetTo(stringField, "something else");
        assertEquals("something else", m.s);
        assertEquals(INITIAL_INT, m.i);
        assertEquals(INITIAL_BOOLEAN, m.b);
        assertEquals(INITIAL_OBJECT, m.o);
    }

    @Test
    public void withFieldSetToWorksInPlace() {
        ObjectAccessor<Modifiable> modified =
                accessor.withFieldSetTo(stringField, "something else");
        assertSame(m, modified.get());
    }

    static class Modifiable {
        private final int i;
        private final boolean b;
        private final String s;
        private final Object o;

        public Modifiable(int i, boolean b, String s, Object o) {
            this.i = i;
            this.b = b;
            this.s = s;
            this.o = o;
        }
    }
}
