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

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.points.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PreconditionTest {
	private static final String PRECONDITION = "Precondition";
	
	FinalPoint first;
	FinalPoint second;
	
	@Before
	public void setup() {
		first = new FinalPoint(1, 2);
		second = new FinalPoint(2, 3);
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
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
		EqualsVerifier<?> ev = EqualsVerifier.forExamples(first, new Point(1, 2));
		assertFailure(ev, PRECONDITION, "are of different classes", FinalPoint.class.getSimpleName(), Point.class.getSimpleName());
	}
	
	@Test
	public void fail_whenExamplesAreTheSame() {
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forExamples(first, first);
		assertFailure(ev, PRECONDITION, "the same object appears twice", FinalPoint.class.getSimpleName());
	}
	
	@Test
	public void fail_whenExamplesAreNotTheSameButEqual() {
		FinalPoint firstest = new FinalPoint(1, 2);
		EqualsVerifier<FinalPoint> ev = EqualsVerifier.forExamples(first, firstest);
		assertFailure(ev, PRECONDITION, "two objects are equal to each other", FinalPoint.class.getSimpleName());
	}
}
