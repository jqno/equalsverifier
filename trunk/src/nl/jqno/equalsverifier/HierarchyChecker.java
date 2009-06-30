/*
 * Copyright 2009 Jan Ouwens
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.jqno.instantiator.Instantiator;

class HierarchyChecker<T> {
	private final Class<T> klass;
	private final Instantiator<T> instantiator;
	private final boolean klassIsFinal;
	
	private T reference;
	private T other;
	
	private Class<? extends T> redefinedSubclass;
	private boolean hasRedefinedSuperclass = false;
	private boolean weakInheritanceCheck = false;

	public HierarchyChecker(Instantiator<T> instantiator) {
		this.instantiator = instantiator;
		this.klass = instantiator.getKlass();
		
		klassIsFinal = Modifier.isFinal(klass.getModifiers());
	}
	
	public void setRedefinedSubclass(Class<? extends T> redefinedSubclass) {
		if (weakInheritanceCheck) {
			fail("withRedefinedSubclass and weakInheritanceCheck are mutually exclusive.");
		}
		this.redefinedSubclass = redefinedSubclass;
	}
	
	public void hasRedefinedSuperclass() {
		this.hasRedefinedSuperclass = true;
	}
	
	public void weakInheritanceCheck() {
		if (redefinedSubclass != null) {
			fail("withRedefinedSubclass and weakInheritanceCheck are mutually exclusive.");
		}
		this.weakInheritanceCheck = true;
	}
	
	public void check() {
		generateExamples();
		
		checkSuperclass();
		checkSubclass();
		
		checkRedefinedSubclass();
		if (!weakInheritanceCheck) {
			checkFinalEqualsMethod();
		}
	}
	
	private void checkSuperclass() {
		Class<? super T> superclass = klass.getSuperclass();
		if (redefinedSubclass != null || superclass == Object.class) {
			return;
		}

		Object equalSuper = instantiateSuperclass(superclass);
		
		if (hasRedefinedSuperclass) {
			assertFalse("Redefined superclass: " + reference + " may not equal " + equalSuper + ", but it does.",
					reference.equals(equalSuper));
		}
		else {
			T shallow = instantiator.cloneFrom(reference);
			instantiator.shallowScramble(shallow);
			
			assertTrue("Symmetry: " + reference + " does not equal " + equalSuper + ".",
					reference.equals(equalSuper) && equalSuper.equals(reference));
			
			assertTrue("Transitivity: " + reference + " and " + shallow +
					" both equal " + equalSuper + ", which implies they equal each other.",
					reference.equals(shallow) || reference.equals(equalSuper) != equalSuper.equals(shallow));
			
			assertTrue("Superclass: hashCode for " + reference + " should be equal to hashCode for " + equalSuper + ".",
					reference.hashCode() == equalSuper.hashCode());
		}
	}
	
	@SuppressWarnings("unchecked")
	private <S> Object instantiateSuperclass(Class<S> superclass) {
		Instantiator superInstantiator = Instantiator.forClass(superclass);
		return superInstantiator.cloneFrom(reference);
	}

	private void checkSubclass() {
		if (klassIsFinal) {
			return;
		}
		
		T equalSub = instantiator.cloneToSubclass(reference);
		assertTrue("Subclass: " + reference + " is not equal to an instance of a trivial subclass with equal fields. (Consider making the class final.)",
				reference.equals(equalSub));
	}
	
	private void checkRedefinedSubclass() {
		if (klassIsFinal || redefinedSubclass == null) {
			return;
		}
		
		if (methodIsFinal("equals", Object.class)) {
			fail("Subclass: " + klass.getSimpleName() + " has a final equals method; don't need to supply a redefined subclass.");
		}

		T redefinedSub = instantiator.cloneToSubclass(reference, redefinedSubclass);
		assertFalse("Subclass: " + reference + " equals " + redefinedSub + ".",
				reference.equals(redefinedSub));
	}
	
	private void checkFinalEqualsMethod() {
		if (klassIsFinal || redefinedSubclass != null) {
			return;
		}
		
		assertTrue("Subclass: equals is not final. Supply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.",
				methodIsFinal("equals", Object.class));
		assertTrue("Subclass: hashCode is not final. Supply an instance of a redefined subclass using withRedefinedSubclass if hashCode cannot be final.",
				methodIsFinal("hashCode"));
	}

	private boolean methodIsFinal(String methodName, Class<?>... parameterTypes) {
		try {
			Method method = klass.getMethod(methodName, parameterTypes);
			return Modifier.isFinal(method.getModifiers());
		}
		catch (SecurityException e) {
			throw new AssertionError("Security error: cannot access equals method for class " + klass);
		}
		catch (NoSuchMethodException e) {
			throw new AssertionError("Impossible: class " + klass + " has no equals method.");
		}
		
	}
	
	private void generateExamples() {
		reference = instantiator.instantiate();
		instantiator.scramble(reference);
		other = instantiator.cloneFrom(reference);
		instantiator.scramble(other);
	}
}
