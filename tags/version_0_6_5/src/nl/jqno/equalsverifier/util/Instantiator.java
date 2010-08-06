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
package nl.jqno.equalsverifier.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Can instantiate, clone, and modify objects.
 * <p>
 * An instance {@code Instantiator} can operate on one class, and one class
 * alone. It is consistent: given the same parameters, invocations of the
 * methods will always yield identical results.
 * <p>
 * {@code Instantiator} cannot modifiy:<br>
 * 1. static final fields, and<br>
 * 2. final fields that are initialized to a compile-time constant in the
 *		field declaration. For a more detailed explanation, see <i>JSR-133,
 *		section 9.1.1</i>
 *		(http://www.cs.umd.edu/~pugh/java/memoryModel/jsr133.pdf). Or, see
 *		http://www.javaspecialists.eu/archive/Issue096.html<br>
 * These fields will be left unmodified.
 * 
 * @param <T> The class on which {@code Instantiator} operates.
 *
 * @author Jan Ouwens
 */
public class Instantiator<T> {
	private final Class<T> klass;
	private final Map<Class<?>, List<?>> prefabValues;
	private final LinkedHashSet<Class<?>> recursiveCallStack;
	private final Objenesis objenesis;
	private final ObjectInstantiator klassInstantiator;

	/**
	 * Factory method.
	 * 
	 * @param <T> The class on which {@code Instantiator} operates. Can usually
	 * 				be omitted, because the Java compiler will often infer this
	 * 				parameter from context.
	 * @param klass The class on which {@code Instantiator} operates. Should be
	 * 				the same as {@code <T>}.
	 * @return An instantiator for {@code klass}.
	 * @throws IllegalArgumentException If {@code klass} is an interface or an
	 * 				abstract class.
	 */
	public static <T> Instantiator<T> forClass(Class<T> klass) {
		if (klass.isInterface()) {
			throw new IllegalArgumentException("Cannot instantiate an interface.");
		}
		if (Modifier.isAbstract(klass.getModifiers())) {
			return new Instantiator<T>(createDynamicSubclass(klass), PrefabValues.get(), new LinkedHashSet<Class<?>>());
		}
		return new Instantiator<T>(klass, PrefabValues.get(), new LinkedHashSet<Class<?>>());
	}
	
	/**
	 * Private constructor. Call {@link #forClass(Class)} to instantiate.
	 */
	private Instantiator(Class<T> klass, Map<Class<?>, List<?>> prefabValues, LinkedHashSet<Class<?>> recursiveCallStack) {
		this.klass = klass;
		
		this.prefabValues = new HashMap<Class<?>, List<?>>();
		this.prefabValues.putAll(prefabValues);
		
		this.recursiveCallStack = recursiveCallStack;
		recursiveCallStack.add(klass);
		
		objenesis = new ObjenesisStd();
		klassInstantiator = objenesis.getInstantiatorOf(klass);
	}
	
	/**
	 * Add prefabricated values for classes that Instantiator cannot
	 * instantiate by itself.
	 * 
	 * @param <S> The class of the prefabricated values.
	 * @param klass The class of the prefabricated values.
	 * @param first An instance of {@code S}.
	 * @param second An instance of {@code S}.
	 * @throws NullPointerException If either {@code first} or {@code second}
	 * 				is null.
	 * @throws IllegalArgumentException If {@code first} equals {@code second}.
	 */
	public <S> void addPrefabValues(Class<S> klass, S first, S second) {
		if (klass == null) {
			throw new NullPointerException("klass is null.");
		}
		if (first == null || second == null) {
			throw new NullPointerException("Added null prefab value.");
		}
		if (first.equals(second)) {
			throw new IllegalArgumentException("Added equal prefab values: " + first + ".");
		}
		List<S> list = new ArrayList<S>();
		list.add(first);
		list.add(second);
		prefabValues.put(klass, list);
	}
	
	/**
	 * Copy, from another instantiator, prefabricated values for classes that
	 * Instantiator cannot instantiate by itself.
	 * 
	 * @param <S> The type parameter of {@code otherInstantiator}.
	 * @param otherInstantiator The instantiator from which the prefabricated
	 * 				values must be copied.
	 */
	public <S> void copyPrefabValues(Instantiator<S> otherInstantiator) {
		for (Class<?> prefabClass : otherInstantiator.prefabValues.keySet()) {
			prefabValues.put(prefabClass, otherInstantiator.prefabValues.get(prefabClass));
		}
	}
	
	/**
	 * Returns the class on which {@code Instantiator} operates ({@code T}).
	 * 
	 * @return The class on which {@code Instantiator} operates ({@code T}).
	 */
	public Class<T> getKlass() {
		return klass;
	}
	
	/**
	 * Instantiates an object of {@code T}.
	 * 
	 * @return An object of {@code T}.
	 */
	public T instantiate() {
		@SuppressWarnings("unchecked")
		T result = (T)klassInstantiator.newInstance();
		return result;
	}
	
	/**
	 * Instantiates an instance of a subclass of {@code T}. The subclass itself
	 * is generated and need not be supplied.
	 * 
	 * @return An instance of a subclass of {@code T}.
	 */
	public T instantiateSubclass() {
		return instantiateDynamicSubclass(klass);
	}
	
	/**
	 * Clones an instance of {@code T}.
	 * 
	 * Note: it does a "shallow" clone. Reference fields are copied, not cloned
	 * recursively.
	 * 
	 * @param original The object that should be cloned.
	 * @return A new instance of {@code T} that is equal to {@code original}.
	 */
	public T cloneFrom(T original) {
		T result = instantiate();
		
		for (Field field : new FieldIterable(klass)) {
			copyField(field, original, result);
		}
		
		return result;
	}
	
	/**
	 * Clones an instance of {@code T} into an instance of an unnamed subclass
	 * of {@code T}.
	 * 
	 * Note: it does a "shallow" clone. Reference fields are copied, not cloned
	 * recursively.
	 * 
	 * @param original The object that should be cloned.
	 * @return A new instance of {@code S}. All fields in {@code original} are
	 * 			copied into this instance, making it equal to {@code original},
	 * 			from {@code original}'s perspective.
	 */
	public T cloneToSubclass(T original) {
		T result = instantiateSubclass();
		copyIntoSubclassInstance(original, result);
		return result;
	}
	
	/**
	 * Clones an instance of {@code T} into an instance of a given subclass of
	 * {@code T}.
	 * 
	 * Note: it does a "shallow" clone. Reference fields are copied, not cloned
	 * recursively.
	 * 
	 * @param <S> A subclass of {@code T}.
	 * @param original The object that should be cloned.
	 * @param subclass A subclass of {@code T}.
	 * @return A new instance of {@code S}. All fields in {@code original} are
	 * 			copied into this instance, making it equal to {@code original},
	 * 			from {@code original}'s perspective.
	 */
	public <S extends T> S cloneToSubclass(T original, Class<S> subclass) {
		@SuppressWarnings("unchecked")
		S result = (S)objenesis.newInstance(subclass);
		copyIntoSubclassInstance(original, result);
		return result;
	}
	
	/**
	 * Modifies all fields of the given object, recursively for all fields of
	 * all its superclasses.
	 * <p>
	 * This method is consistent: given two equal objects; after scrambling
	 * both objects, they remain equal to each other.
	 * <p>
	 * It cannot modifiy:<br>
	 * 1. static final fields, and<br>
	 * 2. final fields that are initialized to a compile-time constant in the
	 *		field declaration.<br>
	 * These fields will be left unmodified.
	 * 
	 * @param obj The object to be scrambled.
	 */
	public void scramble(T obj) {
		for (Field field : new FieldIterable(klass)) {
			changeField(field, obj);
		}
	}
	
	/**
	 * Modifies all fields of the given object that are declared in {@code T},
	 * but not those inherited from superclasses.
	 * <p>
	 * This method is consistent: given two equal objects; after scrambling
	 * both objects, they remain equal to each other.
	 * <p>
	 * It cannot modifiy:<br>
	 * 1. static final fields, and<br>
	 * 2. final fields that are initialized to a compile-time constant in the
	 *		field declaration.<br>
	 * These fields will be left unmodified.
	 * 
	 * @param obj The object to be scrambled.
	 */
	public void shallowScramble(T obj) {
		for (Field field : klass.getDeclaredFields()) {
			changeField(field, obj);
		}
	}
	
	/**
	 * Sets the value of the given field on the given object to null. If the
	 * field is primitve, nothing is done.
	 * <p>
	 * It cannot modifiy:<br>
	 * 1. static final fields, and<br>
	 * 2. final fields that are initialized to a compile-time constant in the
	 *		field declaration.<br>
	 * These fields will be left unmodified.
	 * 
	 * @param field The field to be set to null.
	 * @param obj The object on which {@code field} is to be set to null.
	 */
	public void nullField(Field field, Object obj) {
		if (field.getType().isPrimitive() || !canBeModifiedReflectively(field)) {
			return;
		}
		
		try {
			field.setAccessible(true);
			field.set(obj, null);
		}
		catch (IllegalArgumentException e) {
			throw new AssertionError("Caught IllegalArgumentException on " + field.getName() + " (" + e.getMessage() + ")");
		}
		catch (IllegalAccessException e) {
			throw new AssertionError("Caught IllegalAccessException on " + field.getName() + " (" + e.getMessage() + ")");
		}
	}
	
	/**
	 * Changes the value of the given field on the given object.
	 * <p>
	 * This method is consistent: given two equal objects; after changing a
	 * field on both objects, they remain equal to each other.
	 * <p>
	 * It cannot modifiy:<br>
	 * 1. static final fields, and<br>
	 * 2. final fields that are initialized to a compile-time constant in the
	 *		field declaration.<br>
	 * These fields will be left unmodified.
	 * 
	 * @param field The field to be modified.
	 * @param obj The object on which {@code field} is to be modified.
	 */
	public void changeField(Field field, Object obj) {
		if (!canBeModifiedReflectively(field)) {
			return;
		}
		
		try {
			field.setAccessible(true);
			Class<?> type = field.getType();
			if (type == boolean.class) {
				field.setBoolean(obj, !field.getBoolean(obj));
			}
			else if (type == byte.class) {
				field.setByte(obj, (byte)(field.getByte(obj) + 1));
			}
			else if (type == char.class) {
				field.setChar(obj, (char)(field.getChar(obj) + 1));
			}
			else if (type == double.class) {
				field.setDouble(obj, field.getDouble(obj) + 1.0D);
			}
			else if (type == float.class) {
				field.setFloat(obj, field.getFloat(obj) + 1.0F);
			}
			else if (type == int.class) {
				field.setInt(obj, field.getInt(obj) + 1);
			}
			else if (type == long.class) {
				field.setLong(obj, field.getLong(obj) + 1);
			}
			else if (type == short.class) {
				field.setShort(obj, (short)(field.getShort(obj) + 1));
			}
			else if (prefabValues.containsKey(type)) {
				Object newValue = getNextPrefabValue(type, field.get(obj));
				field.set(obj, newValue);
			}
			else if (type.isEnum()) {
				Object value = field.get(obj);
				Object newValue = type.getEnumConstants()[0];
				if (value == newValue) {
					newValue = type.getEnumConstants()[1];
				}
				field.set(obj, newValue);
			}
			else if (type.isArray()) {
				Object array = field.get(obj);
				if (array == null) {
					array = Array.newInstance(type.getComponentType(), 1);
				}
				changeArrayElement(array, 0);
				field.set(obj, array);
			}
			else {
				createPrefabValues(type);
				Object newValue = getNextPrefabValue(type, field.get(obj));
				field.set(obj, newValue);
			}
		}
		catch (IllegalArgumentException e) {
			throw new AssertionError("Caught IllegalArgumentException on " + field.getName() + " (" + e.getMessage() + ")");
		}
		catch (IllegalAccessException e) {
			throw new AssertionError("Caught IllegalAccessException on " + field.getName() + " (" + e.getMessage() + ")");
		}
	}
	
	/**
	 * Changes one element of the given array.
	 * <p>
	 * This method is consistent: given two equal arrays; after changing
	 * elements with equal index on both arrays, they remain equal to each
	 * other.
	 * 
	 * @param array The array on which an element is to be modified.
	 * @param index The index of the element in {@code array} that is to be
	 * 				modified.
	 * @throws IllegalArgumentException If {@code array} is not an array.
	 */
	public void changeArrayElement(Object array, int index) {
		if (!array.getClass().isArray()) {
			throw new IllegalArgumentException("Expected arrary is not an array.");
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
		else if (prefabValues.containsKey(type)) {
			Object newValue = getNextPrefabValue(type, Array.get(array, index));
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
			changeArrayElement(nestedArray, 0);
			Array.set(array, index, nestedArray);
		}
		else {
			createPrefabValues(type);
			Object newValue = getNextPrefabValue(type, Array.get(array, index));
			Array.set(array, index, newValue);
		}
	}

	/**
	 * Determines whether the {@code field} can be modified using reflection.
	 * 
	 * @param field The field that needs to be checked.
	 * @return Whether or not the given field can be modified reflectively.
	 */
	public static boolean canBeModifiedReflectively(Field field) {
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
	
	private Object getNextPrefabValue(Class<?> type, Object currentValue) {
		List<?> values = prefabValues.get(type);
		int index = values.indexOf(currentValue) + 1;
		if (index >= values.size()) {
			index = 0;
		}
		return values.get(index);
	}

	@SuppressWarnings("unchecked")
	private void createPrefabValues(Class<?> type) {
		if (recursiveCallStack.contains(type)) {
			throw new AssertionError("Recursive datastructure.\nAdd prefab values for one of the following classes: " + recursiveCallStack + ".");
		}

		Instantiator i = new Instantiator(type, prefabValues, (LinkedHashSet<Class<?>>)recursiveCallStack.clone());
		final Object first;
		final Object second;
		
		if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
			first = instantiateDynamicSubclass(type);
			second = instantiateDynamicSubclass(type);
		}
		else {
			first = i.instantiate();
			second = i.instantiate();
		}

		i.scramble(first);
		i.scramble(second);
		i.scramble(second);
		
		addPrefabValues((Class)type, first, second);
	}
	
	private <S extends T> void copyIntoSubclassInstance(T original, S instance) {
		for (Field field : new FieldIterable(klass)) {
			copyField(field, original, instance);
		}
	}

	private void copyField(Field field, Object original, Object clone) {
		if (!canBeModifiedReflectively(field)) {
			return;
		}
		
		try {
			field.setAccessible(true);
			field.set(clone, field.get(original));
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
		catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private <S> S instantiateDynamicSubclass(Class<S> superclass) {
		Class<S> proxyClass = createDynamicSubclass(superclass);
		
		@SuppressWarnings("unchecked")
		S instance = (S)objenesis.newInstance(proxyClass);
		return instance;
	}

	private static <S> Class<S> createDynamicSubclass(Class<S> superclass) {
		Enhancer e = new Enhancer() {
			@Override
			@SuppressWarnings("unchecked")
        	protected void filterConstructors(Class sc, List constructors) {
        		// Don't filter
        	}
        };
        
		if (superclass.isInterface()) {
			e.setSuperclass(Object.class);
			e.setInterfaces(new Class[] { superclass });
		}
		else {
			e.setSuperclass(superclass);
		}
		
		e.setCallbackType(NoOp.class);
		@SuppressWarnings("unchecked")
		Class<S> proxyClass = e.createClass();
		return proxyClass;
	}
}
