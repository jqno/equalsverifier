package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

import static nl.jqno.equalsverifier.internal.util.Assert.assertFalse;
import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

public class SignificantFieldCheck<T> implements FieldCheck {
    private final TypeTag typeTag;
    private final PrefabValues prefabValues;
    private final ClassAccessor<T> classAccessor;
    private final EnumSet<Warning> warningsToSuppress;
    private final Set<String> ignoredFields;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final Predicate<FieldAccessor> isCachedHashCodeField;
    private final boolean skipCertainTestsThatDontMatterWhenValuesAreNull;

    public SignificantFieldCheck(
            Configuration<T> config, Predicate<FieldAccessor> isCachedHashCodeField, boolean skipTestBecause0AndNullBothHaveA0HashCode) {
        this.typeTag = config.getTypeTag();
        this.prefabValues = config.getPrefabValues();
        this.classAccessor = config.createClassAccessor();
        this.warningsToSuppress = config.getWarningsToSuppress();
        this.ignoredFields = config.getIgnoredFields();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
        this.isCachedHashCodeField = isCachedHashCodeField;
        this.skipCertainTestsThatDontMatterWhenValuesAreNull = skipTestBecause0AndNullBothHaveA0HashCode;
    }

    @Override
    public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
        if (isCachedHashCodeField.test(referenceAccessor)) {
            return;
        }

        Object reference = referenceAccessor.getObject();
        Object changed = changedAccessor.getObject();
        String fieldName = referenceAccessor.getFieldName();

        if (referenceAccessor.get() == null && NonnullAnnotationVerifier.fieldIsNonnull(classAccessor, referenceAccessor.getField())) {
            return;
        }

        boolean equalToItself = reference.equals(changed);

        changedAccessor.changeField(prefabValues, typeTag);

        boolean equalsChanged = !reference.equals(changed);
        boolean hashCodeChanged =
                cachedHashCodeInitializer.getInitializedHashCode(reference) != cachedHashCodeInitializer.getInitializedHashCode(changed);

        assertEqualsAndHashCodeRelyOnSameFields(equalsChanged, hashCodeChanged, reference, changed, fieldName);
        assertFieldShouldBeIgnored(equalToItself, equalsChanged, referenceAccessor, fieldName);

        referenceAccessor.changeField(prefabValues, typeTag);
    }

    private void assertEqualsAndHashCodeRelyOnSameFields(boolean equalsChanged, boolean hashCodeChanged,
                Object reference, Object changed, String fieldName) {

        if (equalsChanged != hashCodeChanged) {
            boolean skipEqualsHasMoreThanHashCodeTest =
                    warningsToSuppress.contains(Warning.STRICT_HASHCODE) || skipCertainTestsThatDontMatterWhenValuesAreNull;
            if (!skipEqualsHasMoreThanHashCodeTest) {
                Formatter formatter = Formatter.of(
                        "Significant fields: equals relies on %%, but hashCode does not." +
                        "\n  %% has hashCode %%\n  %% has hashCode %%",
                        fieldName, reference, reference.hashCode(), changed, changed.hashCode());
                assertFalse(formatter, equalsChanged);
            }
            Formatter formatter = Formatter.of(
                    "Significant fields: hashCode relies on %%, but equals does not." +
                    "\nThese objects are equal, but probably shouldn't be:\n  %%\nand\n  %%",
                    fieldName, reference, changed);
            assertFalse(formatter, hashCodeChanged);
        }
    }

    private void assertFieldShouldBeIgnored(boolean equalToItself, boolean equalsChanged,
                FieldAccessor referenceAccessor, String fieldName) {

        if (shouldAllFieldsBeUsed(referenceAccessor) && isFieldEligible(referenceAccessor)) {
            assertTrue(Formatter.of("Significant fields: equals does not use %%.", fieldName), equalToItself);

            boolean fieldShouldBeIgnored = ignoredFields.contains(fieldName);
            assertTrue(Formatter.of("Significant fields: equals does not use %%, or it is stateless.", fieldName),
                    fieldShouldBeIgnored || equalsChanged);
            assertTrue(Formatter.of("Significant fields: equals should not use %%, but it does.", fieldName),
                    !fieldShouldBeIgnored || !equalsChanged || skipCertainTestsThatDontMatterWhenValuesAreNull);
        }
    }

    private boolean shouldAllFieldsBeUsed(FieldAccessor referenceAccessor) {
        return !warningsToSuppress.contains(Warning.ALL_FIELDS_SHOULD_BE_USED) &&
                !warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY) &&
                !(warningsToSuppress.contains(Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED) && !referenceAccessor.fieldIsFinal());
    }

    private boolean isFieldEligible(FieldAccessor referenceAccessor) {
        return !referenceAccessor.fieldIsStatic() &&
                !referenceAccessor.fieldIsTransient() &&
                !referenceAccessor.fieldIsEmptyOrSingleValueEnum() &&
                !classAccessor.fieldHasAnnotation(referenceAccessor.getField(), SupportedAnnotations.TRANSIENT);
    }
}
