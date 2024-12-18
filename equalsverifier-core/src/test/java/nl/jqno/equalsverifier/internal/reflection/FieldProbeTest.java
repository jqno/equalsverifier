package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.ConfigurationHelper;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Test;

/*
 * FieldProbe.isAnnotatedNonnull() is tested in AnnotationNonnullTest
 */
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
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertEquals(Object.class, probe.getType());
    }

    @Test
    public void getName() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertEquals(FIELD_NAME, probe.getName());
    }

    @Test
    public void isNotPublic() {
        FieldProbe probe = getProbeFor(DifferentAccessModifiersFieldContainer.class, "K");
        assertFalse(probe.isPublic());
    }

    @Test
    public void isPublic() {
        FieldProbe probe = getProbeFor(DifferentAccessModifiersFieldContainer.class, "L");
        assertTrue(probe.isPublic());
    }

    @Test
    public void isNotPrimitive() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertFalse(probe.isPrimitive());
    }

    @Test
    public void isPrimitive() {
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertTrue(probe.isPrimitive());
    }

    @Test
    public void isNotFinal() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertFalse(probe.isFinal());
    }

    @Test
    public void isFinal() {
        FieldProbe probe = getProbeFor(FinalContainer.class, FIELD_NAME);
        assertTrue(probe.isFinal());
    }

    @Test
    public void isNotStatic() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertFalse(probe.isStatic());
    }

    @Test
    public void isStatic() {
        FieldProbe probe = getProbeFor(StaticContainer.class, FIELD_NAME);
        assertTrue(probe.isStatic());
    }

    @Test
    public void isNotTransient() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertFalse(probe.isTransient());
    }

    @Test
    public void isTransient() {
        FieldProbe probe = getProbeFor(TransientContainer.class, FIELD_NAME);
        assertTrue(probe.isTransient());
    }

    @Test
    public void isNotEnum() {
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertFalse(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void isEnumButNotSingleValue() {
        FieldProbe probe = getProbeFor(EnumContainer.class, "twoElementEnum");
        assertFalse(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void isSingleValueEnum() {
        FieldProbe probe = getProbeFor(EnumContainer.class, "oneElementEnum");
        assertTrue(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void isEmptyEnum() {
        FieldProbe probe = getProbeFor(EnumContainer.class, "emptyEnum");
        assertTrue(probe.isEmptyOrSingleValueEnum());
    }

    @Test
    public void canBeDefault_forObject() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertTrue(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_primitive() {
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertTrue(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_primitiveWithPrefabbedField() {
        config = ConfigurationHelper.emptyConfigurationWithPrefabbedFields(PrimitiveContainer.class, FIELD_NAME);
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertFalse(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_isMentionedExplicitly() {
        config = ConfigurationHelper.emptyConfigurationWithNonnullFields(ObjectContainer.class, FIELD_NAME);
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertFalse(probe.canBeDefault(config));
    }

    @Test
    public void canBeDefault_annotated() {
        config = ConfigurationHelper.emptyConfigurationWithNonnullFields(NonNullContainer.class, FIELD_NAME);
        FieldProbe probe = getProbeFor(NonNullContainer.class, FIELD_NAME);
        assertFalse(probe.canBeDefault(config));
    }

    @Test
    public void canBeModifiedReflectively_synthetic() {
        FieldProbe probe = getProbeFor(NonStaticInner.class, "this$0");
        assertFalse(probe.canBeModifiedReflectively());
    }

    @Test
    public void canBeModifiedReflectively_staticFinal() {
        FieldProbe publicStaticFinal = getProbeFor(ModifierMix.class, "PUBLIC_STATIC_FINAL");
        assertFalse(publicStaticFinal.canBeModifiedReflectively());
    }

    @Test
    public void canBeModifiedReflectively_static() {
        FieldProbe publicStatic = getProbeFor(ModifierMix.class, "publicStatic");
        assertTrue(publicStatic.canBeModifiedReflectively());
    }

    @Test
    public void canBeModifiedReflectively_final() {
        FieldProbe publicFinal = getProbeFor(ModifierMix.class, "publicFinal");
        assertTrue(publicFinal.canBeModifiedReflectively());
    }

    @Test
    public void canBeModifiedReflectively_noModifiers() {
        FieldProbe publicNothingElse = getProbeFor(ModifierMix.class, "publicNothingElse");
        assertTrue(publicNothingElse.canBeModifiedReflectively());
    }

    private FieldProbe getProbeFor(Class<?> type, String fieldName) {
        try {
            Field field = type.getDeclaredField(fieldName);
            return FieldProbe.of(field);
        }
        catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldName: " + fieldName);
        }
    }

    class NonStaticInner {}

    static class ModifierMix {

        public static final int PUBLIC_STATIC_FINAL = -1;
        public static int publicStatic = 1;
        public final int publicFinal;
        public int publicNothingElse;

        public ModifierMix(int alsoYes) {
            this.publicFinal = alsoYes;
        }
    }
}
