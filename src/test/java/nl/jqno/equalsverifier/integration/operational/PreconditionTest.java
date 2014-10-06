/*
 * Copyright 2009-2010, 2013-2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;

import org.junit.Before;
import org.junit.Test;

public class PreconditionTest extends IntegrationTestBase {
	private static final String PRECONDITION = "Precondition";
	
	FinalPoint first;
	FinalPoint second;
	
	@Before
	public void setup() {
		first = new FinalPoint(1, 2);
		second = new FinalPoint(2, 3);
	}
	
	@Test
	public void fail_whenFirstExampleIsNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("First example is null.");
		
		EqualsVerifier.forExamples(null, second);
	}
	
	@Test
	public void fail_whenSecondExampleIsNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Second example is null.");
		
		EqualsVerifier.forExamples(first, null);
	}
	
	@Test
	public void succeed_whenTheVarargsArrayIsNull() {
		EqualsVerifier.forExamples(first, second, (FinalPoint[])null).verify();
	}
	
	@Test
	public void fail_whenAVarargsParameterIsNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("One of the examples is null.");
		
		FinalPoint another = new FinalPoint(3, 4);
		EqualsVerifier.forExamples(first, second, another, null);
	}
	
	@Test
	public void fail_whenExamplesAreOfIncompatibleTypes() {
		expectFailure(PRECONDITION, "are of different classes", FinalPoint.class.getSimpleName(), Point.class.getSimpleName());
		EqualsVerifier.forExamples(first, new Point(1, 2))
				.verify();
	}
	
	@Test
	public void fail_whenExamplesAreTheSame() {
		expectFailure(PRECONDITION, "the same object appears twice", FinalPoint.class.getSimpleName());
		EqualsVerifier.forExamples(first, first)
				.verify();
	}
	
	@Test
	public void fail_whenExamplesAreNotTheSameButEqual() {
		FinalPoint equalToFirst = new FinalPoint(1, 2);
		expectFailure(PRECONDITION, "two objects are equal to each other", FinalPoint.class.getSimpleName());
		EqualsVerifier.forExamples(first, equalToFirst)
				.verify();
	}
}
