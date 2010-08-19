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
import nl.jqno.equalsverifier.util.InstantiatorFacade;
import nl.jqno.equalsverifier.util.PrefabValues;

public class AbstractDelegationChecker<T> {
	private final Class<T> klass;
	private final PrefabValues prefabValues;
	private final ClassAccessor<T> classAccessor;

	public AbstractDelegationChecker(ClassAccessor<T> classAccessor) {
		this.klass = classAccessor.getType();
		this.prefabValues = classAccessor.getPrefabValues();
		this.classAccessor = classAccessor;
	}

	public void check() {
		checkAbstractDelegationInFields();

		T instance = this.<T>getPrefabValue(klass);
		if (instance == null) {
			instance = classAccessor.getFirstObject();
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
		S instance = this.<S>getPrefabValue(superclass);
		if (instance == null) {
			ClassAccessor<S> superclassAccessor = ClassAccessor.of(superclass, prefabValues);
			instance = superclassAccessor.getFirstObject();
		}
		checkAbstractMethods(superclass, instance, false);
	}
	
	@SuppressWarnings("unchecked")
	private <S> S getPrefabValue(Class<?> type) {
		if (prefabValues.contains(type)) {
			return (S)prefabValues.getFirst(type);
		}
		return null;
	}
	
	private Object safelyGetInstance(Class<?> type) {
		Object result = getPrefabValue(type);
		if (result != null) {
			return result;
		}
		try {
			return InstantiatorFacade.forClass(type).instantiate();
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
