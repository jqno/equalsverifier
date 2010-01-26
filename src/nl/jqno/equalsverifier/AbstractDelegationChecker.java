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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.Assert.fail;

import java.lang.reflect.Field;

import nl.jqno.instantiator.Instantiator;

public class AbstractDelegationChecker<T> {
	private final Class<T> klass;
	private final Instantiator<T> instantiator;

	public AbstractDelegationChecker(Class<T> klass, Instantiator<T> instantiator) {
		this.klass = klass;
		this.instantiator = instantiator;
	}

	public void check() {
		checkAbstractDelegationInFields();

		T instance = instantiator.instantiate();
		instantiator.scramble(instance);
		checkAbstractDelegation(instance);
	}
	
	private void checkAbstractDelegationInFields() {
		Class<?> k = klass;
		while (k != Object.class) {
			for (Field field : k.getDeclaredFields()) {
				Class<?> type = field.getType();
				Object o = safelyGetInstance(type);
				if (o != null) {
					checkAbstractMethods(type, o);
				}
			}
			k = k.getSuperclass();
		}
	}
	
	private void checkAbstractDelegation(T instance) {
		checkAbstractMethods(klass, instance);
		Class<?> i = klass;
		while (i != Object.class) {
			for (Field field : i.getDeclaredFields()) {
				try {
					Object object = field.get(instance);
					checkAbstractMethods(field.getType(), object);
				}
				catch (IllegalAccessException ignored) {
					// Skip this field.
				}
			}
			i = i.getSuperclass();
		}
	}
	
	private Object safelyGetInstance(Class<?> type) {
		try {
			return Instantiator.forClass(type).instantiate();
		}
		catch (Throwable e) {
			// 
			return null;
		}
	}
	
	private <S> void checkAbstractMethods(Class<?> instanceClass, S instance) {
		try {
			instance.equals(instance);
		}
		catch (AbstractMethodError e) {
			fail("Abstract delegation: " + 
					instanceClass.getSimpleName() +
					"'s equals method delegates to an abstract method. Add prefab values for " +
					instanceClass.getName() + ".");
		}
		catch (Throwable ignored) {
			// Skip. We only care about AbstractMethodError at this point;
			// other errors will be handled later.
		}
		
		try {
			instance.hashCode();
		}
		catch (AbstractMethodError e) {
			fail("Abstract delegation: " + 
					instanceClass.getSimpleName() +
					"'s hashCode method delegates to an abstract method. Add prefab values for " +
					instanceClass.getName() + ".");
		}
		catch (Throwable ignored) {
			// Skip. We only care about AbstractMethodError at this point;
			// other errors will be handled later.
		}
	}
}
