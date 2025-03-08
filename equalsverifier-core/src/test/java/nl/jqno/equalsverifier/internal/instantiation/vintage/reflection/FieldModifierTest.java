package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.ObjectContainer;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.PrimitiveContainer;
import org.junit.jupiter.api.Test;

class FieldModifierTest {

    private static final String FIELD_NAME = "field";

    @Test
    void copyToPrimitiveField() {
        int value = 10;

        PrimitiveContainer from = new PrimitiveContainer();
        from.field = value;

        PrimitiveContainer to = new PrimitiveContainer();
        doCopyField(to, from, FIELD_NAME);

        assertThat(to.field).isEqualTo(value);
    }

    @Test
    void copyToObjectField() {
        Object value = new Object();

        ObjectContainer from = new ObjectContainer();
        from.field = value;

        ObjectContainer to = new ObjectContainer();
        doCopyField(to, from, FIELD_NAME);

        assertThat(to.field).isSameAs(value);
    }

    private void doCopyField(Object to, Object from, String fieldName) {
        getAccessorFor(from, fieldName).copyTo(to);
    }

    private FieldModifier getAccessorFor(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return FieldModifier.of(FieldProbe.of(field), object);
        }
        catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldName: " + fieldName);
        }
    }
}
