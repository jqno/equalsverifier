package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class RecordObjectAccessorModificationTest {
    private static final int INITIAL_INT = 42;
    private static final boolean INITIAL_BOOLEAN = true;
    private static final String INITIAL_STRING = "hello";
    private static final Object INITIAL_OBJECT = new Object();

    private PrefabValues prefabValues;
    private Class<?> modifiable;
    private Object m;
    private ObjectAccessor<?> accessor;
    private Field stringField;
    private ObjectAccessor<?> modified;

    @BeforeEach
    public void setUp() throws Exception {
        prefabValues = new PrefabValues(JavaApiPrefabValues.build());

        modifiable = ModifiableRecord.class;
        Constructor<?> c =
                modifiable.getDeclaredConstructor(
                        int.class, boolean.class, String.class, Object.class);
        m = c.newInstance(INITIAL_INT, INITIAL_BOOLEAN, INITIAL_STRING, INITIAL_OBJECT);
        accessor = create(m);
        stringField = modifiable.getDeclaredField("s");
    }

    @Test
    public void clearClears() throws Exception {
        modified = accessor.clear(f -> true, prefabValues, TypeTag.NULL);
        assertEquals(0, fieldValue(modified, "i"));
        assertEquals(false, fieldValue(modified, "b"));
        assertNull(fieldValue(modified, "s"));
        assertNull(fieldValue(modified, "o"));
    }

    @Test
    public void clearClearsExcept() throws Exception {
        modified = accessor.clear(f -> !f.getName().equals("s"), prefabValues, TypeTag.NULL);
        assertEquals(prefabValues.giveRed(new TypeTag(String.class)), fieldValue(modified, "s"));
        assertEquals(0, fieldValue(modified, "i"));
        assertEquals(false, fieldValue(modified, "b"));
        assertNull(fieldValue(modified, "o"));
    }

    @Test
    public void clearLeavesOriginalUnaffected() {
        modified = accessor.clear(f -> true, prefabValues, TypeTag.NULL);
        assertNotSame(m, modified.get());
    }

    @Test
    public void withDefaultedField() throws Exception {
        modified = accessor.withDefaultedField(stringField);
        assertNull(fieldValue(modified, "s"));
        assertEquals(INITIAL_INT, fieldValue(modified, "i"));
        assertEquals(INITIAL_BOOLEAN, fieldValue(modified, "b"));
        assertEquals(INITIAL_OBJECT, fieldValue(modified, "o"));
    }

    @Test
    public void withDefaultedFieldLeavesOriginalUnaffected() {
        modified = accessor.withDefaultedField(stringField);
        assertNotSame(m, modified.get());
    }

    @Test
    public void withChangedField() throws Exception {
        modified = accessor.withChangedField(stringField, prefabValues, TypeTag.NULL);
        assertEquals(prefabValues.giveRed(new TypeTag(String.class)), fieldValue(modified, "s"));
        assertEquals(INITIAL_INT, fieldValue(modified, "i"));
        assertEquals(INITIAL_BOOLEAN, fieldValue(modified, "b"));
        assertEquals(INITIAL_OBJECT, fieldValue(modified, "o"));
    }

    @Test
    public void withChangedFieldLeavesOriginalUnaffected() {
        modified = accessor.withChangedField(stringField, prefabValues, TypeTag.NULL);
        assertNotSame(m, modified.get());
    }

    @Test
    public void withFieldSetTo() throws Exception {
        modified = accessor.withFieldSetTo(stringField, "something else");
        assertEquals("something else", fieldValue(modified, "s"));
        assertEquals(INITIAL_INT, fieldValue(modified, "i"));
        assertEquals(INITIAL_BOOLEAN, fieldValue(modified, "b"));
        assertEquals(INITIAL_OBJECT, fieldValue(modified, "o"));
    }

    @Test
    public void withFieldSetToLeavesOriginalUnaffected() {
        modified = accessor.withFieldSetTo(stringField, "something else");
        assertNotSame(m, modified.get());
    }

    @SuppressWarnings("unchecked")
    private <T> RecordObjectAccessor<T> create(T object) {
        return new RecordObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private Object fieldValue(ObjectAccessor<?> objectAccessor, String fieldName)
            throws NoSuchFieldException {
        Field field = objectAccessor.get().getClass().getDeclaredField(fieldName);
        return objectAccessor.getField(field);
    }

    record ModifiableRecord(int i, boolean b, String s, Object o) {}
}
