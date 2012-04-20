/*
 * Copyright 2009-2012 Jan Ouwens
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
import java.util.EnumSet;
import java.util.List;

import nl.jqno.equalsverifier.util.ClassAccessor;
import nl.jqno.equalsverifier.util.FieldIterable;
import nl.jqno.equalsverifier.util.ObjectAccessor;

class ExamplesChecker<T> implements Checker {
	private final Class<T> type;
	private final ClassAccessor<T> accessor;
	private final EnumSet<Warning> warningsToSuppress;
	private final List<T> equalExamples;
	private final List<T> unequalExamples;

	public ExamplesChecker(ClassAccessor<T> accessor, EnumSet<Warning> warningsToSuppress, List<T> equalExamples, List<T> unequalExamples) {
		this.type = accessor.getType();
		this.accessor = accessor;
		this.warningsToSuppress = warningsToSuppress;
		this.equalExamples = equalExamples;
		this.unequalExamples = unequalExamples;
	}
	
	@Override
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
		final T copy = ObjectAccessor.of(reference, type).copy();
		
		checkReflexivity(reference);
		checkSymmetryEquals(reference, copy);
		checkNonNullity(reference);
		checkTypeCheck(reference);
		checkHashCode(reference, copy);
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
		
		if (!accessor.isEqualsInheritedFromObject()) {
			checkReflexivityForNonDefaultValues(reference);
			checkReflexivityForDefaultValues();
		}
	}

	private void checkReflexivityForNonDefaultValues(T reference) {
		T identicalCopy = ObjectAccessor.of(reference).copy();
		checkReflexivity(reference, identicalCopy);
	}

	private void checkReflexivityForDefaultValues() {
		if (!warningsToSuppress.contains(Warning.NULL_FIELDS)) {
			T one = accessor.getDefaultValuesObject();
			T two = ObjectAccessor.of(one).copy();
			checkReflexivity(one, two);
		}
	}

	private void checkReflexivity(T reference, T identicalCopy) {
		String identicalCopyName = Warning.IDENTICAL_COPY.toString();
		
		if (warningsToSuppress.contains(Warning.IDENTICAL_COPY)) {
			assertFalse("Unnecessary suppression: " + identicalCopyName + ". Two identical copies are equal.",
					reference.equals(identicalCopy));
		}
		else {
			assertEquals("Reflexivity: object does not equal an identical copy of itself:\n  " + reference +
					"If this is intentional, consider suppressing Warning." + identicalCopyName,
					reference, identicalCopy);
		}
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
	
	private void checkTypeCheck(T reference) {
		class SomethingElse {}
		SomethingElse somethingElse = new SomethingElse();
		try {
			reference.equals(somethingElse);
		}
		catch (ClassCastException e) {
			fail("Type-check: equals throws ClassCastException.\nAdd an instanceof or getClass() check.");
		}
		catch (Exception e) {
			fail("Type-check: equals throws " + e.getClass().getSimpleName() + ".\nAdd an instanceof or getClass() check.");
		}
	}

	private void checkHashCode(T reference, T copy) {
		if (!reference.equals(copy)) {
			return;
		}
		
		assertEquals("hashCode: hashCodes should be equal:\n  " + reference + " (" + reference.hashCode() + ")\nand\n  " + copy + " (" +  copy.hashCode() + ")",
				reference.hashCode(), copy.hashCode());
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
