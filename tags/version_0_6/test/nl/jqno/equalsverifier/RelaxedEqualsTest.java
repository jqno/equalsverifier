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

public class RelaxedEqualsTest extends EqualsVerifierTestBase {
	private Multiple a;
	private Multiple b;
	private Multiple x;
	
	@Before
	public void setup() {
		a = new Multiple(1, 2);
		b = new Multiple(2, 1);
		x = new Multiple(2, 2);
	}

	@Test
	public void multipleFails() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forExamples(a, b);
		verifyFailure("Precondition: two objects are equal to each other", ev);
	}
	
	@Test
	public void multipleSuccess() {
		EqualsVerifier.forRelaxedEqualExamples(a, b)
				.andUnequalExample(x)
				.verify();
	}
	
	@Test
	public void mixUpEqualAndUnequalExamples() {
		EqualsVerifier<Multiple> ev = EqualsVerifier.forRelaxedEqualExamples(a, b)
				.andUnequalExamples(a);
		verifyFailure("Precondition: the same object appears twice:\n  Multiple:1*2=2", ev);
	}
}
