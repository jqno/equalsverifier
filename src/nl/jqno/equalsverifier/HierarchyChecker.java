/*
 * Copyright 2009-2011, 2013 Jan Ouwens
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

import static nl.jqno.equalsverifier.util.Assert.assertEquals;
import static nl.jqno.equalsverifier.util.Assert.assertFalse;
import static nl.jqno.equalsverifier.util.Assert.assertTrue;
import static nl.jqno.equalsverifier.util.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumSet;

import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.ObjectAccessor;

class HierarchyChecker<T> implements Checker {
	private final Class<T> type;
	private final ClassAccessor<T> classAccessor;
	private final EnumSet<Warning> warningsToSuppress;
	private final boolean usingGetClass;
	private final boolean hasRedefinedSuperclass;
	private final Class<? extends T> redefinedSubclass;
	private final ObjectAccessor<T> referenceAccessor;
	private final T reference;
	private final boolean typeIsFinal;

	public HierarchyChecker(ClassAccessor<T> classAccessor, EnumSet<Warning> warningsToSuppress, boolean usingGetClass, boolean hasRedefinedSuperclass, Class<? extends T> redefinedSubclass) {
		if (warningsToSuppress.contains(Warning.STRICT_INHERITANCE) && redefinedSubclass != null) {
			fail("withRedefinedSubclass and weakInheritanceCheck are mutually exclusive.");
		}
		
		this.classAccessor = classAccessor;
		this.type = classAccessor.getType();
		this.warningsToSuppress = EnumSet.copyOf(warningsToSuppress);
		this.usingGetClass = usingGetClass;
		this.hasRedefinedSuperclass = hasRedefinedSuperclass;
		this.redefinedSubclass = redefinedSubclass;
		this.referenceAccessor = classAccessor.getRedAccessor();
		this.reference = referenceAccessor.get();
		this.typeIsFinal = Modifier.isFinal(type.getModifiers());
	}
	
	@Override
	public void check() {
		checkSuperclass();
		checkSubclass();
		
		checkRedefinedSubclass();
		if (!warningsToSuppress.contains(Warning.STRICT_INHERITANCE)) {
			checkFinalEqualsMethod();
		}
	}
	
	private void checkSuperclass() {
		Class<? super T> superclass = type.getSuperclass();
		ClassAccessor<? super T> superAccessor = ClassAccessor.of(superclass, classAccessor.getPrefabValues(), true);
		if (superAccessor.isEqualsInheritedFromObject()) {
			return;
		}

		Object equalSuper = ObjectAccessor.of(reference, superclass).copy();
		
		if (hasRedefinedSuperclass || usingGetClass) {
			assertFalse("Redefined superclass:\n  " + reference + "\nshould not equal superclass instance\n  " + equalSuper + "\nbut it does.",
					reference.equals(equalSuper) || equalSuper.equals(reference));
		}
		else {
			T shallow = referenceAccessor.copy();
			ObjectAccessor.of(shallow).shallowScramble(classAccessor.getPrefabValues());
			
			assertTrue("Symmetry:\n  " + reference + "\ndoes not equal superclass instance\n  " + equalSuper,
					reference.equals(equalSuper) && equalSuper.equals(reference));
			
			assertTrue("Transitivity:\n  " + reference + "\nand\n  " + shallow +
					"\nboth equal superclass instance\n  " + equalSuper + "\nwhich implies they equal each other.",
					reference.equals(shallow) || reference.equals(equalSuper) != equalSuper.equals(shallow));
			
			assertTrue("Superclass: hashCode for\n  " + reference + " (" + reference.hashCode() +
					")\nshould be equal to hashCode for superclass instance\n  " + equalSuper + " (" + equalSuper.hashCode() + ")",
					reference.hashCode() == equalSuper.hashCode());
		}
	}
	
	private void checkSubclass() {
		if (typeIsFinal) {
			return;
		}
		
		T equalSub = referenceAccessor.copyIntoAnonymousSubclass();
		
		if (usingGetClass) {
			assertFalse("Subclass: object is equal to an instance of a trivial subclass with equal fields:\n " + reference + "\nThis should not happen when using getClass().",
					reference.equals(equalSub));
		}
		else {
			assertTrue("Subclass: object is not equal to an instance of a trivial subclass with equal fields:\n " + reference + "\nConsider making the class final.",
					reference.equals(equalSub));
		}
	}
	
	private void checkRedefinedSubclass() {
		if (typeIsFinal || redefinedSubclass == null) {
			return;
		}
		
		if (methodIsFinal("equals", Object.class)) {
			fail("Subclass: " + type.getSimpleName() + " has a final equals method.\nNo need to supply a redefined subclass.");
		}

		T redefinedSub = referenceAccessor.copyIntoSubclass(redefinedSubclass);
		assertFalse("Subclass:\n  " + reference + "\nequals subclass instance\n  " + redefinedSub,
				reference.equals(redefinedSub));
	}
	
	private void checkFinalEqualsMethod() {
		if (typeIsFinal || redefinedSubclass != null) {
			return;
		}
		
		boolean equalsIsFinal = methodIsFinal("equals", Object.class);
		boolean hashCodeIsFinal = methodIsFinal("hashCode");
		
		if (usingGetClass) {
			assertEquals("Finality: equals and hashCode must both be final or both be non-final.",
					equalsIsFinal, hashCodeIsFinal);
		}
		else {
			assertTrue("Subclass: equals is not final.\nSupply an instance of a redefined subclass using withRedefinedSubclass if equals cannot be final.",
					equalsIsFinal);
			assertTrue("Subclass: hashCode is not final.\nSupply an instance of a redefined subclass using withRedefinedSubclass if hashCode cannot be final.",
					hashCodeIsFinal);
		}
	}

	private boolean methodIsFinal(String methodName, Class<?>... parameterTypes) {
		try {
			Method method = type.getMethod(methodName, parameterTypes);
			return Modifier.isFinal(method.getModifiers());
		}
		catch (SecurityException e) {
			throw new AssertionError("Security error: cannot access equals method for class " + type);
		}
		catch (NoSuchMethodException e) {
			throw new AssertionError("Impossible: class " + type + " has no equals method.");
		}
		
	}
}
