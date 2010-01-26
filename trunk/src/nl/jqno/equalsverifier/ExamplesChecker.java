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

import static nl.jqno.equalsverifier.Assert.assertEquals;
import static nl.jqno.equalsverifier.Assert.assertFalse;
import static nl.jqno.equalsverifier.Assert.assertTrue;
import static nl.jqno.equalsverifier.Assert.fail;

import java.lang.reflect.Field;
import java.util.List;

import nl.jqno.equalsverifier.util.Instantiator;

class ExamplesChecker<T> {
	private final Instantiator<T> instantiator;
	private final List<T> equalExamples;
	private final List<T> unequalExamples;

	public ExamplesChecker(Instantiator<T> instantiator, List<T> equalExamples, List<T> unequalExamples) {
		this.instantiator = instantiator;
		this.equalExamples = equalExamples;
		this.unequalExamples = unequalExamples;
	}
	
	public void check() {
		for (int i = 0; i < equalExamples.size(); i++) {
			T reference = equalExamples.get(i);
			checkSingle(reference);
			
			for (int j = i + 1; j < equalExamples.size(); j++) {
				T other = equalExamples.get(j);
				checkEqualButNotIdentical(reference, other);
				checkSymmetryEquals(reference, other);
				checkHashCode(reference, other);
			}
			
			for (T other : unequalExamples) {
				checkDouble(reference, other);
			}
		}
		
		for (int i = 0; i < unequalExamples.size(); i++) {
			T reference = unequalExamples.get(i);
			checkSingle(reference);
			
			for (int j = i + 1; j < unequalExamples.size(); j++) {
				T other = unequalExamples.get(j);
				checkDouble(reference, other);
			}
		}
	}
	
	private void checkEqualButNotIdentical(T reference, T other) {
		assertFalse("Precondition: the same object (" + reference + ") appears twice.",
				reference == other);
		assertFalse("Precondition: two identical objects (" + reference + ") appear.",
				isIdentical(reference, other));
		assertTrue("Precondition: not all equal objects are equal (" + reference + ", " + other + ").",
				reference.equals(other));
	}

	private void checkSingle(T reference) {
		final T copy = createCopy(reference);
		
		checkReflexivity(reference);
		checkSymmetryEquals(reference, copy);
		checkNonNullity(reference);
		checkHashCode(reference, copy);
	}

	private T createCopy(T reference) {
		if (instantiator.getKlass() == reference.getClass()) {
			return instantiator.cloneFrom(reference);
		}
		else {
			@SuppressWarnings("unchecked")
			Instantiator<T> subInstantiator = Instantiator.forClass((Class<T>)reference.getClass());
			return subInstantiator.cloneFrom(reference);
		}
	}
	
	private void checkDouble(T reference, T other) {
		checkNotEqual(reference, other);
		checkSymmetryNotEquals(reference, other);
	}
	
	private void checkNotEqual(T reference, T other) {
		assertFalse("Precondition: the same object (" + reference + ") appears twice.",
				reference == other);
		assertFalse("Precondition: two objects are equal to each other (" + reference + ").",
				reference.equals(other));
	}
	
	private void checkReflexivity(T reference) {
		assertEquals("Reflexivity: " + reference + " does not equal itself.",
				reference, reference);
	}

	private void checkSymmetryEquals(T reference, T copy) {
		assertTrue("Symmetry: " + reference + " is not symmetric to " + copy + ".",
				reference.equals(copy) == copy.equals(reference));
	}

	private void checkSymmetryNotEquals(T reference, T other) {
		assertTrue("Symmetry: " + reference + " is not symmetric to " + other + ".",
				reference.equals(other) == other.equals(reference));
	}

	private void checkNonNullity(T reference) {
		try {
			boolean nullity = reference.equals(null);
			assertFalse("Non-nullity: true returned for null value", nullity);
		}
		catch (NullPointerException e) {
			fail("Non-nullity: NullPointerException thrown");
		}
	}

	private void checkHashCode(T reference, T other) {
		assertEquals("hashCode: hashCode for " + reference + " should be equal to hashCode for " + other + ".",
				reference.hashCode(), other.hashCode());
	}
	
	private boolean isIdentical(T reference, T other) {
		Class<?> i = reference.getClass();
		while (i != Object.class) {
			for (Field field : i.getDeclaredFields()) {
				try {
					field.setAccessible(true);
					if (!field.get(reference).equals(field.get(other))) {
						return false;
					}
				}
				catch (IllegalArgumentException e) {
					return false;
				}
				catch (IllegalAccessException e) {
					return false;
				}
			}
			i = i.getSuperclass();
		}
		
		return true;
	}
}
