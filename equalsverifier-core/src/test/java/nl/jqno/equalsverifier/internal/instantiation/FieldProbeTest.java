package nl.jqno.equalsverifier.internal.instantiation;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.ConfigurationHelper;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Test;

public class FieldProbeTest {

    private static final String FIELD_NAME = "field";

    @Test
    public void getField() throws NoSuchFieldException {
        ObjectContainer foo = new ObjectContainer();
        Field field = foo.getClass().getDeclaredField(FIELD_NAME);
        FieldProbe probe = FieldProbe.of(
            field,
            ConfigurationHelper.emptyConfiguration(ObjectContainer.class)
        );
        assertSame(field, probe.getField());
    }

    @Test
    public void getType() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertEquals(Object.class, probe.getType());
    }

    @Test
    public void getName() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertEquals(FIELD_NAME, probe.getName());
    }

    @Test
    public void isNotPrimitive() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertFalse(probe.fieldIsPrimitive());
    }

    @Test
    public void isPrimitive() {
        PrimitiveContainer foo = new PrimitiveContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertTrue(probe.fieldIsPrimitive());
    }

    @Test
    public void isNotStatic() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertFalse(probe.fieldIsStatic());
    }

    @Test
    public void isStatic() {
        StaticContainer foo = new StaticContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertTrue(probe.fieldIsStatic());
    }

    @Test
    public void canBeDefault_forObject() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertTrue(probe.canBeDefault());
    }

    @Test
    public void canBeDefault_primitive() {
        PrimitiveContainer foo = new PrimitiveContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertTrue(probe.canBeDefault());
    }

    @Test
    public void canBeDefault_primitiveWithWarningSuppressed() {
        PrimitiveContainer foo = new PrimitiveContainer();
        Configuration<?> config = ConfigurationHelper.emptyConfiguration(
            foo.getClass(),
            Warning.ZERO_FIELDS
        );
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME, config);
        assertFalse(probe.canBeDefault());
    }

    @Test
    public void canBeDefault_isMentionedExplicitly() {
        ObjectContainer foo = new ObjectContainer();
        Configuration<?> config = ConfigurationHelper.emptyConfigurationWithNonnullFields(
            foo.getClass(),
            FIELD_NAME
        );
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME, config);
        assertFalse(probe.canBeDefault());
    }

    @Test
    public void canBeDefault_annotated() {
        NonNullContainer foo = new NonNullContainer();
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertFalse(probe.canBeDefault());
    }

    private FieldProbe getAccessorFor(Object object, String fieldName) {
        return getAccessorFor(
            object,
            fieldName,
            ConfigurationHelper.emptyConfiguration(object.getClass())
        );
    }

    private FieldProbe getAccessorFor(Object object, String fieldName, Configuration<?> config) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return FieldProbe.of(field, config);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldName: " + fieldName);
        }
    }
}
