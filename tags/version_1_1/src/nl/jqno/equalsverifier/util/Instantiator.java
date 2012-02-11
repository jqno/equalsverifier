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

import java.lang.reflect.Modifier;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * Instantiates objects of a given class.
 * 
 * @param <T> {@link Instantiator} instantiates objects of this class, or of an
 * 				anonymous subclass of this class.
 * 
 * @author Jan Ouwens
 */
public class Instantiator<T> {
	private final Class<T> type;
	private Objenesis objenesis;

	/**
	 * Factory method.
	 * 
	 * @param <T> The class on which {@link Instantiator} operates.
	 * @param type The class on which {@link Instantiator} operates. Should be
	 * 				the same as T.
	 * @return An {@link Instantiator} for {@link #type}.
	 */
	public static <T> Instantiator<T> of(Class<T> type) {
		if (Modifier.isAbstract(type.getModifiers())) {
			return new Instantiator<T>(createDynamicSubclass(type));
		}
		return new Instantiator<T>(type);
	}
	
	/**
	 * Private constructor. Call {@link #of(Class)} to instantiate.
	 */
	private Instantiator(Class<T> type) {
		this.type = type;
		this.objenesis = new ObjenesisStd();
	}
	
	/**
	 * Instantiates an object of type T.
	 * 
	 * @return An object of type T.
	 */
	public T instantiate() {
		@SuppressWarnings("unchecked")
		T result = (T)objenesis.newInstance(type);
		return result;
	}
	
	/**
	 * Instantiates an anonymous subclass of T. The subclass is
	 * generated dynamically.
	 * 
	 * @return An instance of an anonymous subclass of T.
	 */
	public T instantiateAnonymousSubclass() {
		Class<T> proxyClass = createDynamicSubclass(type);
		@SuppressWarnings("unchecked")
		T instance = (T)objenesis.newInstance(proxyClass);
		return instance;
	}
	
	private static <S> Class<S> createDynamicSubclass(Class<S> superclass) {
		Enhancer e = new Enhancer() {
			@Override
			@SuppressWarnings("rawtypes")
        	protected void filterConstructors(Class sc, List constructors) {
        		// Don't filter
        	}
        };
        
		if (superclass.isInterface()) {
			e.setSuperclass(Object.class);
			e.setInterfaces(new Class[] { superclass });
		}
		else {
			e.setSuperclass(superclass);
		}
		
		e.setCallbackType(NoOp.class);
		@SuppressWarnings("unchecked")
		Class<S> proxyClass = e.createClass();
		return proxyClass;
	}
}
