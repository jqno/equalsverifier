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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterable to iterate over all declared fields in a class and all of its
 * superclasses.
 * 
 * @author Jan Ouwens
 */
public class FieldIterable implements Iterable<Field> {
	private final Class<?> klass;
	
	/**
	 * Constructor.
	 * 
	 * @param klass The class that contains the fields over which to iterate.
	 */
	public FieldIterable(Class<?> klass) {
		this.klass = klass;
	}

	/**
	 * Returns an iterator over all declared fields of the class and all of its
	 * superclasses.
	 * 
	 * @return An iterator over all declared fields of the class and all of its
	 * 			superclasses.
	 */
	@Override
	public Iterator<Field> iterator() {
		return createFieldList().iterator();
	}

	private List<Field> createFieldList() {
		List<Field> result = new ArrayList<Field>();
		
		Class<?> i = klass;
		while (i != null && i != Object.class) {
			for (Field field : i.getDeclaredFields()) {
				result.add(field);
			}
			i = i.getSuperclass();
		}
		
		return result;
	}
}
