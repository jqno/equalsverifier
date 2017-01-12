/*
 * Copyright 2009-2016 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.FieldInspector.FieldCheck;
import nl.jqno.equalsverifier.internal.*;
import nl.jqno.equalsverifier.internal.annotations.NonnullAnnotationChecker;
import nl.jqno.equalsverifier.internal.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;

import static nl.jqno.equalsverifier.internal.Assert.*;

class FieldsChecker<T> implements Checker {
    private final TypeTag typeTag;
    private final ClassAccessor<T> classAccessor;
    private final PrefabValues prefabValues;
    private final EnumSet<Warning> warningsToSuppress;
    private final Set<String> ignoredFields;
    private final Set<String> nonnullFields;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public FieldsChecker(Configuration<T> config) {
        this.typeTag = config.getTypeTag();
        this.classAccessor = config.createClassAccessor();
        this.prefabValues = config.getPrefabValues();
        this.warningsToSuppress = config.getWarningsToSuppress();
        this.ignoredFields = config.getIgnoredFields();
        this.nonnullFields = config.getNonnullFields();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
    }

    @Override
    public void check() {
        FieldInspector<T> inspector = new FieldInspector<>(classAccessor, typeTag);

        if (!classAccessor.isEqualsInheritedFromObject()) {
            inspector.check(new ArrayFieldCheck());
            inspector.check(new FloatAndDoubleFieldCheck());
            inspector.check(new ReflexivityFieldCheck());
        }

        if (!ignoreMutability()) {
            inspector.check(new MutableStateFieldCheck());
        }

        if (!warningsToSuppress.contains(Warning.TRANSIENT_FIELDS)) {
            inspector.check(new TransientFieldsCheck());
        }

        inspector.check(new SignificantFieldCheck(false));
        inspector.check(new SymmetryFieldCheck());
        inspector.check(new TransitivityFieldCheck());

        if (!warningsToSuppress.contains(Warning.NULL_FIELDS)) {
            inspector.checkWithNull(nonnullFields, new SignificantFieldCheck(true));
        }
    }

    private boolean ignoreMutability() {
        return warningsToSuppress.contains(Warning.NONFINAL_FIELDS) ||
                classAccessor.hasAnnotation(SupportedAnnotations.IMMUTABLE) ||
                classAccessor.hasAnnotation(SupportedAnnotations.ENTITY);
    }

    private boolean isCachedHashCodeField(FieldAccessor accessor) {
        return accessor.getFieldName().equals(cachedHashCodeInitializer.getCachedHashCodeFieldName());
    }

    private class SymmetryFieldCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            checkSymmetry(referenceAccessor, changedAccessor);

            changedAccessor.changeField(prefabValues, typeTag);
            checkSymmetry(referenceAccessor, changedAccessor);

            referenceAccessor.changeField(prefabValues, typeTag);
            checkSymmetry(referenceAccessor, changedAccessor);
        }

        private void checkSymmetry(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Object left = referenceAccessor.getObject();
            Object right = changedAccessor.getObject();
            assertTrue(Formatter.of("Symmetry: objects are not symmetric:\n  %%\nand\n  %%", left, right),
                    left.equals(right) == right.equals(left));
        }
    }

    private class TransitivityFieldCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Object a1 = referenceAccessor.getObject();
            Object b1 = buildB1(changedAccessor);
            Object b2 = buildB2(a1, referenceAccessor.getField());

            boolean x = a1.equals(b1);
            boolean y = b1.equals(b2);
            boolean z = a1.equals(b2);

            if (countFalses(x, y, z) == 1) {
                fail(Formatter.of(
                        "Transitivity: two of these three instances are equal to each other," +
                        " so the third one should be, too:\n-  %%\n-  %%\n-  %%",
                        a1, b1, b2));
            }
        }

        private Object buildB1(FieldAccessor accessor) {
            accessor.changeField(prefabValues, typeTag);
            return accessor.getObject();
        }

        private Object buildB2(Object a1, Field referenceField) {
            Object result = ObjectAccessor.of(a1).copy();
            ObjectAccessor<?> objectAccessor = ObjectAccessor.of(result);
            objectAccessor.fieldAccessorFor(referenceField).changeField(prefabValues, typeTag);
            for (Field field : FieldIterable.of(result.getClass())) {
                if (!field.equals(referenceField)) {
                    objectAccessor.fieldAccessorFor(field).changeField(prefabValues, typeTag);
                }
            }
            return result;
        }

        private int countFalses(boolean... bools) {
            int result = 0;
            for (boolean b : bools) {
                if (!b) {
                    result++;
                }
            }
            return result;
        }
    }

    private class SignificantFieldCheck implements FieldCheck {
        private final boolean skipTestBecause0AndNullBothHaveA0HashCode;

        public SignificantFieldCheck(boolean skipTestBecause0AndNullBothHaveA0HashCode) {
            this.skipTestBecause0AndNullBothHaveA0HashCode = skipTestBecause0AndNullBothHaveA0HashCode;
        }

        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            if (isCachedHashCodeField(referenceAccessor)) {
                return;
            }

            Object reference = referenceAccessor.getObject();
            Object changed = changedAccessor.getObject();
            String fieldName = referenceAccessor.getFieldName();

            if (referenceAccessor.get() == null && NonnullAnnotationChecker.fieldIsNonnull(classAccessor, referenceAccessor.getField())) {
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
                        warningsToSuppress.contains(Warning.STRICT_HASHCODE) || skipTestBecause0AndNullBothHaveA0HashCode;
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

            boolean allFieldsShouldBeUsed = !warningsToSuppress.contains(Warning.ALL_FIELDS_SHOULD_BE_USED) &&
                    !warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY);

            boolean fieldIsEligible = !referenceAccessor.fieldIsStatic() &&
                    !referenceAccessor.fieldIsTransient() &&
                    !referenceAccessor.fieldIsSingleValueEnum();

            if (allFieldsShouldBeUsed && fieldIsEligible) {
                assertTrue(Formatter.of("Significant fields: equals does not use %%.", fieldName), equalToItself);

                boolean fieldShouldBeIgnored = ignoredFields.contains(fieldName);
                assertTrue(Formatter.of("Significant fields: equals does not use %%, or it is stateless.", fieldName),
                        fieldShouldBeIgnored || equalsChanged);
                assertTrue(Formatter.of("Significant fields: equals should not use %%, but it does.", fieldName),
                        !fieldShouldBeIgnored || !equalsChanged);
            }
        }
    }

    private class ArrayFieldCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Class<?> arrayType = referenceAccessor.getFieldType();
            if (!arrayType.isArray()) {
                return;
            }
            if (!referenceAccessor.canBeModifiedReflectively()) {
                return;
            }

            String fieldName = referenceAccessor.getFieldName();
            Object reference = referenceAccessor.getObject();
            Object changed = changedAccessor.getObject();
            replaceInnermostArrayValue(changedAccessor);

            if (arrayType.getComponentType().isArray()) {
                assertDeep(fieldName, reference, changed);
            }
            else {
                assertArray(fieldName, reference, changed);
            }
        }

        private void replaceInnermostArrayValue(FieldAccessor accessor) {
            Object newArray = arrayCopy(accessor.get());
            accessor.set(newArray);
        }

        private Object arrayCopy(Object array) {
            if (array == null) {
                return null;
            }
            Class<?> componentType = array.getClass().getComponentType();
            int length = Array.getLength(array);
            Object result = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (componentType.isArray()) {
                    Array.set(result, i, arrayCopy(Array.get(array, i)));
                }
                else {
                    Array.set(result, i, Array.get(array, i));
                }
            }
            return result;
        }

        private void assertDeep(String fieldName, Object reference, Object changed) {
            Formatter eqEqFormatter = Formatter.of(
                    "Multidimensional array: ==, regular equals() or Arrays.equals() used instead of Arrays.deepEquals() for field %%.",
                    fieldName);
            assertEquals(eqEqFormatter, reference, changed);

            Formatter regularFormatter = Formatter.of(
                    "Multidimensional array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field %%.",
                    fieldName);
            assertEquals(regularFormatter,
                    cachedHashCodeInitializer.getInitializedHashCode(reference), cachedHashCodeInitializer.getInitializedHashCode(changed));
        }

        private void assertArray(String fieldName, Object reference, Object changed) {
            assertEquals(Formatter.of("Array: == or regular equals() used instead of Arrays.equals() for field %%.", fieldName),
                    reference, changed);
            assertEquals(Formatter.of("Array: regular hashCode() used instead of Arrays.hashCode() for field %%.", fieldName),
                    cachedHashCodeInitializer.getInitializedHashCode(reference), cachedHashCodeInitializer.getInitializedHashCode(changed));
        }
    }

    private class FloatAndDoubleFieldCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Class<?> type = referenceAccessor.getFieldType();
            if (isFloat(type)) {
                referenceAccessor.set(Float.NaN);
                changedAccessor.set(Float.NaN);
                assertEquals(Formatter.of("Float: equals doesn't use Float.compare for field %%.", referenceAccessor.getFieldName()),
                        referenceAccessor.getObject(), changedAccessor.getObject());
            }
            if (isDouble(type)) {
                referenceAccessor.set(Double.NaN);
                changedAccessor.set(Double.NaN);
                assertEquals(Formatter.of("Double: equals doesn't use Double.compare for field %%.", referenceAccessor.getFieldName()),
                        referenceAccessor.getObject(), changedAccessor.getObject());
            }
        }

        private boolean isFloat(Class<?> type) {
            return type == float.class || type == Float.class;
        }

        private boolean isDouble(Class<?> type) {
            return type == double.class || type == Double.class;
        }
    }

    private class ReflexivityFieldCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            if (warningsToSuppress.contains(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)) {
                return;
            }

            checkReferenceReflexivity(referenceAccessor, changedAccessor);
            checkValueReflexivity(referenceAccessor, changedAccessor);
            checkNullReflexivity(referenceAccessor, changedAccessor);
        }

        private void checkReferenceReflexivity(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            referenceAccessor.changeField(prefabValues, typeTag);
            changedAccessor.changeField(prefabValues, typeTag);
            checkReflexivityFor(referenceAccessor, changedAccessor);
        }

        private void checkValueReflexivity(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Class<?> fieldType = changedAccessor.getFieldType();
            if (warningsToSuppress.contains(Warning.REFERENCE_EQUALITY)) {
                return;
            }
            if (fieldType.equals(Object.class) || fieldType.isInterface()) {
                return;
            }
            if (changedAccessor.fieldIsStatic() && changedAccessor.fieldIsFinal()) {
                return;
            }
            ClassAccessor<?> fieldTypeAccessor = ClassAccessor.of(fieldType, prefabValues, true);
            if (!fieldTypeAccessor.declaresEquals()) {
                return;
            }
            Object value = changedAccessor.get();
            if (value.getClass().isSynthetic()) {
                // Sometimes not the fieldType, but its content, is synthetic.
                return;
            }

            Object copy = ObjectAccessor.of(value).copy();
            changedAccessor.set(copy);

            Formatter f = Formatter.of("Reflexivity: == used instead of .equals() on field: %%" +
                    "\nIf this is intentional, consider suppressing Warning.%%",
                    changedAccessor.getFieldName(), Warning.REFERENCE_EQUALITY.toString());
            Object left = referenceAccessor.getObject();
            Object right = changedAccessor.getObject();
            assertEquals(f, left, right);
        }

        private void checkNullReflexivity(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Field field = referenceAccessor.getField();
            boolean fieldIsPrimitive = referenceAccessor.fieldIsPrimitive();
            boolean fieldIsNonNull = NonnullAnnotationChecker.fieldIsNonnull(classAccessor, field);
            boolean ignoreNull = fieldIsNonNull || warningsToSuppress.contains(Warning.NULL_FIELDS) || nonnullFields.contains(field.getName());
            if (fieldIsPrimitive || !ignoreNull) {
                referenceAccessor.defaultField();
                changedAccessor.defaultField();
                checkReflexivityFor(referenceAccessor, changedAccessor);
            }
        }

        private void checkReflexivityFor(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Object left = referenceAccessor.getObject();
            Object right = changedAccessor.getObject();

            if (warningsToSuppress.contains(Warning.IDENTICAL_COPY)) {
                assertFalse(Formatter.of("Unnecessary suppression: %%. Two identical copies are equal.", Warning.IDENTICAL_COPY.toString()),
                        left.equals(right));
            }
            else {
                Formatter f = Formatter.of("Reflexivity: object does not equal an identical copy of itself:\n  %%" +
                        "\nIf this is intentional, consider suppressing Warning.%%", left, Warning.IDENTICAL_COPY.toString());
                assertEquals(f, left, right);
            }
        }
    }

    private class MutableStateFieldCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            if (isCachedHashCodeField(referenceAccessor)) {
                return;
            }

            Object reference = referenceAccessor.getObject();
            Object changed = changedAccessor.getObject();

            changedAccessor.changeField(prefabValues, typeTag);

            boolean equalsChanged = !reference.equals(changed);

            if (equalsChanged && !referenceAccessor.fieldIsFinal()) {
                fail(Formatter.of("Mutability: equals depends on mutable field %%.", referenceAccessor.getFieldName()));
            }

            referenceAccessor.changeField(prefabValues, typeTag);
        }
    }

    private class TransientFieldsCheck implements FieldCheck {
        @Override
        public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
            Object reference = referenceAccessor.getObject();
            Object changed = changedAccessor.getObject();

            changedAccessor.changeField(prefabValues, typeTag);

            boolean equalsChanged = !reference.equals(changed);
            boolean fieldIsTransient = referenceAccessor.fieldIsTransient() ||
                    classAccessor.fieldHasAnnotation(referenceAccessor.getField(), SupportedAnnotations.TRANSIENT);

            if (equalsChanged && fieldIsTransient) {
                fail(Formatter.of("Transient field %% should not be included in equals/hashCode contract.", referenceAccessor.getFieldName()));
            }

            referenceAccessor.changeField(prefabValues, typeTag);
        }
    }
}
