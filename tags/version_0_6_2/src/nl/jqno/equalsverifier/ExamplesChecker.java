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

import static nl.jqno.equalsverifier.util.Assert.assertEquals;
import static nl.jqno.equalsverifier.util.Assert.assertFalse;
import static nl.jqno.equalsverifier.util.Assert.assertTrue;
import static nl.jqno.equalsverifier.util.Assert.fail;

import java.lang.reflect.Field;
import java.util.List;

import nl.jqno.equalsverifier.util.FieldIterable;
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
		assertFalse("Precondition: the same object appears twice:\n  " + reference,
				reference == other);
		assertFalse("Precondition: two identical objects appear:\n  " + reference,
				isIdentical(reference, other));
		assertTrue("Precondition: not all equal objects are equal:\n  " + reference + "\nand\n  " + other,
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
		assertFalse("Precondition: the same object appears twice:\n  " + reference,
				reference == other);
		assertFalse("Precondition: two objects are equal to each other:\n  " + reference,
				reference.equals(other));
	}
	
	private void checkReflexivity(T reference) {
		assertEquals("Reflexivity: object does not equal itself:\n  " + reference,
				reference, reference);
	}

	private void checkSymmetryEquals(T reference, T copy) {
		assertTrue("Symmetry: objects are not symmetric:\n  " + reference + "\nand\n  " + copy,
				reference.equals(copy) == copy.equals(reference));
	}

	private void checkSymmetryNotEquals(T reference, T other) {
		assertTrue("Symmetry: objects are not symmetric:\n  " + reference + "\nand\n  " + other,
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
		assertEquals("hashCode: hashCodes should be equal:\n  " + reference + " (" + reference.hashCode() + ")\nand\n  " + other + " (" +  other.hashCode() + ")",
				reference.hashCode(), other.hashCode());
	}
	
	private boolean isIdentical(T reference, T other) {
		for (Field field : new FieldIterable(reference.getClass())) {
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
		
		return true;
	}
}
