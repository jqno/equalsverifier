/*
 * Copyright 2014 Jan Ouwens
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

import java.util.ArrayList;
import java.util.List;

import nl.jqno.equalsverifier.util.exceptions.ReflectionException;

public class ConditionalPrefabValueBuilder {
	private final Class<?> type;
	private boolean stop = false;
	private ConditionalInstantiator ci;
	private List<Object> instances = new ArrayList<Object>();
	
	public static ConditionalPrefabValueBuilder of(String fullyQualifiedClassName) {
		return new ConditionalPrefabValueBuilder(fullyQualifiedClassName);
	}
	
	private ConditionalPrefabValueBuilder(String fullyQualifiedClassName) {
		this.ci = new ConditionalInstantiator(fullyQualifiedClassName);
		this.type = ci.resolve();
		if (type == null) {
			stop = true;
		}
	}
	
	public ConditionalPrefabValueBuilder withConcreteClass(String fullyQualifiedClassName) {
		if (stop) {
			return this;
		}
		ci = new ConditionalInstantiator(fullyQualifiedClassName);
		Class<?> concreteType = ci.resolve();
		if (concreteType == null) {
			stop = true;
			return this;
		}
		if (!type.isAssignableFrom(concreteType)) {
			throw new ReflectionException("");
		}
		return this;
	}
	
	public ConditionalPrefabValueBuilder instantiate(Class<?>[] paramTypes, Object[] paramValues) {
		if (!stop) {
			validate();
			instances.add(ci.instantiate(paramTypes, paramValues));
		}
		return this;
	}
	
	public ConditionalPrefabValueBuilder callFactory(String factoryMethod, Class<?>[] paramTypes, Object[] paramValues) {
		if (!stop) {
			validate();
			instances.add(ci.callFactory(factoryMethod, paramTypes, paramValues));
		}
		return this;
	}
	
	public ConditionalPrefabValueBuilder withConstant(String constantName) {
		if (!stop) {
			validate();
			instances.add(ci.returnConstant(constantName));
		}
		return this;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addTo(PrefabValues prefabValues) {
		if (!stop) {
			prefabValues.put((Class)type, instances.get(0), instances.get(1));
		}
	}
	
	private void validate() {
		if (instances.size() >= 2) {
			throw new ReflectionException("Too many instances");
		}
	}
}
