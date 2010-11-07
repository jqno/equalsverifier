/*
 * Copyright 2009-2010 Jan Ouwens
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

import static nl.jqno.equalsverifier.Helper.assertFailure;
import nl.jqno.equalsverifier.points.Multiple;

import org.junit.Before;
import org.junit.Test;

public class RelaxedEqualsPreconditionTest {
	private static final String PRECONDITION = "Precondition";
	private static final String DIFFERENT_CLASSES = "are of different classes";
	private static final String TWO_IDENTICAL_OBJECTS_APPEAR = "two identical objects appear";
	private static final String NOT_ALL_EQUAL = "not all equal objects are equal";
	private static final String OBJECT_APPEARS_TWICE = "the same object appears twice";
	private static final String TWO_OBJECTS_ARE_EQUAL = "two objects are equal to each other";
	
	private Multiple red;
	private Multiple black;
	private Multiple green;
	
	@Before
	public void setup() {
		red = new Multiple(1, 2);
		black = new Multiple(2, 1);
		green = new Multiple(2, 2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void equalFirstNull() {
		EqualsVerifier.forRelaxedEqualExamples(null, black);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void equalSecondNull() {
		EqualsVerifier.forRelaxedEqualExamples(red, null);
	}
	
	@Test
	public void equalMoreNull() {
		EqualsVerifier.forRelaxedEqualExamples(red, black, (Multiple[])null)
				.andUnequalExample(green)
				.verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void equalAnyNull() {
		Multiple another = new Multiple(-1, -2);
		EqualsVerifier.forRelaxedEqualExamples(red, black, another, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void unequalFirstNull() {
		EqualsVerifier.forRelaxedEqualExamples(red, black)
				.andUnequalExample(null);
	}
	
	@Test
	public void unequalMoreNull() {
		EqualsVerifier.forRelaxedEqualExamples(red, black)
				.andUnequalExamples(green, (Multiple[])null)
				.verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void unequalAnyNull() {
		Multiple another = new Multiple(3, 3);
		EqualsVerifier.forRelaxedEqualExamples(red, black)
				.andUnequalExamples(green, another, null);
	}

	@Test
	public void incompatibleClass() {
		SubMultiple sm = new SubMultiple(1, 2);
		EqualsVerifier<?> ev = EqualsVerifier.forRelaxedEqualExamples(sm, red)
				.andUnequalExample(green);
		assertFailure(ev, PRECONDITION, DIFFERENT_CLASSES, SubMultiple.class.getSimpleName(), Multiple.class.getSimpleName());
	}
	
	@Test
	public void equalAllSame() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(red, red)
				.andUnequalExample(green);
		assertFailure(ev, PRECONDITION, OBJECT_APPEARS_TWICE, Multiple.class.getSimpleName());
	}
	
	@Test
	public void equalAllIdentical() {
		Multiple aa = new Multiple(1, 2);
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(red, aa)
				.andUnequalExample(green);
		assertFailure(ev, PRECONDITION, TWO_IDENTICAL_OBJECTS_APPEAR, Multiple.class.getSimpleName());
	}
	
	@Test
	public void equalAllUnequal() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(red, green)
				.andUnequalExample(green);
		assertFailure(ev, PRECONDITION, NOT_ALL_EQUAL, "and", Multiple.class.getSimpleName());
	}

	@Test
	public void unequalAllSame() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(red, black)
				.andUnequalExamples(green, green);
		assertFailure(ev, PRECONDITION, OBJECT_APPEARS_TWICE, Multiple.class.getSimpleName());
	}
	
	@Test
	public void unequalAllEqual() {
		Multiple xx = new Multiple(2, 2);
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(red, black)
				.andUnequalExamples(green, xx);
		assertFailure(ev, PRECONDITION, TWO_OBJECTS_ARE_EQUAL, Multiple.class.getSimpleName());
	}
	
	public static class SubMultiple extends Multiple {
		public SubMultiple(int a, int b) {
			super(a, b);
		}
	}
}
