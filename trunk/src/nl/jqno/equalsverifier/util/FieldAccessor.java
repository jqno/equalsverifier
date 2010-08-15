/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;

/**
 * Provides reflective access to one field of an object.
 *
 * @author Jan Ouwens
 */
public class FieldAccessor {
	private final Object object;
	private final Field field;

	/**
	 * Constructor.
	 * 
	 * @param object The object we want to access.
	 * @param field A field of object.
	 */
	public FieldAccessor(Object object, Field field) {
		this.object = object;
		this.field = field;
	}
	
	/**
	 * Tries to make the field null. If this fails, nothing happens.
	 */
	public void nullField() {
		modify(new FieldNuller());
	}
	
	/**
	 * Copies field's value to the corresponding field in the specified object.
	 * 
	 * @param to The object into which to copy the field.
	 */
	public void copyTo(Object to) {
		modify(new FieldCopier(to));
	}
	
	/**
	 * Changes the field's value to something else. The new value will never be
	 * null. Other than that, the precise value is undefined.
	 * 
	 * @param prefabValues If the field is of a type contained within
	 * 			prefabValues, the new value will be taken from it.
	 */
	public void changeField(PrefabValues prefabValues) {
		modify(new FieldChanger(prefabValues));
	}
	
	/**
	 * Assumes the field is an array, and changes the value at index to
	 * something else. The new value will never be null. Other than that, the
	 * precise value is undefined.
	 * 
	 * @param index The value at this index of the array will be changed.
	 * @param prefabValues If the field is of a type contained within
	 * 			prefabValues, the new value will be taken from it.
	 * @throws IllegalArgumentException if field is not an array.
	 * @throws ArrayIndexOutOfBoundsException if index is out of bounds.
	 */
	public void changeArrayField(int index, PrefabValues prefabValues) {
		modify(new ArrayFieldChanger(index, prefabValues));
	}
	
	private void modify(FieldModifier modifier) {
		if (!canBeModifiedReflectively()) {
			return;
		}
		
		field.setAccessible(true);
		try {
			modifier.modify();
		}
		catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private boolean canBeModifiedReflectively() {
		if (field.isSynthetic()) {
			return false;
		}
		int modifiers = field.getModifiers();
		if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
			return false;
		}
		// CGLib, which is used by this class, adds several fields to classes
		// that it creates. If they are changed using reflection, exceptions
		// are thrown.
		if (field.getName().startsWith("CGLIB$")) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static void createPrefabValues(PrefabValues prefabValues, Class<?> type) {
		InstantiatorFacade foo = new InstantiatorFacade(type, prefabValues, new LinkedHashSet<Class<?>>());
		foo.createPrefabValues(type, new LinkedHashSet<Class<?>>());
	}
	
	private interface FieldModifier {
		void modify() throws IllegalAccessException;
	}
	
	private class FieldNuller implements FieldModifier {
		@Override
		public void modify() throws IllegalAccessException {
			if (!field.getType().isPrimitive()) {
				field.set(object, null);
			}
		}
	}
	
	private class FieldCopier implements FieldModifier {
		private final Object to;

		public FieldCopier(Object to) {
			this.to = to;
		}

		@Override
		public void modify() throws IllegalAccessException {
			field.set(to, field.get(object));
		}
	}
	
	private class FieldChanger implements FieldModifier {
		private final PrefabValues prefabValues;

		public FieldChanger(PrefabValues prefabValues) {
			this.prefabValues = prefabValues;
		}
		
		@Override
		public void modify() throws IllegalAccessException {
			Class<?> type = field.getType();
			if (type == boolean.class) {
				field.setBoolean(object, !field.getBoolean(object));
			}
			else if (type == byte.class) {
				field.setByte(object, (byte)(field.getByte(object) + 1));
			}
			else if (type == char.class) {
				field.setChar(object, (char)(field.getChar(object) + 1));
			}
			else if (type == double.class) {
				field.setDouble(object, field.getDouble(object) + 1.0D);
			}
			else if (type == float.class) {
				field.setFloat(object, field.getFloat(object) + 1.0F);
			}
			else if (type == int.class) {
				field.setInt(object, field.getInt(object) + 1);
			}
			else if (type == long.class) {
				field.setLong(object, field.getLong(object) + 1);
			}
			else if (type == short.class) {
				field.setShort(object, (short)(field.getShort(object) + 1));
			}
			else if (prefabValues.contains(type)) {
				Object newValue = prefabValues.getOther(type, field.get(object));
				field.set(object, newValue);
			}
			else if (type.isEnum()) {
				Object value = field.get(object);
				Object newValue = type.getEnumConstants()[0];
				if (value == newValue) {
					newValue = type.getEnumConstants()[1];
				}
				field.set(object, newValue);
			}
			else if (type.isArray()) {
				Object array = field.get(object);
				if (array == null) {
					array = Array.newInstance(type.getComponentType(), 1);
				}
				modifyArrayElement(array, 0, prefabValues);
				field.set(object, array);
			}
			else {
				createPrefabValues(prefabValues, type);
				Object newValue = prefabValues.getOther(type, field.get(object));
				field.set(object, newValue);
			}
		}
	}
	
	private class ArrayFieldChanger implements FieldModifier {
		private final int index;
		private final PrefabValues prefabValues;

		public ArrayFieldChanger(int index, PrefabValues prefabValues) {
			this.index = index;
			this.prefabValues = prefabValues;
		}

		@Override
		public void modify() throws IllegalAccessException {
			modifyArrayElement(field.get(object), index, prefabValues);
		}
	}
	
	private static void modifyArrayElement(Object array, int index, PrefabValues prefabValues) {
		if (!array.getClass().isArray()) {
			throw new IllegalArgumentException("Expected array is not an array.");
		}
		
		Class<?> type = array.getClass().getComponentType();
		if (type == boolean.class) {
			Array.setBoolean(array, index, !Array.getBoolean(array, index));
		}
		else if (type == byte.class) {
			Array.setByte(array, index, (byte)(Array.getByte(array, index) + 1));
		}
		else if (type == char.class) {
			Array.setChar(array, index, (char)(Array.getChar(array, index) + 1));
		}
		else if (type == double.class) {
			Array.setDouble(array, index, Array.getDouble(array, index) + 1.0D);
		}
		else if (type == float.class) {
			Array.setFloat(array, index, Array.getFloat(array, index) + 1.0F);
		}
		else if (type == int.class) {
			Array.setInt(array, index, Array.getInt(array, index) + 1);
		}
		else if (type == long.class) {
			Array.setLong(array, index, Array.getLong(array, index) + 1);
		}
		else if (type == short.class) {
			Array.setShort(array, index, (short)(Array.getShort(array, index) + 1));
		}
		else if (prefabValues.contains(type)) {
			Object newValue = prefabValues.getOther(type, Array.get(array, index));
			Array.set(array, index, newValue);
		}
		else if (type.isEnum()) {
			Object value = Array.get(array, index);
			Object newValue = type.getEnumConstants()[0];
			if (value == newValue) {
				newValue = type.getEnumConstants()[1];
			}
			Array.set(array, index, newValue);
		}
		else if (type.isArray()) {
			Object nestedArray = Array.get(array, index);
			if (nestedArray == null) {
				nestedArray = Array.newInstance(type.getComponentType(), 1);
			}
			modifyArrayElement(nestedArray, index, prefabValues);
			Array.set(array, index, nestedArray);
		}
		else {
			createPrefabValues(prefabValues, type);
			Object newValue = prefabValues.getOther(type, Array.get(array, index));
			Array.set(array, index, newValue);
		}
	}
}
