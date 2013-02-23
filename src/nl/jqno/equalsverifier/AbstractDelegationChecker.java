/*
 * Copyright 2010-2011, 2013 Jan Ouwens
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
import nl.jqno.equalsverifier.util.Formatter;
import nl.jqno.equalsverifier.util.Instantiator;
import nl.jqno.equalsverifier.util.PrefabValues;

class AbstractDelegationChecker<T> implements Checker {
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

		checkAbstractDelegationInSuper();
	}
	
	private void checkAbstractDelegationInFields() {
		for (Field field : FieldIterable.of(type)) {
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
	
	private void checkAbstractDelegationInSuper() {
		Class<? super T> superclass = type.getSuperclass();
		ClassAccessor<? super T> superAccessor = classAccessor.getSuperAccessor();
		
		boolean equalsIsAbstract = superAccessor.isEqualsAbstract();
		boolean hashCodeIsAbstract = superAccessor.isHashCodeAbstract();
		if (equalsIsAbstract != hashCodeIsAbstract) {
			fail(buildAbstractMethodInSuperErrorMessage(superclass, equalsIsAbstract));
		}
		if (equalsIsAbstract && hashCodeIsAbstract) {
			return;
		}

		Object instance = getPrefabValue(superclass);
		if (instance == null) {
			instance = superAccessor.getRedObject();
		}
		checkAbstractMethods(superclass, instance, false);
	}

	private Formatter buildAbstractMethodInSuperErrorMessage(Class<?> type, boolean isEqualsAbstract) {
		return Formatter.of("Abstract delegation: %%'s %% method is abstract, but %% is not.\nBoth should be either abstract or concrete.",
				type.getSimpleName(), (isEqualsAbstract ? "equals" : "hashCode"), (isEqualsAbstract ? "hashCode" : "equals"));
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
		catch (Exception ignored) {
			// If it fails for some reason, any reason, just return null.
			return null;
		}
	}
	
	private <S> void checkAbstractMethods(Class<?> instanceClass, S instance, boolean prefabPossible) {
		try {
			instance.equals(instance);
		}
		catch (AbstractMethodError e) {
			fail(buildAbstractDelegationErrorMessage(instanceClass, prefabPossible, "equals", e.getMessage()));
		}
		catch (Exception ignored) {
			// Skip. We only care about AbstractMethodError at this point;
			// other errors will be handled later.
		}
		
		try {
			instance.hashCode();
		}
		catch (AbstractMethodError e) {
			fail(buildAbstractDelegationErrorMessage(instanceClass, prefabPossible, "hashCode", e.getMessage()));
		}
		catch (Exception ignored) {
			// Skip. We only care about AbstractMethodError at this point;
			// other errors will be handled later.
		}
	}
	
	private Formatter buildAbstractDelegationErrorMessage(Class<?> type, boolean prefabPossible, String method, String originalMessage) {
		Formatter prefabFormatter = Formatter.of("\nAdd prefab values for %%.", type.getName());
		
		return Formatter.of("Abstract delegation: %%'s %% method delegates to an abstract method:\n %%%%",
				type.getSimpleName(), method, originalMessage, prefabPossible ? prefabFormatter.format() : "");
	}
}
