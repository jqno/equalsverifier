package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.ConfigurationHelper;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Test;

public class FieldProbeTest {

    private static final String FIELD_NAME = "field";
    private Configuration<?> config = ConfigurationHelper.emptyConfiguration(ObjectContainer.class);

    @Test
    public void getField() throws NoSuchFieldException {
        ObjectContainer foo = new ObjectContainer();
        Field field = foo.getClass().getDeclaredField(FIELD_NAME);
        FieldProbe probe = FieldProbe.of(field);
        assertSame(field, probe.getField());
    }

    @Test
    public void getType() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertEquals(Object.class, probe.getType());
    }

    @Test
    public void getName() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertEquals(FIELD_NAME, probe.getName());
    }

    @Test
    public void isNotPrimitive() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertFalse(probe.isPrimitive());
    }

    @Test
    public void isPrimitive() {
        PrimitiveContainer foo = new PrimitiveContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertTrue(probe.isPrimitive());
    }

    @Test
    public void isNotFinal() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertFalse(probe.isFinal());
    }

    @Test
    public void isFinal() {
        FinalContainer foo = new FinalContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertTrue(probe.isFinal());
    }

    @Test
    public void isNotStatic() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertFalse(probe.isStatic());
    }

    @Test
    public void isStatic() {
        StaticContainer foo = new StaticContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertTrue(probe.isStatic());
    }

    @Test
    public void isNotTransient() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertFalse(probe.isTransient());
    }

    @Test
    public void isTransient() {
        TransientContainer foo = new TransientContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertTrue(probe.isTransient());
    }

    @Test
    public void isNotEnum() {
        PrimitiveContainer foo = new PrimitiveContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertFalse(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void isEnumButNotSingleValue() {
        EnumContainer foo = new EnumContainer();
        FieldProbe probe = getProbeFor(foo, "twoElementEnum");
        assertFalse(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void isSingleValueEnum() {
        EnumContainer foo = new EnumContainer();
        FieldProbe probe = getProbeFor(foo, "oneElementEnum");
        assertTrue(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void isEmptyEnum() {
        EnumContainer foo = new EnumContainer();
        FieldProbe probe = getProbeFor(foo, "emptyEnum");
        assertTrue(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void canBeDefault_forObject() {
        ObjectContainer foo = new ObjectContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertTrue(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_primitive() {
        PrimitiveContainer foo = new PrimitiveContainer();
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertTrue(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_primitiveWithWarningSuppressed() {
        PrimitiveContainer foo = new PrimitiveContainer();
        config = ConfigurationHelper.emptyConfiguration(foo.getClass(), Warning.ZERO_FIELDS);
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertFalse(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_isMentionedExplicitly() {
        ObjectContainer foo = new ObjectContainer();
        config =
            ConfigurationHelper.emptyConfigurationWithNonnullFields(foo.getClass(), FIELD_NAME);
        FieldProbe probe = getAccessorFor(foo, FIELD_NAME);
        assertFalse(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_annotated() {
        NonNullContainer foo = new NonNullContainer();
        config =
            ConfigurationHelper.emptyConfigurationWithNonnullFields(foo.getClass(), FIELD_NAME);
        FieldProbe probe = getProbeFor(foo, FIELD_NAME);
        assertFalse(probe.canBeDefault(config));
    }

    private FieldProbe getProbeFor(Object object, String fieldName) {
        return getAccessorFor(object, fieldName);
    }

    private FieldProbe getAccessorFor(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return FieldProbe.of(field);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldName: " + fieldName);
        }
    }
}
