/*
 * Copyright 2009-2012 Jan Ouwens
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

import static nl.jqno.equalsverifier.util.Assert.assertEquals;
import static nl.jqno.equalsverifier.util.Assert.assertFalse;
import static nl.jqno.equalsverifier.util.Assert.assertTrue;
import static nl.jqno.equalsverifier.util.Assert.fail;

import java.lang.reflect.Array;
import java.util.EnumSet;

import nl.jqno.equalsverifier.FieldInspector.FieldCheck;
import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.FieldAccessor;
import nl.jqno.equalsverifier.util.PrefabValues;
import nl.jqno.equalsverifier.util.SupportedAnnotations;

class FieldsChecker<T> implements Checker {
	private final ClassAccessor<T> classAccessor;
	private final PrefabValues prefabValues;
	private final EnumSet<Warning> warningsToSuppress;
	private final boolean allFieldsShouldBeUsed;

	public FieldsChecker(ClassAccessor<T> classAccessor, EnumSet<Warning> warningsToSuppress, boolean allFieldsShouldBeUsed) {
		this.classAccessor = classAccessor;
		this.prefabValues = classAccessor.getPrefabValues();
		this.warningsToSuppress = EnumSet.copyOf(warningsToSuppress);
		this.allFieldsShouldBeUsed = allFieldsShouldBeUsed;
	}
	
	@Override
	public void check() {
		FieldInspector<T> inspector = new FieldInspector<T>(classAccessor);
		
		inspector.check(new ArrayFieldCheck());
		inspector.check(new SignificanceFieldCheck());
		
		if (hasEqualsMethod(classAccessor.getType())) {
			inspector.check(new FloatAndDoubleFieldCheck());
		}
		
		if (!ignoreMutability()) {
			inspector.check(new MutableStateFieldCheck());
		}
		
		if (!warningsToSuppress.contains(Warning.TRANSIENT_FIELDS)) {
			inspector.check(new TransitiveFieldsCheck());
		}
	}
	
	private boolean hasEqualsMethod(Class<?> type) {
		try {
			type.getDeclaredMethod("equals", Object.class);
			return true;
		}
		catch (NoSuchMethodException e) {
			return false;
		}
	}

	private boolean ignoreMutability() {
		return warningsToSuppress.contains(Warning.NONFINAL_FIELDS) ||
				classAccessor.hasAnnotation(SupportedAnnotations.IMMUTABLE) ||
				classAccessor.hasAnnotation(SupportedAnnotations.ENTITY);
	}
	
	private class SignificanceFieldCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Object reference = referenceAccessor.getObject();
			Object changed = changedAccessor.getObject();
			
			changedAccessor.changeField(prefabValues);
			
			boolean equalsChanged = !reference.equals(changed);
			boolean hashCodeChanged = reference.hashCode() != changed.hashCode();
			
			if (equalsChanged != hashCodeChanged) {
				assertFalse("Significant fields: equals relies on " + referenceAccessor.getFieldName() + ", but hashCode does not.",
						equalsChanged);
				assertFalse("Significant fields: hashCode relies on " + referenceAccessor.getFieldName() + ", but equals does not.",
						hashCodeChanged);
			}
			
			if (allFieldsShouldBeUsed && !referenceAccessor.fieldIsStatic() && !referenceAccessor.fieldIsTransient()) {
				assertTrue("Significant fields: equals does not use " + referenceAccessor.getFieldName() + ".",
						equalsChanged);
			}
			
			referenceAccessor.changeField(prefabValues);
		}
	}
	
	private class FloatAndDoubleFieldCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Class<?> type = referenceAccessor.getFieldType();
			if (isFloat(type)) {
				referenceAccessor.set(Float.NaN);
				changedAccessor.set(Float.NaN);
				assertEquals("Float: equals doesn't use Float.compare for field " + referenceAccessor.getFieldName() + ".",
						referenceAccessor.getObject(), changedAccessor.getObject());
			}
			if (isDouble(type)) {
				referenceAccessor.set(Double.NaN);
				changedAccessor.set(Double.NaN);
				assertEquals("Double: equals doesn't use Double.compare for field " + referenceAccessor.getFieldName() + ".",
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
	
	private class ArrayFieldCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Class<?> arrayType = referenceAccessor.getFieldType();
			if (!arrayType.isArray()) {
				return;
			}
			
			String fieldName = referenceAccessor.getFieldName();
			Object reference = referenceAccessor.getObject();
			Object changed = changedAccessor.getObject();

			if (arrayType == Object[].class) {
				insertIntArray(referenceAccessor, changedAccessor);
				assertDeep(fieldName, reference, changed, "Object");
			}
			else if (arrayType.getComponentType().isArray()) {
				changeFields(referenceAccessor, changedAccessor);
				assertDeep(fieldName, reference, changed, "Multidimensional");
			}
			else {
				changeFields(referenceAccessor, changedAccessor);
				assertArray(fieldName, reference, changed);
			}
		}

		private void insertIntArray(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Array.set(referenceAccessor.get(), 0, new int[]{0});
			Array.set(changedAccessor.get(), 0, new int[]{0});
		}
		
		private void changeFields(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			referenceAccessor.changeField(prefabValues);
			changedAccessor.changeField(prefabValues);
		}

		private void assertDeep(String fieldName, Object reference, Object changed, String type) {
			assertEquals(type + " array: == or Arrays.equals() used instead of Arrays.deepEquals() for field " + fieldName + ".",
					reference, changed);
			assertEquals(type + " array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field " + fieldName + ".",
					reference.hashCode(), changed.hashCode());
		}
		
		private void assertArray(String fieldName, Object reference, Object changed) {
			assertEquals("Array: == used instead of Arrays.equals() for field " + fieldName + ".",
					reference, changed);
			assertEquals("Array: regular hashCode() used instead of Arrays.hashCode() for field " + fieldName + ".",
					reference.hashCode(), changed.hashCode());
		}
	}
	
	private class MutableStateFieldCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Object reference = referenceAccessor.getObject();
			Object changed = changedAccessor.getObject();
			
			changedAccessor.changeField(prefabValues);

			boolean equalsChanged = !reference.equals(changed);

			if (equalsChanged && !referenceAccessor.fieldIsFinal()) {
				fail("Mutability: equals depends on mutable field " + referenceAccessor.getFieldName() + ".");
			}
			
			referenceAccessor.changeField(prefabValues);
		}
	}
	
	private class TransitiveFieldsCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Object reference = referenceAccessor.getObject();
			Object changed = changedAccessor.getObject();
			
			changedAccessor.changeField(prefabValues);

			boolean equalsChanged = !reference.equals(changed);
			boolean fieldIsTransient = referenceAccessor.fieldIsTransient() ||
					classAccessor.fieldHasAnnotation(referenceAccessor.getField(), SupportedAnnotations.TRANSIENT);
			
			if (equalsChanged && fieldIsTransient) {
				fail("Transient field " + referenceAccessor.getFieldName() + " should not be included in equals/hashCode contract.");
			}
			
			referenceAccessor.changeField(prefabValues);
		}
	}
}
