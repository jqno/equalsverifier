/*
 * Copyright 2015 Jan Ouwens
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import nl.jqno.equalsverifier.util.FieldIterable;

/**
 * Records an initializer for a cached hash code (field name and recompute
 * method), if any, for the object to be verified.
 * 
 * EqualsVerifier may then, instead of calling <code>Object.hashCode()</code> to
 * obtain the hash code, call the {@link #getInitializedHashCode(Object)} method
 * in this class:
 * 
 * * If this class has recorded a cached hash code initializer for the object,
 * that method will recompute and update the cached hash code in the object
 * automatically, before returning the result of <code>Object.hashCode()</code>.
 * 
 * * If this class has not recorded a cached hash code initializer for the
 * object, it will simply return the value of <code>Object.hashCode()</code> as
 * normal instead.
 *
 * @author Niall Gallagher
 */
public class CachedHashCodeInitializer {
	static ThreadLocal<CachedHashCodeInitializer> THREAD_LOCAL = new ThreadLocal<CachedHashCodeInitializer>();
	
	/**
	 * Records details of the cached hash code (field name and recompute method)
	 * for the object to be verified. If the object does not have a cached hash
	 * code, supply null.
	 *
	 * @param cachedHashCodeInitializer
	 *            details of the cached hash code in the object to be verified,
	 *            or null if the object does not have a cached hash code
	 */
	public static void setInitializer(CachedHashCodeInitializer cachedHashCodeInitializer) {
		THREAD_LOCAL.set(cachedHashCodeInitializer);
	}
	
	/**
	 * Returns the hash code of the given object, first recomputing the cached
	 * hash code in the object if necessary.
	 *
	 * @param object The object for which the hash code is required
	 * @return The hash code of the object, recomputed automatically if it was cached
	 */
	public static int getInitializedHashCode(Object object) {
		CachedHashCodeInitializer cachedHashCodeInitializer = THREAD_LOCAL.get();
		if (cachedHashCodeInitializer != null) {
			cachedHashCodeInitializer.recomputeCachedHashCode(object);
		}
		return object.hashCode();
	}
	
	private final Field cachedHashCodeField;
	private final Method calculateMethod;
	
	public CachedHashCodeInitializer(Class<?> type, String cachedHashCodeField, String calculateHashCodeMethod) {
		this.cachedHashCodeField = findCachedHashCodeField(type, cachedHashCodeField);
		this.calculateMethod = findCalculateHashCodeMethod(type, calculateHashCodeMethod);
	}
	
	public void recomputeCachedHashCode(Object object) {
		try {
			cachedHashCodeField.set(object, 0); // zero the field first, in case calculateMethod checks it
			Integer recomputedHashCode = (Integer) calculateMethod.invoke(object);
			cachedHashCodeField.set(object, recomputedHashCode);
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to recompute cached hash code", e);
		}
	}
	
	private Field findCachedHashCodeField(Class<?> type, String cachedHashCodeFieldName) {
		for (Field candidateField : FieldIterable.of(type)) {
			if (candidateField.getName().equals(cachedHashCodeFieldName) && candidateField.getType().equals(int.class)) {
				candidateField.setAccessible(true);
				return candidateField;
			}
		}
		throw new IllegalStateException("Could not find cached hash code field: " + cachedHashCodeFieldName);
	}
	
	private Method findCalculateHashCodeMethod(Class<?> type, String calculateHashCodeMethodName) {
		Class<?> currentClass = type;
		while (!currentClass.equals(Object.class)) {
			try {
				Method method = currentClass.getDeclaredMethod(calculateHashCodeMethodName);
				method.setAccessible(true);
				return method;
			}
			catch (NoSuchMethodException ignore) {
				currentClass = currentClass.getSuperclass();
			}
		}
		throw new IllegalStateException("Could not find calculate hash code method: " + calculateHashCodeMethodName);
	}
}
