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

import nl.jqno.equalsverifier.util.Instantiator;

class FieldsChecker<T> {
	private final Instantiator<T> instantiator;
	private final EnumSet<Feature> features;

	public FieldsChecker(Instantiator<T> instantiator, EnumSet<Feature> featureSet) {
		this.instantiator = instantiator;
		this.features = EnumSet.copyOf(featureSet);
	}
	
	public void checkNull() {
		if (features.contains(Feature.FIELDS_ARE_NEVER_NULL)) {
			return;
		}
		
		check(new NullPointerExceptionFieldCheck<T>());
	}
	
	public void check() {
		check(new SignificanceFieldCheck<T>());
		check(new FloatAndDoubleFieldCheck<T>());
		check(new ArrayFieldCheck<T>());
		
		if (!features.contains(Feature.ALLOW_NONFINAL_FIELDS)) {
			check(new MutableStateFieldCheck<T>());
		}
	}
	
	private void check(FieldCheck<T> check) {
		T reference = instantiator.instantiate();
		instantiator.scramble(reference);
		T changed = instantiator.instantiate();
		instantiator.scramble(changed);

		Class<? extends Object> klass = changed.getClass();
		while (klass != Object.class) {
			for (Field field : klass.getDeclaredFields()) {
				try {
					if (Instantiator.canBeModifiedReflectively(field)) {
						field.setAccessible(true);
						check.execute(field, reference, changed, instantiator);
					}
				}
				catch (IllegalArgumentException e) {
					fail("Caught IllegalArgumentException on " + field.getName() + " (" + e.getMessage() + ")");
				}
				catch (IllegalAccessException e) {
					fail("Caught IllegalAccessException on " + field.getName() + " (" + e.getMessage() + ")");
				}
			}
			klass = klass.getSuperclass();
		}
	}

	private static class NullPointerExceptionFieldCheck<T> implements FieldCheck<T> {
		public void execute(Field field, T reference, T changed, Instantiator<T> instantiator) {
			if (field.getType().isPrimitive()) {
				return;
			}
			
			instantiator.nullField(field, changed);
			
			try {
				changed.toString();
			}
			catch (NullPointerException e) {
				fail("Non-nullity: toString throws NullPointerException.");
			}
			
			try {
				reference.equals(changed);
			}
			catch (NullPointerException e) {
				fail("Non-nullity: equals throws NullPointerException.");
			}
			try {
				changed.equals(reference);
			}
			catch (NullPointerException e) {
				fail("Non-nullity: equals throws NullPointerException.");
			}
			
			try {
				changed.hashCode();
			}
			catch (NullPointerException e) {
				fail("Non-nullity: hashCode throws NullPointerException.");
			}
			instantiator.nullField(field, reference);
		}
	}
	
	private static class SignificanceFieldCheck<T> implements FieldCheck<T> {
		public void execute(Field field, T reference, T changed, Instantiator<T> instantiator) {
			instantiator.changeField(field, changed);
			
			boolean equalsChanged = !reference.equals(changed);
			boolean hashCodeChanged = reference.hashCode() != changed.hashCode();
			
			if (equalsChanged != hashCodeChanged) {
				assertFalse("Significant fields: equals relies on " + field.getName() + ", but hashCode does not.",
						equalsChanged);
				assertFalse("Significant fields: hashCode relies on " + field.getName() + ", but equals does not.",
						hashCodeChanged);
			}
			
			instantiator.changeField(field, reference);
		}
	}
	
	private static class FloatAndDoubleFieldCheck<T> implements FieldCheck<T> {
		public void execute(Field field, T reference, T changed, Instantiator<T> instantiator) throws IllegalArgumentException, IllegalAccessException {
			Class<?> type = field.getType();
			if (isFloat(type)) {
				set(field, reference, Float.NaN);
				set(field, changed, Float.NaN);
				assertEquals("Float: equals doesn't use Float.compare.", reference, changed);
			}
			if (isDouble(type)) {
				set(field, reference, Double.NaN);
				set(field, changed, Double.NaN);
				assertEquals("Double: equals doesn't use Double.compare.", reference, changed);
			}
		}

		private boolean isFloat(Class<?> type) {
			return type == float.class || type == Float.class;
		}
		
		private boolean isDouble(Class<?> type) {
			return type == double.class || type == Double.class;
		}
		
		private void set(Field field, T object, double value) throws IllegalArgumentException, IllegalAccessException {
			Class<?> type = field.getType();
			if (type == float.class) {
				field.setFloat(object, (float)value);
			}
			if (type == Float.class) {
				field.set(object, (float)value);
			}
			if (type == double.class) {
				field.setDouble(object, value);
			}
			if (type == Double.class) {
				field.set(object, value);
			}
		}
	}
	
	private static class ArrayFieldCheck<T> implements FieldCheck<T> {
		public void execute(Field field, T reference, T changed, Instantiator<T> instantiator) throws IllegalArgumentException, IllegalAccessException {
			Class<?> arrayType = field.getType();
			if (!arrayType.isArray()) {
				return;
			}

			field.set(reference, createNewArrayInstance(arrayType, instantiator));
			field.set(changed, createNewArrayInstance(arrayType, instantiator));
			
			if (arrayType.getComponentType().isArray() || arrayType == Object[].class) {
				// In case of Object[], arbitrarily pick int as type for the nested array.
				Class<?> deepType = arrayType == Object[].class ?
						int[].class :
						arrayType.getComponentType();
				
				Array.set(field.get(reference), 0, createNewArrayInstance(deepType, instantiator));
				Array.set(field.get(changed), 0, createNewArrayInstance(deepType, instantiator));
				
				assertEquals("Multidimensional or Object array: == or Arrays.equals used instead of Arrays.deepEquals().",
							reference, changed);
			}
			else {
				assertEquals("Array: == used instead of Arrays.equals().", reference, changed);
			}
		}

		private Object createNewArrayInstance(Class<?> arrayType, Instantiator<T> instantiator) {
			Object array = Array.newInstance(arrayType.getComponentType(), 1);
			instantiator.changeArrayElement(array, 0);
			return array;
		}
	}
	
	private static class MutableStateFieldCheck<T> implements FieldCheck<T> {
		public void execute(Field field, T reference, T changed, Instantiator<T> instantiator) {
			instantiator.changeField(field, changed);

			boolean equalsChanged = !reference.equals(changed);

			if (equalsChanged && !Modifier.isFinal(field.getModifiers())) {
				assertEquals("Mutability: equals depends on mutable field " + field.getName() + ".",
						reference, changed);
			}
			
			instantiator.changeField(field, reference);
		}
	}
	
	private interface FieldCheck<T> {
		void execute(Field field, T reference, T changed, Instantiator<T> instantiator) throws IllegalArgumentException, IllegalAccessException;
	}
}
