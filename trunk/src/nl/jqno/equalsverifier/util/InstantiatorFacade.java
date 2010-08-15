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

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

/**
 * Can instantiate, clone, and modify objects.
 * <p>
 * An instance {@link InstantiatorFacade} can operate on one class, and one class
 * alone. It is consistent: given the same parameters, invocations of the
 * methods will always yield identical results.
 * <p>
 * {@link InstantiatorFacade} cannot modifiy:<br>
 * 1. static final fields, and<br>
 * 2. final fields that are initialized to a compile-time constant in the
 *		field declaration. For a more detailed explanation, see <i>JSR-133,
 *		section 9.1.1</i>
 *		(http://www.cs.umd.edu/~pugh/java/memoryModel/jsr133.pdf). Or, see
 *		http://www.javaspecialists.eu/archive/Issue096.html<br>
 * These fields will be left unmodified.
 * 
 * @param <T> The class on which {@link InstantiatorFacade} operates.
 *
 * @author Jan Ouwens
 */
public class InstantiatorFacade<T> {
	private final Class<T> klass;
	private final PrefabValues prefabValues;
	private final Instantiator<T> instantiator;

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
	public static <T> InstantiatorFacade<T> forClass(Class<T> klass) {
		if (klass.isInterface()) {
			throw new IllegalArgumentException("Cannot instantiate an interface.");
		}
		return new InstantiatorFacade<T>(klass, PrefabValuesFactory.withJavaClasses(), new LinkedHashSet<Class<?>>());
	}
	
	/**
	 * Private constructor. Call {@link #forClass(Class)} to instantiate.
	 */
	InstantiatorFacade(Class<T> klass, PrefabValues prefabValues, LinkedHashSet<Class<?>> recursiveCallStack) {
		this.klass = klass;
		this.instantiator = Instantiator.of(klass);
		this.prefabValues = prefabValues;
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
		prefabValues.put(klass, first, second);
	}
	
	/**
	 * Copy, from another instantiator, prefabricated values for classes that
	 * Instantiator cannot instantiate by itself.
	 * 
	 * @param <S> The type parameter of {@code otherInstantiator}.
	 * @param otherInstantiator The instantiator from which the prefabricated
	 * 				values must be copied.
	 */
	public <S> void copyPrefabValues(InstantiatorFacade<S> otherInstantiator) {
		prefabValues.putAll(otherInstantiator.prefabValues);
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
		return instantiator.instantiate();
	}
	
	/**
	 * Instantiates an instance of a subclass of {@code T}. The subclass itself
	 * is generated and need not be supplied.
	 * 
	 * @return An instance of a subclass of {@code T}.
	 */
	public T instantiateSubclass() {
		return instantiator.instantiateAnonymousSubclass();
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
		return ObjectAccessor.of(original, klass).clone();
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
		return ObjectAccessor.of(original).cloneIntoAnonymousSubclass();
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
		return ObjectAccessor.of(original).cloneIntoSubclass(subclass);
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
		try {
			ObjectAccessor.of(obj).scramble(prefabValues);
		}
		catch (RecursionException e) {
			Assert.fail(e.getMessage());
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
		ObjectAccessor.of(obj).shallowScramble(prefabValues);
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
		new FieldAccessor(obj, field).nullField();
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
		new FieldAccessor(obj, field).changeField(prefabValues);
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
		FieldAccessor.modifyArrayElement(array, index, prefabValues);
	}

	/**
	 * Determines whether the {@code field} can be modified using reflection.
	 * 
	 * @param field The field that needs to be checked.
	 * @return Whether or not the given field can be modified reflectively.
	 */
	public static boolean canBeModifiedReflectively(Field field) {
		return new FieldAccessor(null, field).canBeModifiedReflectively();
	}
	
	public void createPrefabValues(Class<?> type, LinkedHashSet<Class<?>> xxxx) {
		PrefabValuesFactory factory = new PrefabValuesFactory(prefabValues);
		factory.createFor(type);
	}
}
