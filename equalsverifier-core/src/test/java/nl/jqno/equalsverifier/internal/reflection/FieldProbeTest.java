package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.ConfigurationHelper;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Test;

/*
 * FieldProbe.isAnnotatedNonnull() is tested in AnnotationNonnullTest
 */
class FieldProbeTest {

    private static final String FIELD_NAME = "field";
    private Configuration<?> config = ConfigurationHelper.emptyConfiguration(ObjectContainer.class);

    @Test
    void getField() throws NoSuchFieldException {
        ObjectContainer foo = new ObjectContainer();
        Field field = foo.getClass().getDeclaredField(FIELD_NAME);
        FieldProbe probe = FieldProbe.of(field);
        assertThat(probe.getField()).isSameAs(field);
    }

    @Test
    void getType() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.getType()).isEqualTo(Object.class);
    }

    @Test
    void getName() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.getName()).isEqualTo(FIELD_NAME);
    }

    @Test
    void isNotPublic() {
        FieldProbe probe = getProbeFor(DifferentAccessModifiersFieldContainer.class, "K");
        assertThat(probe.isPublic()).isFalse();
    }

    @Test
    void isPublic() {
        FieldProbe probe = getProbeFor(DifferentAccessModifiersFieldContainer.class, "L");
        assertThat(probe.isPublic()).isTrue();
    }

    @Test
    void isNotPrimitive() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.isPrimitive()).isFalse();
    }

    @Test
    void isPrimitive() {
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertThat(probe.isPrimitive()).isTrue();
    }

    @Test
    void isNotFinal() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.isFinal()).isFalse();
    }

    @Test
    void isFinal() {
        FieldProbe probe = getProbeFor(FinalContainer.class, FIELD_NAME);
        assertThat(probe.isFinal()).isTrue();
    }

    @Test
    void isNotStatic() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.isStatic()).isFalse();
    }

    @Test
    void isStatic() {
        FieldProbe probe = getProbeFor(StaticContainer.class, FIELD_NAME);
        assertThat(probe.isStatic()).isTrue();
    }

    @Test
    void isNotTransient() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.isTransient()).isFalse();
    }

    @Test
    void isTransient() {
        FieldProbe probe = getProbeFor(TransientContainer.class, FIELD_NAME);
        assertThat(probe.isTransient()).isTrue();
    }

    @Test
    void isNotEnum() {
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertThat(probe.isEmptyOrSingleValueEnum()).isFalse();
    }

    @Test
    void isEnumButNotSingleValue() {
        FieldProbe probe = getProbeFor(EnumContainer.class, "twoElementEnum");
        assertThat(probe.isEmptyOrSingleValueEnum()).isFalse();
    }

    @Test
    void isSingleValueEnum() {
        FieldProbe probe = getProbeFor(EnumContainer.class, "oneElementEnum");
        assertThat(probe.isEmptyOrSingleValueEnum()).isTrue();
    }

    @Test
    void isEmptyEnum() {
        FieldProbe probe = getProbeFor(EnumContainer.class, "emptyEnum");
        assertThat(probe.isEmptyOrSingleValueEnum()).isTrue();
    }

    @Test
    void canBeDefault_forObject() {
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.canBeDefault(config)).isTrue();
    }

    @Test
    void canBeDefault_primitive() {
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertThat(probe.canBeDefault(config)).isTrue();
    }

    @Test
    void canBeDefault_primitiveWithPrefabbedField() {
        config = ConfigurationHelper.emptyConfigurationWithPrefabbedFields(PrimitiveContainer.class, FIELD_NAME);
        FieldProbe probe = getProbeFor(PrimitiveContainer.class, FIELD_NAME);
        assertThat(probe.canBeDefault(config)).isFalse();
    }

    @Test
    void canBeDefault_isMentionedExplicitly() {
        config = ConfigurationHelper.emptyConfigurationWithNonnullFields(ObjectContainer.class, FIELD_NAME);
        FieldProbe probe = getProbeFor(ObjectContainer.class, FIELD_NAME);
        assertThat(probe.canBeDefault(config)).isFalse();
    }

    @Test
    void canBeDefault_annotated() {
        config = ConfigurationHelper.emptyConfigurationWithNonnullFields(NonNullContainer.class, FIELD_NAME);
        FieldProbe probe = getProbeFor(NonNullContainer.class, FIELD_NAME);
        assertThat(probe.canBeDefault(config)).isFalse();
    }

    @Test
    void canBeModifiedReflectively_synthetic() {
        FieldProbe probe = getProbeFor(NonStaticInner.class, "this$0");
        assertThat(probe.canBeModifiedReflectively()).isFalse();
    }

    @Test
    void canBeModifiedReflectively_staticFinal() {
        FieldProbe publicStaticFinal = getProbeFor(ModifierMix.class, "PUBLIC_STATIC_FINAL");
        assertThat(publicStaticFinal.canBeModifiedReflectively()).isFalse();
    }

    @Test
    void canBeModifiedReflectively_static() {
        FieldProbe publicStatic = getProbeFor(ModifierMix.class, "publicStatic");
        assertThat(publicStatic.canBeModifiedReflectively()).isTrue();
    }

    @Test
    void canBeModifiedReflectively_final() {
        FieldProbe publicFinal = getProbeFor(ModifierMix.class, "publicFinal");
        assertThat(publicFinal.canBeModifiedReflectively()).isTrue();
    }

    @Test
    void canBeModifiedReflectively_noModifiers() {
        FieldProbe publicNothingElse = getProbeFor(ModifierMix.class, "publicNothingElse");
        assertThat(publicNothingElse.canBeModifiedReflectively()).isTrue();
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

    @SuppressWarnings("ClassCanBeStatic")
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
