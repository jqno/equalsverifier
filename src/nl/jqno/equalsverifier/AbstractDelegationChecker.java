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

import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.FieldIterable;
import nl.jqno.equalsverifier.util.Instantiator;
import nl.jqno.equalsverifier.util.PrefabValues;

public class AbstractDelegationChecker<T> implements Checker {
	private final Class<T> type;
	private final PrefabValues prefabValues;
	private final ClassAccessor<T> classAccessor;

	public AbstractDelegationChecker(ClassAccessor<T> classAccessor) {
		this.type = classAccessor.getType();
		this.prefabValues = classAccessor.getPrefabValues();
		this.classAccessor = classAccessor;
	}

	@Override
	public void check() {
		checkAbstractDelegationInFields();

		T instance = this.<T>getPrefabValue(type);
		if (instance == null) {
			instance = classAccessor.getRedObject();
		}
		checkAbstractDelegation(instance);

		checkAbstractDelegationInSuper(type.getSuperclass());
	}
	
	private void checkAbstractDelegationInFields() {
		for (Field field : new FieldIterable(type)) {
			Class<?> type = field.getType();
			Object o = safelyGetInstance(type);
			if (o != null) {
				checkAbstractMethods(type, o, true);
			}
		}
	}
	
	private void checkAbstractDelegation(T instance) {
		checkAbstractMethods(type, instance, false);
	}
	
	private <S> void checkAbstractDelegationInSuper(Class<S> superclass) {
		S instance = this.<S>getPrefabValue(superclass);
		if (instance == null) {
			ClassAccessor<S> superclassAccessor = ClassAccessor.of(superclass, prefabValues);
			instance = superclassAccessor.getRedObject();
		}
		checkAbstractMethods(superclass, instance, false);
	}
	
	@SuppressWarnings("unchecked")
	private <S> S getPrefabValue(Class<?> type) {
		if (prefabValues.contains(type)) {
			return (S)prefabValues.getRed(type);
		}
		return null;
	}
	
	private Object safelyGetInstance(Class<?> type) {
		Object result = getPrefabValue(type);
		if (result != null) {
			return result;
		}
		try {
			return Instantiator.of(type).instantiate();
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
	
	private String buildErrorMessage(Class<?> type, boolean prefabPossible, String method) {
		StringBuilder sb = new StringBuilder();
		sb.append("Abstract delegation: ");
		sb.append(type.getSimpleName());
		sb.append("'s ");
		sb.append(method);
		sb.append(" method delegates to an abstract method.");
		if (prefabPossible) {
			sb.append("\nAdd prefab values for ");
			sb.append(type.getName());
			sb.append(".");
		}
		return sb.toString();
	}
}
