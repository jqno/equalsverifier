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

import static nl.jqno.equalsverifier.util.Assert.fail;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import nl.jqno.equalsverifier.util.FieldIterable;
import nl.jqno.equalsverifier.util.Instantiator;

public class AbstractDelegationChecker<T> {
	private final Class<T> klass;
	private final Instantiator<T> instantiator;
	private final Map<Class<?>, List<?>> prefabValues;

	public AbstractDelegationChecker(Class<T> klass, Instantiator<T> instantiator, Map<Class<?>, List<?>> prefabValues) {
		this.klass = klass;
		this.instantiator = instantiator;
		this.prefabValues = prefabValues;
	}

	public void check() {
		checkAbstractDelegationInFields();

		T instance = getPrefabValue(klass);
		if (instance == null) {
			instance = instantiator.instantiate();
			instantiator.scramble(instance);
		}
		checkAbstractDelegation(instance);

		checkAbstractDelegationInSuper(klass.getSuperclass());
	}
	
	private void checkAbstractDelegationInFields() {
		for (Field field : new FieldIterable(klass)) {
			Class<?> type = field.getType();
			Object o = safelyGetInstance(type);
			if (o != null) {
				checkAbstractMethods(type, o, true);
			}
		}
	}
	
	private void checkAbstractDelegation(T instance) {
		checkAbstractMethods(klass, instance, false);
	}
	
	private <S> void checkAbstractDelegationInSuper(Class<S> superclass) {
		S instance = getPrefabValue(superclass);
		if (instance == null) {
			Instantiator<S> superInstantiator = Instantiator.forClass(superclass);
			superInstantiator.copyPrefabValues(instantiator);
			instance = superInstantiator.instantiate();
			superInstantiator.scramble(instance);
		}
		checkAbstractMethods(superclass, instance, false);
	}
	
	@SuppressWarnings("unchecked")
	private <S> S getPrefabValue(Class<?> type) {
		if (prefabValues.containsKey(type)) {
			return (S)prefabValues.get(type).iterator().next();
		}
		return null;
	}
	
	private Object safelyGetInstance(Class<?> type) {
		Object result = getPrefabValue(type);
		if (result != null) {
			return result;
		}
		try {
			return Instantiator.forClass(type).instantiate();
		}
		catch (Throwable e) {
			// If it fails for some reason, any reason, just return null.
			return null;
		}
	}
	
	private <S> void checkAbstractMethods(Class<?> instanceClass, S instance, boolean prefabPossible) {
		try {
			instance.equals(instance);
		}
		catch (AbstractMethodError e) {
			fail(buildErrorMessage(instanceClass, prefabPossible, "equals"));
		}
		catch (Throwable ignored) {
			// Skip. We only care about AbstractMethodError at this point;
			// other errors will be handled later.
		}
		
		try {
			instance.hashCode();
		}
		catch (AbstractMethodError e) {
			fail(buildErrorMessage(instanceClass, prefabPossible, "hashCode"));
		}
		catch (Throwable ignored) {
			// Skip. We only care about AbstractMethodError at this point;
			// other errors will be handled later.
		}
	}
	
	private String buildErrorMessage(Class<?> klass, boolean prefabPossible, String method) {
		StringBuilder sb = new StringBuilder();
		sb.append("Abstract delegation: ");
		sb.append(klass.getSimpleName());
		sb.append("'s ");
		sb.append(method);
		sb.append(" method delegates to an abstract method.");
		if (prefabPossible) {
			sb.append("\nAdd prefab values for ");
			sb.append(klass.getName());
			sb.append(".");
		}
		return sb.toString();
	}
}
