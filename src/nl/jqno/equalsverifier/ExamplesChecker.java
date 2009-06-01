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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import nl.jqno.instantiator.Instantiator;

class ExamplesChecker<T> {
	private final List<T> examples;
	private final Instantiator<T> instantiator;

	public ExamplesChecker(Instantiator<T> instantiator, List<T> examples) {
		this.examples = examples;
		this.instantiator = instantiator;
	}
	
	public void check() {
		for (int i = 0; i < examples.size(); i++) {
			checkSingle(examples.get(i));
			
			for (int j = i + 1; j < examples.size(); j++) {
				checkDouble(examples.get(i), examples.get(j));
			}
		}
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
		assertNotSame("Precondition: the same object (" + reference + ") appears twice.",
				reference, other);
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
}
