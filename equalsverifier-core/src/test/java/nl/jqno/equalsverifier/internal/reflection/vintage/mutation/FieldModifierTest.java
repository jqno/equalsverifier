package nl.jqno.equalsverifier.internal.reflection.vintage.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.ObjectContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.PrimitiveContainer;
import org.junit.jupiter.api.Test;

public class FieldModifierTest {

    private static final String FIELD_NAME = "field";

    @Test
    public void copyToPrimitiveField() {
        int value = 10;

        PrimitiveContainer from = new PrimitiveContainer();
        from.field = value;

        PrimitiveContainer to = new PrimitiveContainer();
        doCopyField(to, from, FIELD_NAME);

        assertEquals(value, to.field);
    }

    @Test
    public void copyToObjectField() {
        Object value = new Object();

        ObjectContainer from = new ObjectContainer();
        from.field = value;

        ObjectContainer to = new ObjectContainer();
        doCopyField(to, from, FIELD_NAME);

        assertSame(value, to.field);
    }

    private void doCopyField(Object to, Object from, String fieldName) {
        getAccessorFor(from, fieldName).copyTo(to);
    }

    private FieldModifier getAccessorFor(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return FieldModifier.of(field, object);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldName: " + fieldName);
        }
    }
}
