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

import nl.jqno.equalsverifier.points.Multiple;

import org.junit.Before;
import org.junit.Test;

public class RelaxedEqualsPreconditionTest extends EqualsVerifierTestBase {
	private Multiple a;
	private Multiple b;
	private Multiple x;
	
	@Before
	public void setup() {
		a = new Multiple(1, 2);
		b = new Multiple(2, 1);
		x = new Multiple(2, 2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void equalFirstNull() {
		EqualsVerifier.forRelaxedEqualExamples(null, b);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void equalSecondNull() {
		EqualsVerifier.forRelaxedEqualExamples(a, null);
	}
	
	@Test
	public void equalTailNull() {
		EqualsVerifier.forRelaxedEqualExamples(a, b, (Multiple[])null)
				.andUnequalExample(x)
				.verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void equalAnyNull() {
		Multiple another = new Multiple(-1, -2);
		EqualsVerifier.forRelaxedEqualExamples(a, b, another, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void unequalFirstNull() {
		EqualsVerifier.forRelaxedEqualExamples(a, b)
				.andUnequalExample(null);
	}
	
	@Test
	public void unequalTailNull() {
		EqualsVerifier.forRelaxedEqualExamples(a, b)
				.andUnequalExamples(x, (Multiple[])null)
				.verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void unequalAnyNull() {
		Multiple another = new Multiple(3, 3);
		EqualsVerifier.forRelaxedEqualExamples(a, b)
				.andUnequalExamples(x, another, null);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void incompatibleClass() {
		SubMultiple sm = new SubMultiple(1, 2);
		EqualsVerifier ev = EqualsVerifier.forRelaxedEqualExamples(sm, a)
				.andUnequalExample(x);
		verifyFailure("Precondition:\n  SubMultiple:1*2=2\nand\n  Multiple:1*2=2\nare of different classes", ev);
	}
	
	@Test
	public void equalAllSame() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(a, a)
				.andUnequalExample(x);
		verifyFailure("Precondition: the same object appears twice:\n  Multiple:1*2=2", ev);
	}
	
	@Test
	public void equalAllIdentical() {
		Multiple aa = new Multiple(1, 2);
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(a, aa)
				.andUnequalExample(x);
		verifyFailure("Precondition: two identical objects appear:\n  Multiple:1*2=2", ev);
	}
	
	@Test
	public void equalAllUnequal() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(a, x)
				.andUnequalExample(x);
		verifyFailure("Precondition: not all equal objects are equal:\n  Multiple:1*2=2\nand\n  Multiple:2*2=4", ev);
	}

	@Test
	public void unequalAllSame() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(a, b)
				.andUnequalExamples(x, x);
		verifyFailure("Precondition: the same object appears twice:\n  Multiple:2*2=4", ev);
	}
	
	@Test
	public void unequalAllEqual() {
		Multiple xx = new Multiple(2, 2);
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(a, b)
				.andUnequalExamples(x, xx);
		verifyFailure("Precondition: two objects are equal to each other:\n  Multiple:2*2=4", ev);
	}
	
	public static class SubMultiple extends Multiple {
		public SubMultiple(int a, int b) {
			super(a, b);
		}
	}
}
