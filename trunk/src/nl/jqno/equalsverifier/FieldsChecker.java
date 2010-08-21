/*
 * Copyright 2009 Jan Ouwens
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
import static nl.jqno.equalsverifier.util.Assert.fail;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;

import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.FieldAccessor;
import nl.jqno.equalsverifier.util.FieldIterable;
import nl.jqno.equalsverifier.util.ObjectAccessor;
import nl.jqno.equalsverifier.util.PrefabValues;

class FieldsChecker<T> {
	private final ClassAccessor<T> classAccessor;
	private final PrefabValues prefabValues;
	private final EnumSet<Warning> warningsToSuppress;

	public FieldsChecker(ClassAccessor<T> classAccessor, EnumSet<Warning> warningsToSuppress) {
		this.classAccessor = classAccessor;
		this.prefabValues = classAccessor.getPrefabValues();
		this.warningsToSuppress = EnumSet.copyOf(warningsToSuppress);
	}
	
	public void checkNull() {
		if (warningsToSuppress.contains(Warning.NULL_FIELDS)) {
			return;
		}
		
		check(new NullPointerExceptionFieldCheck());
	}
	
	public void check() {
		check(new ArrayFieldCheck());
		check(new SignificanceFieldCheck());
		check(new FloatAndDoubleFieldCheck());
		
		if (!warningsToSuppress.contains(Warning.NONFINAL_FIELDS)) {
			check(new MutableStateFieldCheck());
		}
	}
	
	private void check(FieldCheck check) {
		ObjectAccessor<T> reference = classAccessor.getFirstAccessor();
		ObjectAccessor<T> changed = classAccessor.getFirstAccessor();

		for (Field field : new FieldIterable(classAccessor.getType())) {
			check.execute(reference.fieldAccessorFor(field), changed.fieldAccessorFor(field));
		}
	}

	private class NullPointerExceptionFieldCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Field field = referenceAccessor.getField();
			if (field.getType().isPrimitive()) {
				return;
			}
			Object reference = referenceAccessor.getObject();
			Object changed = changedAccessor.getObject();
			
			changedAccessor.nullField();
			
			try {
				reference.equals(changed);
			}
			catch (NullPointerException e) {
				npeThrown("equals");
			}
			catch (Exception e) {
				exceptionThrown("equals", field, e);
			}
			try {
				changed.equals(reference);
			}
			catch (NullPointerException e) {
				npeThrown("equals");
			}
			catch (Exception e) {
				exceptionThrown("equals", field, e);
			}
			
			try {
				changed.hashCode();
			}
			catch (NullPointerException e) {
				npeThrown("hashCode");
			}
			catch (Exception e) {
				exceptionThrown("hashCode", field, e);
			}
			
			try {
				changed.toString();
			}
			catch (NullPointerException e) {
				npeThrown("toString");
			}
			catch (Exception e) {
				exceptionThrown("toString", field, e);
			}
			
			referenceAccessor.nullField();
		}

		private void npeThrown(String method) {
			fail("Non-nullity: " + method + " throws NullPointerException.");
		}
		
		private void exceptionThrown(String method, Field field, Exception e) {
			fail(method + " throws " + e.getClass().getSimpleName() + " when field " + field.getName() + " is null.");
		}
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

			referenceAccessor.set(createNewArrayInstance(arrayType));
			changedAccessor.set(createNewArrayInstance(arrayType));
			
			if (arrayType.getComponentType().isArray() || arrayType == Object[].class) {
				// In case of Object[], arbitrarily pick int as type for the nested array.
				Class<?> deepType = arrayType == Object[].class ?
						int[].class :
						arrayType.getComponentType();
				
				Array.set(referenceAccessor.get(), 0, createNewArrayInstance(deepType));
				Array.set(changedAccessor.get(), 0, createNewArrayInstance(deepType));
				
				assertEquals("Multidimensional or Object array: == or Arrays.equals() used instead of Arrays.deepEquals() for field " + fieldName + ".",
						reference, changed);
				assertEquals("Multidimensional or Object array: regular hashCode() or Arrays.hashCode() used instead of Arrays.deepHashCode() for field " + fieldName + ".",
						reference.hashCode(), changed.hashCode());
			}
			else {
				assertEquals("Array: == used instead of Arrays.equals() for field " + fieldName + ".",
					reference, changed);
				assertEquals("Array: regular hashCode() used instead of Arrays.hashCode() for field " + fieldName + ".",
					reference.hashCode(), changed.hashCode());
			}
		}

		private Object createNewArrayInstance(Class<?> arrayType) {
			Object array = Array.newInstance(arrayType.getComponentType(), 1);
			FieldAccessor.modifyArrayElement(array, 0, prefabValues);
			return array;
		}
	}
	
	private class MutableStateFieldCheck implements FieldCheck {
		@Override
		public void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor) {
			Object reference = referenceAccessor.getObject();
			Object changed = changedAccessor.getObject();
			
			changedAccessor.changeField(prefabValues);

			boolean equalsChanged = !reference.equals(changed);

			if (equalsChanged && !Modifier.isFinal(referenceAccessor.getField().getModifiers())) {
				assertEquals("Mutability: equals depends on mutable field " + referenceAccessor.getFieldName() + ".",
						reference, changed);
			}
			
			referenceAccessor.changeField(prefabValues);
		}
	}
	
	private interface FieldCheck {
		void execute(FieldAccessor referenceAccessor, FieldAccessor changedAccessor);
	}
}
