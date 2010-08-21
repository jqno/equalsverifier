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

import java.util.HashMap;
import java.util.Map;

/**
 * Container of prefabricated instances of objects and classes.
 *
 * @author Jan Ouwens
 */
public class PrefabValues {
	private final Map<Class<?>, Tuple<?>> values = new HashMap<Class<?>, Tuple<?>>();
	
	/**
	 * Associates the specified values with the specified class in this
	 * collection of prefabricated values.
	 * 
	 * @param <T> The type of value to put into this {@link PrefabValues}.
	 * @param type The class of the values.
	 * @param first A value of type T.
	 * @param second Another value of type T.
	 * @throws IllegalArgumentException When first or second is null, or when
	 * 			they are equal.
	 */
	public <T> void put(Class<T> type, T first, T second) {
		if (type == null) {
			throw new InternalException("Type is null");
		}
		if (first == null || second == null) {
			throw new InternalException("First or second parameter is null.");
		}
		if (values.containsKey(type)) {
			throw new InternalException("Type " + type + " is already present.");
		}
		if (first.equals(second)) {
			throw new InternalException("First and second parameters are equal.");
		}
		values.put(type, new Tuple<T>(first, second));
	}
	
	/**
	 * Copies all prefabricated values of the specified {@link PrefabValues} to
	 * this one.
	 * 
	 * @param from Prefabricated values to be copied to this
	 * 			{@link PrefabValues}.
	 */
	public void putAll(PrefabValues from) {
		values.putAll(from.values);
	}
	
	/**
	 * Tests whether prefabricated values exist for the specified class.
	 * 
	 * @param type Class whose presence in this {@link PrefabValues} is to be
	 * 			tested.
	 * @return True if prefabricated values exist for the specified class.
	 */
	public boolean contains(Class<?> type) {
		return values.containsKey(type);
	}
	
	/**
	 * Getter for the first prefabricated value of the specified type.
	 * 
	 * @param type Class for which to return the prefabricated value.
	 * @return The first prefabricated value for the specified type.
	 */
	public <T> T getFirst(Class<T> type) {
		return getTuple(type).first;
	}
	
	/**
	 * Getter for the second prefabricated value of the specified type.
	 * 
	 * @param type Class for which to return the prefabricated value.
	 * @return The second prefabricated value for the specified type.
	 */
	public <T> T getSecond(Class<T> type) {
		return getTuple(type).second;
	}

	@SuppressWarnings("unchecked")
	private <T> Tuple<T> getTuple(Class<T> type) {
		return (Tuple<T>)values.get(type);
	}
	
	/**
	 * Returns a prefabricated value for type which is not equal to value.
	 * 
	 * @param type Class for which to return a prefabricated value.
	 * @param value An instance of type.
	 * @return A prefabricated value for type which is not equal to value.
	 */
	public Object getOther(Class<?> type, Object value) {
		if (type == null) {
			throw new InternalException("Type is null.");
		}
		
		if (value != null && !type.isAssignableFrom(value.getClass())) {
			throw new InternalException("Type does not match value.");
		}
		
		Tuple<?> tuple = values.get(type);
		if (tuple == null) {
			throw new InternalException("No prefab values for " + type + " exist.");
		}
		
		if (tuple.first.equals(value)) {
			return tuple.second;
		}
		
		return tuple.first;
	}
	
	private static class Tuple<T> {
		private T first;
		private T second;
		
		private Tuple(T first, T second) {
			this.first = first;
			this.second = second;
		}
	}
}
